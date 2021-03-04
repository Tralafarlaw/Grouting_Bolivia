package com.amuyu.groutingbolivia.ui.cart

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.adapters.ConfirmAdapter
import com.amuyu.groutingbolivia.model.ItemVenta
import com.amuyu.groutingbolivia.model.Venta
import com.amuyu.groutingbolivia.utils.ImageUtils
import com.amuyu.groutingbolivia.utils.PDFUtils
import com.amuyu.movil_inv.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.converter.DefaultConverter
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printer.Printer
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import kotlinx.android.synthetic.main.fragment_confirm.view.*
import java.text.SimpleDateFormat


class ConfirmFragment : Fragment() {
    private val mViewModel: MainViewModel by activityViewModels()
    private lateinit var printing: Printing
    var db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val mData = mViewModel._VentaSelected.value!!
        val v = inflater.inflate(R.layout.fragment_confirm, container, false)
        v.final_fecha.text = "Fecha: "+SimpleDateFormat("dd/MM/yyyy").format(mData.fecha.toDate())
        v.final_asesor.text = "Asesor: "+FirebaseAuth.getInstance().currentUser?.displayName
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection("USUARIOS").document(it.uid).get().addOnSuccessListener { asesor ->
                if (asesor.exists()){
                    if (asesor.data?.get("celular")!=null) {
                        v.final_asesor_cel.text = "Cel: " + asesor.data?.get("celular").toString()
                    }else{
                        v.final_asesor_cel.text = "Cel: No disponible"
                    }
                }else{
                    v.final_asesor_cel.text = "Cel: "+"no disponible"
                }
            }
        }
        mViewModel.clientes.observe(viewLifecycleOwner, Observer {
            try {
                val aux = (it.filter { cliente -> cliente.id == mData.cliente })[0]
                v.final_cliente.text =
                    "Cliente: ${if (mData.nombre != null) mData.nombre else aux.direccion}"
//                v.final_direccion.text = "Direccion: ${aux.direccion}"
            } catch (e: Exception) {
                v.final_cliente.text = "Cliente: ${mData.cliente}"
//                v.final_direccion.text = "Direccion: "
            }
        })
        v.final_observaciones.text = "Observaciones:" + mData.observaciones
        v.final_descuento.text = "Descuento: %.2f".format(mData.descuento)
        val subtotal = mData.getSubTotal()
        v.final_subtotal.text = "Subtotal: %.2f".format(subtotal)
        v.final_total.text = "Total: %.2f".format(subtotal - mData.descuento)
        val rv = v.final_rv
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ConfirmAdapter()
        rv.adapter = adapter
        mViewModel.productos.observe(viewLifecycleOwner, Observer {
            val aux = arrayListOf<ItemVenta>()
            for (venta in mData.items) {
                aux.add(ItemVenta(
                    nombre = venta.nombre,
                    cantidad = venta.cantidad,
                    precio = venta.precio,
                    descuento = venta.descuento,
                    producto = venta.producto
                ))
            }
            Log.d("SSSSS", aux.toString())
            adapter.update(aux)
        })
        v.final_final.setOnClickListener {
            try {
                (requireParentFragment().requireParentFragment() as DialogFragment).dismiss()
            }catch (e: Exception){
                (requireParentFragment() as DialogFragment).dismiss()
            }
        }
        v.final_compartir.setOnClickListener {
            val uri = PDFUtils.layoutToPDF(v.final_to_pdf, mData.numero, -1)
           if(uri != null) {
               requireContext().grantUriPermission(requireContext().packageName,
                   uri,
                   Intent.FLAG_GRANT_READ_URI_PERMISSION);
               val intent = ShareCompat.IntentBuilder.from(requireActivity())
                   .setType("application/pdf")
                   .setStream(uri)
                   .setChooserTitle("Elija una aplicacion")
                   .createChooserIntent()
                   .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
               startActivity(intent)
           }else{
               Toast.makeText(requireContext(),
                   "Ocurrio un error porfavor intente de nuevo",
                   Toast.LENGTH_SHORT).show()
           }
        }
        v.final_imprimir.setOnClickListener {
            val bmp = PDFUtils.viewToImage(v.final_to_pdf)


            if(!Printooth.hasPairedPrinter()){
                startActivityForResult(Intent(requireContext(), ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            }else{
                Printooth.printer(MyPrinter()).print(arrayListOf(ImagePrintable.Builder(tograyScale(
                    bmp)).build()))
            }
        }
        return v
    }
    fun tograyScale(srcImage: Bitmap): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(),
            srcImage.getHeight(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()

        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.setColorFilter(ColorMatrixColorFilter(cm))
        canvas.drawBitmap(srcImage, 0f, 0f, paint)
        return resize(bmpGrayscale)
    }
    fun resize(img: Bitmap, maxWidth: Int = 380, maxHeight: Int = Int.MAX_VALUE): Bitmap {
        var image = img
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun Venta.getSubTotal (): Double{
        var aux = 0.0
        this.items.forEach {
            aux+= ((it.cantidad*it.precio)-it.descuento)
        }
        return aux
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            ConfirmFragment().apply {}
    }

}
class Pri: PrintingImagesHelper {
    override fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        return ImageUtils.decodeBitmap(bitmap)!!
    }
}

open class MyPrinter : Printer() {
    override fun useConverter(): Converter = DefaultConverter()

    override fun initLineSpacingCommand(): ByteArray = byteArrayOf(0x1B, 0x33)

    override fun initInitPrinterCommand(): ByteArray = byteArrayOf(0x1b, 0x40)

    override fun initJustificationCommand(): ByteArray = byteArrayOf(27, 97)

    override fun initFontSizeCommand(): ByteArray = byteArrayOf(29, 33)

    override fun initEmphasizedModeCommand(): ByteArray = byteArrayOf(27, 69) //1 on , 0 off

    override fun initUnderlineModeCommand(): ByteArray = byteArrayOf(27, 45) //1 on , 0 off

    override fun initCharacterCodeCommand(): ByteArray = byteArrayOf(27, 116)

    override fun initFeedLineCommand(): ByteArray = byteArrayOf(27, 100)

    override fun initPrintingImagesHelper(): PrintingImagesHelper = Pri()



}
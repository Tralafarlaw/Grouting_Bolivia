package com.amuyu.groutingbolivia.ui.cart

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.amuyu.groutingbolivia.utils.PDFUtils
import com.google.common.reflect.Reflection.getPackageName
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_confirm.view.*
import java.text.SimpleDateFormat


class ConfirmFragment : Fragment() {
    private val mViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val mData = mViewModel._VentaSelected.value!!
        val v = inflater.inflate(R.layout.fragment_confirm, container, false)
        v.final_fecha.text = "Fecha: "+SimpleDateFormat("dd/MM/yyyy").format(mData.fecha.toDate())
        v.final_asesor.text = "Asesor: "+FirebaseAuth.getInstance().currentUser?.displayName
        v.final_nit.text = "Nro Fact: ${mData.numero}"
        mViewModel.clientes.observe(viewLifecycleOwner, Observer {
            try {
                val aux = (it.filter { cliente -> cliente.id == mData.cliente })[0]
                v.final_cliente.text =
                    "Cliente: ${if (mData.nombre != null) mData.nombre else aux.direccion}"
                v.final_direccion.text = "Direccion: ${aux.direccion}"
            } catch (e: Exception) {
                v.final_cliente.text = "Cliente: ${mData.cliente}"
                v.final_direccion.text = "Direccion: "
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
                    producto = try {
                        it.filter { ss -> ss.mProductoID == venta.producto }[0].nombre
                    } catch (e: Exception) {
                        "Producto no Encotrado"
                    },
                    cantidad = venta.cantidad,
                    precio = venta.precio,
                    descuento = venta.descuento
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
               requireContext().grantUriPermission(requireContext().packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
               val intent = ShareCompat.IntentBuilder.from(requireActivity())
                   .setType("application/pdf")
                   .setStream(uri)
                   .setChooserTitle("Elija una aplicacion")
                   .createChooserIntent()
                   .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
               startActivity(intent)
           }else{
               Toast.makeText(requireContext(), "Ocurrio un error porfavor intente de nuevo", Toast.LENGTH_SHORT).show()
           }
        }
        v.final_imprimir.setOnClickListener {
            val uri = PDFUtils.layoutToPDF(v.final_to_pdf, mData.numero, 0)
            if(uri != null ){
                requireContext().grantUriPermission(requireContext().packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
            }else{
                Toast.makeText(requireContext(), "Ocurrio un error porfavor intente de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
        return v
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
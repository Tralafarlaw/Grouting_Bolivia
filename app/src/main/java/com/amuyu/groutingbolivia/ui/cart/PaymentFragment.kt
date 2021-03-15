package com.amuyu.groutingbolivia.ui.cart

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.*
import com.amuyu.movil_inv.model.Const
import com.andreseko.SweetAlert.SweetAlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_payment.view.*
import java.util.*

private const val TAG = "PaymentFragment"
class PaymentFragment : Fragment() {
    private val mViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v =  inflater.inflate(R.layout.fragment_payment, container, false)
        v.confirm_btn.setOnClickListener {
            val ss = v.confirm_fact.text
            val factNum = ss.toString()
            val tipo = when(v.confirm_group.checkedButtonId){
                R.id.confirm_credito -> TipoVenta.CREDITO.i
                R.id.confirm_efectivo -> TipoVenta.EFECTIVO.i
                else -> TipoVenta.OTRO.i
            }
            val obs = v.confirm_observaciones.text.toString()
            val desc = mViewModel.getDesc()

            if(tipo == TipoVenta.CREDITO.i){
                val ll = LinearLayout(requireContext())
                val input = EditText(requireContext()).apply {
                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                    hint = "Primer Pago (Puede ser 0)"
                }
                ll.addView(input)
                AlertDialog.Builder(requireContext()).setTitle("Primer Pago").setView(ll)
                    .setPositiveButton("Aceptar",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (!input.text.isNullOrEmpty()) {
                                commitVenta(firstPay = input.text.toString().toDouble(),
                                    descuento = desc,
                                    tipoVenta = tipo,
                                    factura = factNum,
                                    observaciones = obs)
                            } else {
                                commitVenta(desc,
                                    tipoVenta = tipo,
                                    factura = factNum,
                                    observaciones = obs)
                            }
                        })
                    .setNegativeButton("Saltar",
                        DialogInterface.OnClickListener { dialogInterface, _ ->
                            commitVenta(desc,
                                tipoVenta = tipo,
                                factura = factNum,
                                observaciones = obs)
                            dialogInterface.cancel()
                        }).create().show()
                return@setOnClickListener
            }else{
                commitVenta(desc, tipoVenta = tipo, factura = factNum, observaciones = obs)
            }
        }

        return v
    }
    fun commitVenta(
        descuento: Double = 0.0,
        firstPay: Double = 0.0,
        tipoVenta: Int,
        factura: String,
        observaciones: String
    ){
        val loading = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Registrando...")
        loading.show()
        val mProductos = mViewModel.getCartItems()
        var cliente: String? = null
        var clienteId: String? = null
        var dni: String? = null
        arguments?.let {
            cliente = it.getString("cliente_name", try {
                (it.getSerializable("cliente") as Cliente?)!!.nombre
            } catch (e: Exception) {
                null
            })
            dni = it.getString("dni", try {
                (it.getSerializable("cliente") as Cliente?)!!.nit
            } catch (e: Exception) {
                null
            })
            clienteId = try {
                (it.getSerializable("cliente") as Cliente).id
            } catch (e: Exception){
                ""
            }
        }
        if(clienteId == "" && tipoVenta == 2){
            Toast.makeText(requireContext(),
                "No se puede vender a credito a un cliente no registrado",
                Toast.LENGTH_LONG).show()
            return
        }
        val id = FirebaseFirestore.getInstance().collection(Const.RegistrosT).document().id
        val idCorrelativo = FirebaseFirestore.getInstance().collection(Const.Correlativo).document("2021").id
        var numAlm1 = 0
        var gestion = 20
        val correlativo = FirebaseFirestore.getInstance().collection(Const.Correlativo).document(idCorrelativo)
        val addOnSuccessListener = correlativo.get().addOnSuccessListener { document ->
            if (document.exists()) {
                numAlm1 = document.data?.get("num_alm1").toString().toInt()
                gestion = document.data?.get("gestion").toString().toInt()
            }
//            Log.d("HHHT", "a${gestion}")
//            Log.d("HHHT", "f${gestion}")
            numAlm1 +=  1
            var acuentaTotal = firstPay
            if (tipoVenta == 1 || tipoVenta == 3){
                acuentaTotal = getTotal(mProductos)
            }
            val mVenta = Venta(
                numero = factura,
                numeroCorrelativo = completarNumero(gestion, numAlm1),
                acuenta = acuentaTotal,
                cliente = clienteId,
                nombre = cliente,
                dni = dni,
                fecha = Timestamp.now(),
                asesor = FirebaseAuth.getInstance().currentUser!!.displayName ?: "error",
                asesorId = FirebaseAuth.getInstance().uid ?: "error",
                items = mProductos,
                tipo = tipoVenta,
                descuento = descuento,
                observaciones = observaciones
            )
            if (mVenta.items.size != 0){
                FirebaseFirestore.getInstance().collection(Const.RegistrosT).document(id).set(mVenta)
                    .addOnSuccessListener {
                        val correlativo = hashMapOf(
                            "gestion" to gestion,
                            "num_alm1" to numAlm1,
                            "num_alm2" to 0,
                            "num_alm3" to 0
                        )
                        Log.d("HHHT", "${correlativo.get("num_alm1")}")
                        FirebaseFirestore.getInstance().collection(Const.Correlativo).document(idCorrelativo).set(correlativo)
                            .addOnSuccessListener { Log.d("HHHT", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("HHHT", "Error writing document", e) }
                        Log.d(TAG, "Venta REgistrada")
                        mViewModel._VentaSelected.postValue(mVenta)
                        if (tipoVenta == TipoVenta.CREDITO.i) {
                            var tp  = firstPay
                            if(mVenta.getTotal() < firstPay){
                                Toast.makeText(requireContext(),
                                    "El pago inicial no puede ser mayor al total",
                                    Toast.LENGTH_LONG).show()
                                tp = 0.0
                            }
                            val mCredito = Credito(
                                venta = id,
                                cliente = clienteId ?: "",
                                total = getTotal(mProductos) - descuento,
                                saldo = tp,
                                historial = if (tp == 0.0) arrayListOf(Pagos(
                                    fecha = Timestamp.now(),
                                    cantidad = firstPay
                                )) else arrayListOf(),
                                asesor = FirebaseAuth.getInstance().uid ?: ""
                            )
                            FirebaseFirestore.getInstance().collection(Const.Creditos).document(id)
                                .set(mCredito).addOnSuccessListener {
                                    mViewModel.cleanCart()
                                    findNavController().navigate(R.id.action_payment_to_confirm)
                                    Log.d(TAG, "Credito Sucess")
                                }
                        } else {
                            mViewModel.cleanCart()
                            findNavController().navigate(R.id.action_payment_to_confirm)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),
                            "Error al registrar la venta porfavor intente de nuevo",
                            Toast.LENGTH_SHORT).show()
                    }.addOnCompleteListener {
                        loading.dismissWithAnimation()
                    }
            }else {
                Toast.makeText(requireContext(),
                    "Error nop hay ningun producto en la venta porfavor añada almenos un producto al carrito e intente de nuevo",
                    Toast.LENGTH_LONG).show()
            }

        }


    }
    fun getTotal(data: List<ItemVenta>): Double{
        var aux = 0.0
        data.forEach {
            aux += (it.cantidad*it.precio)-it.descuento
        }
        return aux
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentFragment().apply {}
    }

    fun completarNumero(gestion: Int, numero: Int): String {
        val fmt = Formatter()
        fmt.format("%06d", numero)
        return ""+gestion+fmt
    }
}
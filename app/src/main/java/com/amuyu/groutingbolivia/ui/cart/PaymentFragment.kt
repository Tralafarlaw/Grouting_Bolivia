package com.amuyu.groutingbolivia.ui.cart

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.*
import com.amuyu.movil_inv.model.Const
import com.amuyu.movil_inv.repositories.FirestoreRepo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_payment.view.*

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
            if(ss.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Porfavor ingrese el numero de factura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val factNum = ss.toString()
            val tipo = when(v.confirm_group.checkedButtonId){
                R.id.confirm_credito -> TipoVenta.CREDITO.i
                R.id.confirm_efectivo-> TipoVenta.EFECTIVO.i
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
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialogInterface, i ->
                        if (!input.text.isNullOrEmpty()){
                            commitVenta(firstPay = input.text.toString().toDouble(), descuento = desc, tipoVenta = tipo, factura = factNum, observaciones = obs)
                        }else{
                            commitVenta(desc, tipoVenta = tipo, factura = factNum, observaciones = obs)
                        }
                    })
                    .setNegativeButton("Saltar", DialogInterface.OnClickListener { dialogInterface, _ ->
                        commitVenta(desc, tipoVenta = tipo, factura = factNum, observaciones = obs)
                        dialogInterface.cancel()
                    }).create().show()
                return@setOnClickListener
            }else{
                commitVenta(desc, tipoVenta = tipo, factura = factNum, observaciones = obs)
            }
        }

        return v
    }
    fun commitVenta(descuento: Double = 0.0, firstPay: Double = 0.0, tipoVenta: Int, factura: String, observaciones: String){
        val mProductos = mViewModel.getCartItems()
        var cliente: String? = null
        var clienteId: String? = null
        var dni: String? = null
        arguments?.let {
            cliente = it.getString("cliente_name", try{(it.getSerializable("cliente") as Cliente?)!!.nombre}catch (e: Exception){ null})
            dni = it.getString("dni", try{(it.getSerializable("cliente") as Cliente?)!!.nit}catch (e: Exception){ null})
            clienteId = (it.getSerializable("cliente") as Cliente).id
        }
        val id = FirebaseFirestore.getInstance().collection(Const.RegistrosT).document().id

        val mVenta = Venta(
            numero = factura,
            cliente = clienteId,
            nombre = cliente,
            dni = dni,
            fecha = Timestamp.now(),
            asesor = FirebaseAuth.getInstance().currentUser!!.displayName?: "error",
            asesorId = FirebaseAuth.getInstance().uid?:"error",
            items = mProductos,
            tipo = tipoVenta,
            descuento = descuento,
            observaciones = observaciones
        )

        FirebaseFirestore.getInstance().collection(Const.RegistrosT).document(id).set(mVenta).addOnSuccessListener {
            Log.d(TAG, "Venta REgistrada")
            mViewModel._VentaSelected.postValue(mVenta)
            if(tipoVenta == TipoVenta.CREDITO.i) {
                val mCredito = Credito(
                    venta = id,
                    cliente = clienteId?:"",
                    total = getTotal(mProductos)-descuento,
                    saldo = firstPay,
                    asesor = FirebaseAuth.getInstance().uid?:""
                )
                FirebaseFirestore.getInstance().collection(Const.Creditos).document(id).set(mCredito).addOnSuccessListener {
                    mViewModel.cleanCart()
                    findNavController().navigate(R.id.action_payment_to_confirm)
                    Log.d(TAG, "Credito Sucess")
                }
            }else{
                mViewModel.cleanCart()
                findNavController().navigate(R.id.action_payment_to_confirm)
            }

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al registrar la venta porfavor intente de nuevo", Toast.LENGTH_SHORT).show()
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
}
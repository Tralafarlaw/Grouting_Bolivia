package com.amuyu.groutingbolivia.adapters

import android.text.InputType
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Cliente
import com.amuyu.groutingbolivia.model.Credito
import com.amuyu.movil_inv.repositories.FirestoreRepo
import com.andreseko.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.item_credito.view.*


class CreditosAdapter: RecyclerView.Adapter<CreditosAdapter.CreditosVH>() {
    private val creditos = arrayListOf<Credito>()
    private val nombres = arrayListOf<Cliente>()
    fun addCli(data: List<Cliente>){
        nombres.clear()
        nombres.addAll(data)
        notifyDataSetChanged()
    }
    fun update(data: List<Credito>){
        creditos.clear()
        creditos.addAll(data)
        notifyDataSetChanged()
    }
    inner class CreditosVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val nombre = itemView.credito_nombre
        val pendiente = itemView.credito_pendiente
        val fecha = itemView.credito_fecha
        constructor(parent: ViewGroup)
                : this(LayoutInflater.from(parent.context).inflate(R.layout.item_credito, parent, false))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CreditosVH(parent)

    private fun getname(id: String) = try {
        nombres.filter {
            it.id == id
        }[0].nombre
    }catch (e: Exception){
        "Nombre no encontrado"
    }

    override fun onBindViewHolder(holder: CreditosVH, position: Int) {
        val aux = creditos[position]
        holder.nombre.setText(getname(aux.cliente))
        holder.fecha.setText(DateFormat.format("dd/MM/yyyy", aux.fecha.toDate()))
        holder.pendiente.setText("Bs. %.2f/%.2f".format(aux.saldo, aux.total))
        holder.itemView.setOnClickListener {
            if(aux.saldo < aux.total) {
                MaterialDialog(it.context).show {
                    title(text = "Completar Pago")
                    input(hint = "Cantidad a completar",
                        inputType = (InputType.TYPE_NUMBER_FLAG_DECIMAL.or(InputType.TYPE_CLASS_NUMBER))) { d, t ->
                        val up = aux.saldo + t.toString().toDouble()
                        val top = aux.total
                        if (up > top) {
                            Toast.makeText(it.context,
                                "El valor no debe ser superios al total",
                                Toast.LENGTH_SHORT).show()
                        } else {
                            SweetAlertDialog(it.context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(
                                    "Confirmar"
                                )
                                .setContentText("Pago de Bs. %.2f".format(t.toString().toDouble()))
                                .setConfirmButton("Aceptar",
                                    SweetAlertDialog.OnSweetClickListener { di ->
                                        FirestoreRepo.completeCredito(aux.id, up)
                                        di.dismissWithAnimation()
                                    })
                                .setCancelButton("Cancelar",
                                    SweetAlertDialog.OnSweetClickListener { di ->
                                        di.dismissWithAnimation()
                                    })
                                .show()
                        }
                    }
                    neutralButton(text = "Completar Pago") {
                        FirestoreRepo.completeCredito(aux.id, aux.total)
                    }
                    positiveButton(text = "Aceptar")
                    negativeButton(text = "Cancelar") {
                        it.dismiss()
                    }
                }
            }else{
                Toast.makeText(it.context, "Pagos completados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = creditos.size
}
package com.amuyu.groutingbolivia.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.ItemVenta
import kotlinx.android.synthetic.main.item_final_venta.view.*

class ConfirmAdapter(): RecyclerView.Adapter<ConfirmAdapter.ItemVH>() {
    var data = listOf<ItemVenta>()
    fun update(listd: List<ItemVenta>){
        Log.d("RRR", "updated")
        data = listd
        notifyDataSetChanged()
    }
    inner class ItemVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val cant = itemView.item_final_cant
        val prod = itemView.item_final_prod
        val unit = itemView.item_final_unit
        val desc = itemView.item_final_desc
        val total = itemView.item_final_total
        constructor(parent: ViewGroup)
                : this(LayoutInflater.from(parent.context).inflate(R.layout.item_final_venta, parent, false))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        return ItemVH(parent)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {

        if(position != 0){
            val aux = data[position-1]
            Log.d("RRR", "$position ++++ $aux" )
            val format = "%.2f"
            holder.cant.text = "${aux.cantidad}"
            holder.prod.text = aux.producto
            holder.desc.text = format.format(aux.descuento)
            holder.total.text = format.format((aux.cantidad* aux.precio)-aux.descuento)
            holder.unit.text = format.format(aux.precio)
        }
    }

    override fun getItemCount() = data.size +1
}
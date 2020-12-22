package com.amuyu.groutingbolivia.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Producto
import com.andreseko.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_ventas.view.*
import kotlinx.android.synthetic.main.item_ventas_list.view.*
import java.lang.Exception
import kotlin.collections.ArrayList
private const val TAG = "ProductsAdapter"
class ProductsAdapter(
    private val layoutManager: GridLayoutManager? = null, ivm: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val vm = ivm
    lateinit var ctx: Context
    enum class ViewType {
        SMALL,
        DETAILED
    }
    lateinit var data:           List<Producto>
    private var sdata :    ArrayList<Producto> = arrayListOf()
    fun setdata (ata: List<Producto>){
        data = ata
        sdata.clear()
        sdata.addAll(ata)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //val v = View.inflate(parent.context).inf
       // Log.d(TAG, "${viewType}")
        ctx = parent.context
        return when (viewType) {
            ViewType.DETAILED.ordinal -> ListViewHolder(parent)
            else -> GridViewHolder(parent)
        }
    }

    fun sort(){
        try {
            val sortedList = data.sortedWith(compareBy { it.nombre })
            setdata(sortedList)
        }catch (e: Exception){}

    }
    fun asort(){
        try {
            val sortedList = data.sortedWith(compareBy { it.nombre })
            setdata(sortedList)
        }catch (e:Exception){}
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, p1: Int) {
        val data = sdata[p1]
        //Log.d(TAG, data.toString())
        val stocks = vm.inventarios.value?: listOf(hashMapOf(), hashMapOf(), hashMapOf())
        if(vh is ListViewHolder){
            Glide.with(vh.photo.context).load(data.photourl).into(vh.photo)
            vh.price.text = "" // String.format("Bs: %.2f", data.mprecio)
            vh.stock.text = String.format(
                "%d unidades en Casa Matriz\n" +
                "%d unidades en Sopocachi\n" +
                "%d unidades en Zona Sur",
                stocks[0][data.mProductoID],
                stocks[0][data.mProductoID],
                stocks[0][data.mProductoID])
            vh.title.text = data.nombre
            vh.root.setOnClickListener{onclick(data, vh.root)}

        }else if (vh is GridViewHolder){
            Glide.with(vh.photo.context).load(data.photourl).into(vh.photo)
            vh.price.text = when(vm.getClienteType()){
                1 -> String.format("Bs: %.2f",  data.precioFerreteria)
                2 -> String.format("Bs: %.2f",  data.precioOficina)
                else -> String.format("Bs: %.2f",  data.precioObras)
            }
            vh.root.setOnLongClickListener {
                SweetAlertDialog(it.context, SweetAlertDialog.NORMAL_TYPE).apply {
                    titleText = "Stocks"
                    var st = ""
                    FirebaseDatabase.getInstance().reference.child("al1/${data.mProductoID}").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            st += "El Alto: " + snapshot.value.toString()
                            FirebaseDatabase.getInstance().reference.child("al2/${data.mProductoID}").addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    st += "\nSopocachi: " + snapshot.value.toString()
                                    FirebaseDatabase.getInstance().reference.child("al3/${data.mProductoID}").addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            st += "\nZona Sur: " + snapshot.value.toString()
                                            contentText = st
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }.show()
                true
            }


            vh.title.text = data.nombre
            vh.root.setOnClickListener {onclick(data, vh.root)}
        }

    }
    private fun onclick(map: Producto, c: View){
       vm.addtoCart(map)
       Toast.makeText(c.context, "${map.nombre} se ha añadido al carrito", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        //Log.d(TAG, "${sdata.toString()} \n ${sdata.size}")
        return try {
            sdata.size
        }catch (e:Exception){
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        //Log.d(TAG, "${layoutManager?.spanCount}")
        return if (layoutManager?.spanCount == 1) ViewType.SMALL.ordinal
        else ViewType.SMALL.ordinal
    }
}
class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var photo: ImageView = itemView.img_list
    var title: TextView = itemView.txt_title_list
    var price: TextView = itemView.txt_price_list
    var stock: TextView = itemView.txt_count_list
    var root = itemView
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.item_ventas_list, parent, false))
}
class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var photo: ImageView = itemView.img_grid
    var title: TextView = itemView.txt_title_grid
    var price: TextView = itemView.txt_price_grid
    var root = itemView
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.item_ventas, parent, false))
}

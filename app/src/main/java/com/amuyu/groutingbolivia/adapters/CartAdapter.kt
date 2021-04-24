package com.amuyu.groutingbolivia.adapters

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.bumptech.glide.Glide
import com.github.guilhe.views.addActionListener
import kotlinx.android.synthetic.main.dialog_descuento.view.*
import kotlinx.android.synthetic.main.item_cart.view.*

class CartAdapter(vm: MainViewModel, ctx: Context): RecyclerView.Adapter<CartAdapter.CartItemHolder>() {
    val mViewModel = vm
    val context = ctx
    val mData = arrayListOf<Map<String, Any>>()
    fun update (data: List<Map<String, Any>>){
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }
    inner class CartItemHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val mPhoto = itemView.cart_photo_img
        val mNombre = itemView.cart_nombre_text
        val mPrecio = itemView.cart_precio_text
        val mEliminar = itemView.cart_eliminar_button
        val mDescuento = itemView.cart_descuento_button
        val mStepper = itemView.cart_counter_stepper
        constructor(parent: ViewGroup)
                : this(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
        return CartItemHolder(parent)
    }

    fun parseString(cantidad: Int, precio: Double, descuento: Double): String{
        return String.format("Bs: %.2f", (precio * cantidad)-descuento)
    }
    override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
        val data = mData[position]
        val id = data["id"]!! as String
        var cantidad =  ((data["cantidad"] as Int)?:1)
        var precio =    ((data["precio"] as Double)?:0.0)
        var descuento = ((data["descuento"] as Double)?:0.0)
        holder.mNombre.text = (data["nombre"] as String?)?:""
        val ddata = mViewModel.getCart()[position]
        holder.mPrecio.text = parseString(
            ((ddata["cantidad"] as Int)?:1),
            ((ddata["precio"] as Double)?:0.0),
            ((ddata["descuento"] as Double)?:0.0))
        holder.mStepper.min = 1
        holder.mStepper.value = cantidad
        holder.mStepper.addActionListener(onValueChanged = {_, i ->
            cantidad = i
            mViewModel.setinCart(id, cantidad)
            val ddata = mViewModel.getCart()[position]
            holder.mPrecio.text = parseString(
                ((ddata["cantidad"] as Int)?:1),
                ((ddata["precio"] as Double)?:0.0),
                ((ddata["descuento"] as Double)?:0.0))
        })
        holder.mEliminar.setOnClickListener {
            mViewModel.pullFromCart(data["id"] as String)
            mData.remove(data)
            notifyItemRemoved(position)
        }
        holder.mDescuento.setOnClickListener {
            //Log.d("Descuentos", "DEcusntos Clicked")
            val builder = AlertDialog.Builder(context)
            // Get the layout inflater
            val inflater = LayoutInflater.from(context)
            val v = inflater.inflate(R.layout.dialog_descuento, null)
            val toggleButton = v.descuento_group
            val field = v.descuento_input
            field.setText("$descuento")
            toggleButton.check(R.id.descuento_manual)
            toggleButton.addOnButtonCheckedListener{_, checkedId, isChecked ->
                if(isChecked){
                    if (checkedId == R.id.descuento_manual){
                        val aux = field.text.toString().toDouble()
                        field.setText("${(precio*cantidad*aux)/100}")
                    }else if (checkedId == R.id.descuento_percent){
                        val aux = field.text.toString().toDouble()
                        field.setText("${(100* aux)/(precio*cantidad)}")
                    }
                }
            }
            builder.setView(v)
            builder.setTitle("Descuento")
            val dial = builder.create()
            dial.show()
            v.decuento_acept.setOnClickListener {
                if(field.text.isNullOrBlank()){
                    Toast.makeText(it.context, "Porfavor Ingrese un valor", Toast.LENGTH_SHORT).show()
                    dial.dismiss()
                    return@setOnClickListener
                }
                descuento = if(toggleButton.checkedButtonId == R.id.descuento_percent){
                    (precio*cantidad*field.text.toString().toDouble())/100
                }else{field.text.toString().toDouble()}
                holder.mPrecio.text = parseString(cantidad, precio, descuento)
                mViewModel.setDiscount(id, descuento)
                dial.dismiss()
            }
            v.descuento_cancel.setOnClickListener {
                dial.dismiss()
            }
        }
        holder.itemView.setOnLongClickListener {
            MaterialDialog(it.context).show{
                input(hint = "Cantidad", inputType = InputType.TYPE_CLASS_NUMBER, allowEmpty = false) { _, sequence ->
                    val num = sequence.toString().toInt()
                    cantidad = num
                    mViewModel.setinCart(id, cantidad)
                    holder.mPrecio.text = parseString(
                        ((ddata["cantidad"] as Int)?:1),
                        ((ddata["precio"] as Double)?:0.0),
                        ((ddata["descuento"] as Double)?:0.0))
                    holder.mStepper.value = num
                }
            }
            true
        }
        Glide.with(holder.mPhoto).load(data["url"] as String).into(holder.mPhoto)
    }

    override fun getItemCount() = mData.size



}
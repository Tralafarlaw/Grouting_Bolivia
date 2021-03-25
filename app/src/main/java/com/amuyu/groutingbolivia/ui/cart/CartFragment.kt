package com.amuyu.groutingbolivia.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.adapters.CartAdapter
import com.andreseko.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_descuento.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class CartFragment : Fragment() {

    private val mViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        val adapter = CartAdapter(mViewModel, requireContext())
        root.cart_rv.layoutManager = LinearLayoutManager(requireContext())
        root.cart_rv.adapter = adapter
        adapter.update(mViewModel.getCart())
        root.cart_next_button.setOnClickListener {
            val diag = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
            diag.setContentText("Verificando Stock")
            val ProdIds = mViewModel.getCart().map { map: Map<String, Any> -> map["id"].toString() }
            val stocks = mViewModel._cartProducts.value ?: hashMapOf()
            val nombres = mViewModel._cartNombres.value ?: hashMapOf()
            var iterations = 0
            diag.show()
            val list: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    iterations++
                    val st = snapshot.value.toString().toInt()
                    val key = snapshot.key
                    val com = stocks[key].toString().toInt()
                    if (st < com) {
                        diag.dismissWithAnimation()
                        iterations = 0
                        Toast.makeText(requireContext(),
                            "Error stock de ${nombres[key]} insuficente solo quedan $st en almacen",
                            Toast.LENGTH_LONG).show()
                    }
                    if (iterations == ProdIds.size) {
                        diag.dismissWithAnimation()
                        findNavController().navigate(R.id.action_cart_to_datos)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    diag.dismissWithAnimation()
                    Toast.makeText(requireContext(),
                        "Error al revisar stock porfavor intente de nuevo",
                        Toast.LENGTH_LONG).show()
                }
            }
            FirebaseAuth.getInstance().currentUser!!.getIdToken(false)
                .addOnSuccessListener { tkr ->
                    val al = tkr.claims["al"]
                    ProdIds.forEach { s ->
                        FirebaseDatabase.getInstance().getReference("$al/$s")
                            .addListenerForSingleValueEvent(list)
                    }
                }

        }
        root.add_desc_btn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            // Get the layout inflater
            val inflater = LayoutInflater.from(context)
            val v = inflater.inflate(R.layout.dialog_descuento, null)
            val toggleButton = v.descuento_group
            val field = v.descuento_input
            field.setText("0.0")
            toggleButton.check(R.id.descuento_manual)
            toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    if (checkedId == R.id.descuento_manual) {
                        val aux = try {
                            field.text.toString().toDouble()
                        } catch (e: Exception) {
                            0.0
                        }
                        field.setText("${(mViewModel.getCartTotal() * aux) / 100}")
                    } else if (checkedId == R.id.descuento_percent) {
                        val aux = try {
                            field.text.toString().toDouble()
                        } catch (e: Exception) {
                            0.0
                        }
                        field.setText("${(100 * aux) / (mViewModel.getCartTotal())}")
                    }
                }
            }
            builder.setView(v)
            builder.setTitle("Descuento")
            val dial = builder.create()
            dial.show()
            v.decuento_acept.setOnClickListener {
                if (field.text.isNullOrBlank()) {
                    Toast.makeText(it.context, "Porfavor Ingrese un valor", Toast.LENGTH_SHORT)
                        .show()
                    dial.dismiss()
                    return@setOnClickListener
                }
                val descuento = if (toggleButton.checkedButtonId == R.id.descuento_percent) {
                    (mViewModel.getCartTotal() * field.text.toString().toDouble()) / 100
                } else {
                    field.text.toString().toDouble()
                }
                mViewModel.setDesc(descuento)
                dial.dismiss()
            }
            v.descuento_cancel.setOnClickListener {
                dial.dismiss()
            }
        }
        root.clean_cart_btn.setOnClickListener {
            mViewModel.cleanCart()
            (requireParentFragment().parentFragment as DialogFragment).dismiss()
        }
        mViewModel._cartProducts.observe(viewLifecycleOwner, Observer {
            root.cart_st.setText("Bs. %.2f".format(mViewModel.getCartTotal()))
            root.cat_t.setText("Bs. %.2f".format(mViewModel.getCartTotal() - mViewModel.getDesc()))
        })
        mViewModel._cartDescuentos.observe(viewLifecycleOwner, Observer {
            root.cart_st.setText("Bs. %.2f".format(mViewModel.getCartTotal()))
            root.cat_t.setText("Bs. %.2f".format(mViewModel.getCartTotal() - mViewModel.getDesc()))
        })
        mViewModel._cartDesc.observe(viewLifecycleOwner, Observer {
            root.cart_d.setText("Bs. %.2f".format(mViewModel.getDesc()))
            root.cat_t.setText("Bs. %.2f".format(mViewModel.getCartTotal() - mViewModel.getDesc()))
        })
        return root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
            }
    }
}
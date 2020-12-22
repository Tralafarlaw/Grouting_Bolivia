package com.amuyu.groutingbolivia.ui.clients

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Cliente
import com.amuyu.groutingbolivia.model.ZONAS
import com.amuyu.movil_inv.repositories.FirestoreRepo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_client.*
import kotlinx.android.synthetic.main.fragment_register_client.view.*
private const val ARG1 = "ARG1"
class RegisterClientFragment : DialogFragment() {
    private var id: String? = null
    private val mViewModeel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getString(ARG1)
        if(id == "-1")
            id = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register_client, container, false)
        root.new_client_cancel.setOnClickListener {
            dismiss()
        }
        if(id != null){
        mViewModeel.clientes.observe(viewLifecycleOwner, Observer {
            it.filter { ff -> ff.id == id }[0].also {aux ->
                root.new_client_name.setText(aux.nombre)
                root.new_client_address.setText(aux.direccion)
                root.new_client_nit.setText(aux.nit)
                root.new_client_phone.setText(aux.telefono)
            }
            root.new_client_checks.visibility = View.GONE
        })}

        root.new_client_agree.setOnClickListener {
            root.new_client_agree.isEnabled = false
            val name =      root.new_client_name.text.toString()
            val direccion = root.new_client_address.text.toString()
            val nit =       root.new_client_nit.text.toString()
            val telefono =  root.new_client_phone.text.toString()
            val tipoD = when(root.new_client_checks.checkedRadioButtonId){
                R.id.oruro_check    -> ZONAS.ORURO
                R.id.el_alto_check  -> ZONAS.EL_ALTO
                R.id.zona_sur_check -> ZONAS.SUR
                R.id.centro_check   -> ZONAS.CENTRO
                R.id.others_check   -> ZONAS.OTROS
                else -> null
            }
            //checkear valides de los campos
            if(id==null) {
                FirestoreRepo.registerCliente(Cliente(name, direccion, nit, telefono).apply {
                    zona = tipoD!!
                }, this)
            }else{
                FirestoreRepo.updateCliente(Cliente(name, direccion, nit, telefono).apply {
                }, id!!).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cliente Actualizado con exito", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String = "-1") =
            RegisterClientFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG1, id)
                }
            }
    }
}
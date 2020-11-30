package com.amuyu.groutingbolivia.ui.clients

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Cliente
import com.amuyu.groutingbolivia.model.ZONAS
import com.amuyu.movil_inv.repositories.FirestoreRepo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_client.*
import kotlinx.android.synthetic.main.fragment_register_client.view.*

class RegisterClientFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register_client, container, false)
        root.new_client_cancel.setOnClickListener {
            dismiss()
        }
        root.new_client_agree.setOnClickListener {
            val name = root.new_client_name.text.toString()
            val direccion = root.new_client_address.text.toString()
            val nit = root.new_client_nit.text.toString()
            val telefono = root.new_client_phone.text.toString()
            val tipoD = when(root.new_client_checks.checkedRadioButtonId){
                R.id.oruro_check    -> ZONAS.ORURO
                R.id.el_alto_check  -> ZONAS.EL_ALTO
                R.id.zona_sur_check -> ZONAS.SUR
                R.id.centro_check   -> ZONAS.CENTRO
                R.id.others_check   -> ZONAS.OTROS
                else -> null
            }
            //checkear valides de los campos
            FirestoreRepo.registerCliente(Cliente(name, direccion, nit, telefono).apply { zona = tipoD!! })
        }
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RegisterClientFragment().apply {}
    }
}
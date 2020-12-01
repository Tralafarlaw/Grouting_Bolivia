package com.amuyu.groutingbolivia.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.amuyu.groutingbolivia.LoginActivity
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Perfil
import com.cazaea.sweetalert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    private val mViewModel: MainViewModel by activityViewModels()
    private var mPerfil: Perfil? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        mViewModel.profile.observe(viewLifecycleOwner, Observer {
            v.profile_name.text = "Nombre: %s".format(it.nombre)
            v.profile_almacen.text = when (it.al) {
                "al1" -> "Almacen El Alto"
                "al2" -> "Almacen Sopocachi"
                else -> "Almacen Zona Sur"
            }
            mPerfil = it
        })
        profile_nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_about -> about()
                R.id.menu_logout -> logout()
                R.id.menu_my_clients -> clients()
                R.id.menu_my_profile -> perfil()
            }
            true
        }
        return v
    }
    private fun about(){
        SweetAlertDialog(requireContext())
            .setTitleText("Amuyu Software")
            .setContentText("Acerca de la pp y de la empresa")
            .setConfirmText("Aceptar") // Do not set the property, do not show the button
            .show();
    }
    private fun logout(){
        FirebaseAuth.getInstance().signOut()
        Intent(requireContext(), LoginActivity::class.java).also {
            requireActivity().finish()
            startActivity(it)
        }
    }
    private fun clients(){

    }
    private fun perfil(){
        SweetAlertDialog(requireContext())
            .setTitleText("${mPerfil?.nombre}")
            .setContentText(mPerfil.toString())
            .setConfirmText("Aceptar") // Do not set the property, do not show the button
            .show();
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {}
    }
}
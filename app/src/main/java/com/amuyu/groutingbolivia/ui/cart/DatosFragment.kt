package com.amuyu.groutingbolivia.ui.cart

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.TAG
import com.amuyu.groutingbolivia.model.Cliente
import com.amuyu.groutingbolivia.ui.clients.RegisterClientFragment
import kotlinx.android.synthetic.main.fragment_datos.view.*


class DatosFragment : Fragment() {
    val mAdapter: ArrayAdapter<Cliente> by lazy {
        ArrayAdapter(requireContext(), android.R.layout.simple_selectable_list_item, android.R.id.text1, arrayListOf())
    }
    fun Cliente.toString(): String {
        return nombre
    }
    private val mViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_datos, container, false)
        mViewModel.clientes.observe(viewLifecycleOwner, Observer {
            mAdapter.clear()
            mAdapter.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
        root.client_list.adapter = mAdapter
        root.client_list.setOnItemClickListener { adapterView, view, i, l ->
            val c = adapterView.getItemAtPosition(i) as Cliente
            val aux = Bundle().apply {
                putSerializable("cliente", c)
            }
            findNavController().navigate(R.id.action_datos_to_confirm, aux)
        }
        root.client_search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                mAdapter.filter.filter(p0)
                return true
            }
        })
        root.client_quick.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val name = EditText(requireContext())
            name.hint = "Nombre"
            val dni = EditText(requireContext())
            dni.hint = "NIT o CI"
            val ll = LinearLayout(requireContext())
            ll.orientation = LinearLayout.VERTICAL
            ll.addView(name)
            ll.addView(dni)
            builder.setView(ll)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { d, _ ->
                    val aux = Bundle().apply {
                        if(name.text.isNullOrEmpty()){
                            Toast.makeText(requireContext(), "Porfavor ingrese el nombre el cliente", Toast.LENGTH_SHORT).show()
                            return@OnClickListener
                        }
                        if(dni.text.isNullOrEmpty()){
                            Toast.makeText(requireContext(), "Porfavor ingrese el NIT o CI", Toast.LENGTH_SHORT).show()
                            return@OnClickListener
                        }
                        putString("cliente_name", name.text.toString())
                        putString("dni", dni.text.toString())
                    }
                    findNavController().navigate(R.id.action_datos_to_confirm, aux)
                    d.dismiss()
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { d, _ ->
                    d.cancel()
                })
            builder.create().show()

        }
        root.client_register.setOnClickListener {
            RegisterClientFragment.newInstance().show(childFragmentManager, TAG)
        }
        return  root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DatosFragment().apply {}
    }
}
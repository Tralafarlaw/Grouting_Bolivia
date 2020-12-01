package com.amuyu.groutingbolivia.ui.profile.clientes

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.TAG
import com.amuyu.groutingbolivia.model.Cliente
import com.amuyu.groutingbolivia.ui.clients.RegisterClientFragment
import kotlinx.android.synthetic.main.fragment_datos.view.*
import kotlinx.android.synthetic.main.fragment_manage_clients.view.*


class ManageClientsFragment : DialogFragment() {
    private lateinit var rv: ListView
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
        val v = inflater.inflate(R.layout.fragment_manage_clients, container, false)
        rv = v.mc_rv
        mViewModel.clientes.observe(viewLifecycleOwner, Observer {
            mAdapter.clear()
            mAdapter.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
        rv.adapter = mAdapter
        rv.setOnItemClickListener { adapterView, view, i, l ->
            val c = adapterView.getItemAtPosition(i) as Cliente
            RegisterClientFragment.newInstance().show(childFragmentManager, TAG)
        }
        v.mc_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                mAdapter.filter.filter(p0)
                return true
            }
        })


        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageClientsFragment().apply {

            }
    }
}
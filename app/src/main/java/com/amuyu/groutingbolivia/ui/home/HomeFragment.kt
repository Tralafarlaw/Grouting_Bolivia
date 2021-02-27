package com.amuyu.groutingbolivia.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.adapters.ProductsAdapter
import com.amuyu.groutingbolivia.model.Producto
import kotlinx.android.synthetic.main.fragment_home.view.*

private const val TAG = "HomeFragment"
private const val ARG_PARAM1 = "param1"
class HomeFragment : Fragment() {

    private val  mViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ProductsAdapter
    private var data = listOf<Producto>()
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val rv  = root.products_rv
        root.home_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?) = false


            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0?:"".length != 0){
                    adapter.setdata(data.filter { dd -> dd.nombre.contains(p0!!, true) || dd.codigo.contains(p0!!, true) || dd.mProductoID.contains(p0!!, true) || dd.codigodebarras.contains(p0!!, true)  })
                }else{
                    adapter.setdata(data)
                }
                return true
            }

        })
        val display: Display = requireActivity().windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = resources.displayMetrics.density
        val dpWidth = outMetrics.widthPixels / density
        Log.d(TAG, "$dpWidth, ${(dpWidth/105).toInt()}")
        val layoutManager = GridLayoutManager(requireContext(),(dpWidth / 105).toInt())
        adapter = ProductsAdapter(ivm = mViewModel, layoutManager = layoutManager)
        rv.layoutManager = layoutManager
        rv.adapter = adapter
        mViewModel.productos.observe(viewLifecycleOwner, Observer {
            it.filter { ss -> ss.categorias.contains(param1) }.also { its->
                Log.d(TAG, its.toString())
                data = its
                adapter.setdata(its)
            }
        })
        mViewModel._clientType.observe(viewLifecycleOwner, Observer { _ -> adapter.notifyDataSetChanged() })

        return root
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }

            }
    }
}
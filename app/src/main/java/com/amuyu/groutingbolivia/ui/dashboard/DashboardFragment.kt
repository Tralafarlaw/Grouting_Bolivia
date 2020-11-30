package com.amuyu.groutingbolivia.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.adapters.CreditosAdapter
import com.amuyu.groutingbolivia.adapters.ProductsAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

private const val TAG = "DashboardFragment"
class DashboardFragment : Fragment() {
    val mViewModel: MainViewModel by activityViewModels()
    val mAdapter = CreditosAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        root.creditos_rv.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            mViewModel.creditos.observe(viewLifecycleOwner, Observer {ss ->
                mAdapter.update(ss)
            })
            mViewModel.clientes.observe(viewLifecycleOwner, Observer {ss ->
                mAdapter.addCli(ss
                )
            })
            it.adapter = mAdapter
        }
        return root
    }

}
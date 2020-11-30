package com.amuyu.groutingbolivia.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home_holder.view.*


class HomeHolderFragment : Fragment() {
    private lateinit var vp: ViewPager
    private lateinit var tabs: TabLayout
    private val mViewModel: MainViewModel by activityViewModels()
    private val adapter by  lazy{ CategoriasPagerAdapter(childFragmentManager) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home_holder, container, false)
        vp = v.pager

        mViewModel.productos.observe(viewLifecycleOwner, Observer {
            val categorias = hashSetOf<String>()
            it.forEach { producto ->
                producto.categorias.forEach {  gg ->
                    categorias.add(gg)
                }
            }
            adapter.setCats(categorias.toList().sorted())
        })
        vp.adapter = adapter
        tabs = v.tab_layout
        tabs.setupWithViewPager(vp)
        return v
    }
    inner class CategoriasPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private var mData = listOf<String>()
        fun setCats(data: List<String>){
            mData = data
            notifyDataSetChanged()
        }
        override fun getCount() = mData.size

        override fun getItem(i: Int): Fragment {
            val fragment = HomeFragment.newInstance(mData[i])
            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mData[position]
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeHolderFragment().apply {}
    }
}
package com.amuyu.groutingbolivia.ui.reportes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.amuyu.groutingbolivia.MainViewModel
import com.amuyu.groutingbolivia.R
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment() {

    private val mViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        root.rep_group.setOnCheckedChangeListener { group, i ->
            when(i) {
                R.id.rep_hoy -> setInterval(Calendar.getInstance()
                    .apply { set(Calendar.HOUR, 0); set(Calendar.MINUTE, 0) }.time)
                R.id.rep_week -> setInterval(Calendar.getInstance()
                    .apply { set(Calendar.DAY_OF_WEEK, 1) }.time)
                R.id.rep_monthy -> setInterval(Calendar.getInstance()
                    .apply { set(Calendar.DAY_OF_MONTH, 1) }.time)
                R.id.rep_custom -> {
                    val now = Calendar.getInstance()
                    val builder = MaterialDatePicker.Builder.dateRangePicker()
                    builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
                    val picker = builder.build()
                    picker.show(childFragmentManager, picker.toString())
                    picker.addOnNegativeButtonClickListener {
                        picker.dismiss()
                    }
                    picker.addOnPositiveButtonClickListener {
                        root.rep_from.text = SimpleDateFormat("dd/MM/yyyy").format(Date(it.second!!))
                        root.rep_to.text = SimpleDateFormat("dd/MM/yyyy").format(Date(it.first!!))
                        setInterval(Date(it.first!!), Date(it.second!!))
                    }
                }
            }
        }
        return root
    }
    fun setInterval(desde: Date, hasta: Date = Calendar.getInstance().time) {
        mViewModel.desde.value = desde
        mViewModel.hasta.value = hasta
    }
}
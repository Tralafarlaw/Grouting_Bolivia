package com.amuyu.groutingbolivia.ui.reportes.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import com.amuyu.groutingbolivia.R
import com.amuyu.groutingbolivia.model.Venta
import com.amuyu.movil_inv.model.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_display_report.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayReport : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var desde: Date = Calendar.getInstance().time
    private var hasta: Date = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        arguments?.let {
            desde = it.getSerializable(ARG_PARAM1) as Date
            hasta = it.getSerializable(ARG_PARAM2) as Date
        }
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_display_report, container, false)
        FirebaseFirestore.getInstance().collection(Const.RegistrosT).whereEqualTo("asesorId", FirebaseAuth.getInstance().uid?:"")
            .whereGreaterThanOrEqualTo("fecha", desde)
            .whereLessThanOrEqualTo("fecha", hasta)
            .get().addOnSuccessListener {
                val data = it.toObjects(Venta::class.java)
                val doc = PdfTemplate(requireContext())
                doc.openDocument()
                doc.addTittles("Reporte de Ventas", "Grouting Bolivia", SimpleDateFormat("dd/MM/yyyy hh:mm").format(Calendar.getInstance().time))
                for (d in data){
                    doc.createTable(d)
                }
                doc.clodeDOIc()
                val file = doc.viewPDFuri()
                val uri = FileProvider.getUriForFile(requireContext(),
                    "com.amuyu.groutingbolivia.myprovider",
                    file)
                v.rep_share.setOnClickListener {
                    requireContext().grantUriPermission(requireContext().packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    val intent = ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("application/pdf")
                        .setStream(uri)
                        .setChooserTitle("Elija una aplicacion")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
                Log.d("PDFF", uri.toString())
                v.pdfView.fromFile(file)
            }
        return v
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: Date, param2: Date) =
            DisplayReport().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                }
            }
    }
}
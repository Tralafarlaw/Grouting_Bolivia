package com.amuyu.groutingbolivia.ui.reportes.dialog

import android.content.Context
import android.os.Environment
import android.util.Log
import com.amuyu.groutingbolivia.model.Venta
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PdfTemplate(context: Context) {
    private val ctx = context
    private var pdfFile: File? = null
    private var document: Document? = null
    private var pdfwriter: PdfWriter? = null
    private var paragraph: Paragraph? = null
    private val ftittle: Font = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD)
    private val fSubtittle: Font = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD)
    private val fText: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
    private val fHightittle: Font = Font(Font.FontFamily.HELVETICA, 15f, Font.BOLD, BaseColor.RED)

    fun openDocument() {
        createPDF()

    }

    private fun createPDF() {

           // pdfFile = File(ctx.getExternalFilesDir(null)!!.absolutePath + File.separator.toString(), "ReporteVentas ${SimpleDateFormat("dd/MM/yyyy hh:mm").format(Calendar.getInstance().time)}.pdf")
            try {
                document = Document()
                val dirpath = ctx.getExternalFilesDir(null)!!
                val fos = FileOutputStream(dirpath.absolutePath + "/reporte.pdf")
                pdfFile = File(dirpath.absolutePath + "/reporte.pdf")
                PdfWriter.getInstance(document,
                    fos) //  Change pdf's name.
                document!!.open()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CREATEPFD", "createPDF: error", e)
            }
            //Crea archivo

    }


    fun addMetaData(tittle: String?, subject: String?, author: String?) {
        document!!.addTitle(tittle)
        document!!.addSubject(subject)
        document!!.addAuthor(author)
    }

    fun addTittles(tittle: String?, subtittle: String?, date: String) {
        try {
            paragraph = Paragraph()
            addChildP(Paragraph(tittle, ftittle))
            addChildP(Paragraph(subtittle, fSubtittle))
            addChildP(Paragraph("generado: $date", fHightittle))
            paragraph!!.setSpacingAfter(30f)
            document!!.add(paragraph)
        } catch (e: Exception) {
            Log.e("addTittles", e.toString())
        }
    }

    private fun addChildP(childParagraph: Paragraph) {
        childParagraph.alignment = Element.ALIGN_CENTER
        paragraph!!.add(childParagraph)
    }

    fun getCellHeader(title: String): PdfPCell{
        val pdfPCell = PdfPCell(Phrase(title))
        pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
        pdfPCell.backgroundColor = BaseColor.LIGHT_GRAY
        return pdfPCell
    }
    fun getCellBody(title: String): PdfPCell{
        val pdfPCell = PdfPCell(Phrase(title, fSubtittle))
        pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
        pdfPCell.backgroundColor = BaseColor.WHITE
        return pdfPCell
    }
    fun createTable(venta: Venta) {
        try {
            val columdWidths: FloatArray = floatArrayOf(2.1f, 6.5f, 2f, 2f, 2f)
            paragraph = Paragraph()
            paragraph!!.setFont(fText)
            paragraph!!.add("\n" +
                    "Número: ${venta.numero}\n" +
                    "Clente: ${venta.cliente} [${venta.nombre}]\n" +
                    "Fecha: ${SimpleDateFormat("dd/MM/yyyy hh:mm").format(venta.fecha.toDate())}")

            val pdfPTable = PdfPTable(columdWidths)
            pdfPTable.widthPercentage = 100f

            pdfPTable.addCell(getCellHeader("Cantidad"))
            pdfPTable.addCell(getCellHeader("Producto"))
            pdfPTable.addCell(getCellHeader("Descuento"))
            pdfPTable.addCell(getCellHeader("Precio Unitario"))
            pdfPTable.addCell(getCellHeader("Total"))

            for (item in venta.items) {
                pdfPTable.addCell(Phrase(item.cantidad.toString(), fText))
                pdfPTable.addCell(Phrase(item.nombre, fText))
                pdfPTable.addCell(Phrase("%.2f".format(item.descuento), fText))
                pdfPTable.addCell(Phrase("%.2f".format(item.precio), fText))
                pdfPTable.addCell(Phrase("%.2f".format((item.precio * item.cantidad) - item.descuento), fText))
            }
            pdfPTable.addCell("Descuento: ")
            pdfPTable.addCell("")
            pdfPTable.addCell("")
            pdfPTable.addCell("")
            pdfPTable.addCell("%.2f".format(venta.descuento))
            pdfPTable.addCell("Total: ")
            pdfPTable.addCell("")
            pdfPTable.addCell("")
            pdfPTable.addCell("")
            pdfPTable.addCell("%.2f".format(venta.getTotal()))
            paragraph!!.add(pdfPTable)
            document!!.add(paragraph)

        } catch (e: Exception) {
            Log.e("createTable", e.toString())
        }
    }
    fun clodeDOIc(){
        document!!.close()
    }
    fun viewPDFuri() = pdfFile!!


}
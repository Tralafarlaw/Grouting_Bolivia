package com.amuyu.groutingbolivia.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import java.io.*


class PDFUtils {
    companion object{
        private fun viewToImage(view: View): Bitmap {
            val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            val bgDrawable = view.background
            if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
            view.draw(canvas)
            return returnedBitmap
        }

        fun layoutToPDF(view: View, name: String, action: Int):Uri? {
            val bm: Bitmap = viewToImage(view)
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            val bytes = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val f = File(view.context.getExternalFilesDir(null)!!.absolutePath + File.separator.toString() + "image.jpg")
            try {
                f.createNewFile()
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                return imageToPDF(name, view.context, action)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        @Throws(FileNotFoundException::class)
        fun imageToPDF(name: String, ctx: Context, action: Int):Uri? {
            try {
                val document = Document()
                val dirpath = ctx.getExternalFilesDir(null)!!
                val fos = FileOutputStream(dirpath.absolutePath + "/$name.pdf")
                PdfWriter.getInstance(document,
                    fos) //  Change pdf's name.
                document.open()
                val img: Image =
                    Image.getInstance(dirpath.absolutePath + File.separator.toString() + "image.jpg")
                val scaler: Float = (document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / img.getWidth() * 100
                img.scalePercent(scaler)
                img.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)
                document.setPageSize(Rectangle(img.absoluteX, img.absoluteY))
                document.add(img)
                document.close()
                val aux = File(dirpath.absolutePath + "/$name.pdf")
                val uri = FileProvider.getUriForFile(ctx,
                    "com.amuyu.groutingbolivia.myprovider",
                    aux)
                Log.d("TAGGG", aux.toURI().toString() +"===="+ uri.toString())
                return uri
                //Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                return null
            }
        }
    }
}
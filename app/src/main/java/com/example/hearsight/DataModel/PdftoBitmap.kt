package com.example.hearsight.DataModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PdftoBitmap(private val context: Context) {
    lateinit var  render:PdfRenderer
    var totalPages=0
    var displayPages=0
    var currentPage=0

    fun splitPdfPages(pdfFilePath: Uri, getepdffileDetails: GetPdfFileDetails){
        CoroutineScope(Dispatchers.IO).launch {
            val parcelFileDescriptor: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(pdfFilePath, "r")!!
            render = PdfRenderer(parcelFileDescriptor)
            totalPages = render.pageCount
            displayPages = 0
            val filePaths = mutableListOf<String>()
            val quality = 10
            val scaleFactor = 5
            for (pageIndex in 0 until totalPages) {
                val page = render.openPage(pageIndex)
                val width = (page.width * scaleFactor)
                val height = (page.height * scaleFactor)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                val outputFile = File(context.cacheDir, "page_${pageIndex + 1}.jpg")
                try {
                    val outputStream = FileOutputStream(outputFile)
                    if (outputStream == null)
                    {
                        Toast.makeText(context, "Stream empty", Toast.LENGTH_SHORT).show()
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    outputStream.close()
                }catch (e:NullPointerException)
                {
                    Log.e("SplitScreenStreamEmpty","${e.toString()}")
                }

                filePaths.add(outputFile.absolutePath)
                page.close()
                currentPage++
            }
            render.close()
            getepdffileDetails.getdetails(filePaths, totalPages, currentPage)
        }
    }

    interface GetPdfFileDetails
    {
        fun getdetails(filePaths: MutableList<String>, totalPages: Int, pageIndex: Int)
    }
}

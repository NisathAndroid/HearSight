package com.example.txtextrct

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.hearsight.DataModel.ResultTextPreProcessing
import com.example.hearsight.R
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File


class TesseractMainManager(private val context: Context) {
    lateinit var tessApi: TessBaseAPI
    val progressDialog = ProgressDialog(context)
    var isInitialized = false
    fun initTesseract(dataPath: String, language: String, engineMode: Int) {
        tessApi = TessBaseAPI()
        //val langStorePath="/data/user/0/com.example.hearsight/files"
        val langStorePath=context.getString(R.string.tesseract_filepath)
        try {
            this.isInitialized = tessApi.init(langStorePath, language, engineMode)
        } catch (e: IllegalArgumentException)
        {
            this.isInitialized = false
            Log.e(TAG, "Cannot initialize Tesseract:", e)
        }
    }

    fun recognizeImage(imagePath: File): String? {
        try {
            if (!this.isInitialized) {
                Log.e(TAG, "recognizeImage: Tesseract is not initialized")
                return ""
            }

            (context as Activity).runOnUiThread {
                progressDialog.setMessage("Recognizing text...")
                progressDialog.setCancelable(false)
                progressDialog.show()
            }

            val psm = TessBaseAPI.PageSegMode.PSM_AUTO
            tessApi.pageSegMode = psm
            tessApi.setImage(imagePath)
            tessApi.getHOCRText(0)
            var text = tessApi.utF8Text
            tessApi.clear()

            (context as Activity).runOnUiThread {
                // Dismiss the progress dialog
                progressDialog.dismiss()
            }

            return text
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}

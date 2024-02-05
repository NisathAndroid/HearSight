package com.example.hearsight.DataModel

import TextToSpeechHelper
import android.app.NotificationManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hearsight.Activity.PdfActivity
import com.example.hearsight.R
import com.example.txtextrct.TesseractMainManager
import java.io.File


class TextExtractionBackgroundTask(
    private val context: PdfActivity,
    private val imageFilePathList: MutableList<String>,
    private val tesseractManager: TesseractMainManager,
    private val notificationManager: NotificationManager
) : AsyncTask<Void, Int, String>() {
    var notificationProgressbar=NotificationProgressbar(context)
    private val SHARED_PREF_NAME = "text_extraction_pref"
    private lateinit var  ttsHelper:TextToSpeechHelper
    override fun onPreExecute() {
        super.onPreExecute()
        val builder = NotificationCompat.Builder(context, notificationProgressbar.channelId)
            .setContentTitle("Text Extraction in Progress")
            .setContentText("Please wait...")
            .setSmallIcon(R.drawable.advantechlog_two)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setProgress(imageFilePathList.size, 0, false)
            .setSound(null)

        notificationManager.notify(1, builder.build())
    }

    override fun doInBackground(vararg params: Void?): String {
        val sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val savedProgress = sharedPref.getInt("progress", 0)
        var result: String? = null
        var retryCount = 0
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                val textBuilder = StringBuilder()
                for (i in savedProgress until imageFilePathList.size) {
                    val text = tesseractManager.recognizeImage(File(imageFilePathList[i])) ?: ""
                    textBuilder.append(text)
                    textBuilder.append("\n")
                    publishProgress(i + 1)
                }
                result = textBuilder.toString()
                break
            } catch (e: Exception) {
                Log.e("ExceptionBackground", e.toString())
                retryCount++
            }
        }
        return result ?: ""
    }


    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        try {
            val totalImages = imageFilePathList.size
            val currentImage = values[0] ?: 0
            val percentage = (currentImage.toFloat() / totalImages.toFloat() * 100).toInt()
            val builder = NotificationCompat.Builder(context, notificationProgressbar.channelId)
                .setContentTitle("Text Extraction in Progress")
                .setContentText("Please wait... $percentage%")
                .setSmallIcon(R.drawable.advantechlog_two)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(totalImages, currentImage, false)
                .setSound(null)
            notificationManager.notify(1, builder.build())
            Log.e("isServiceRunning\t:","True")
        }catch (e:Exception)
        {
            Log.e("isServiceRunning\t:",e.toString())
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        result?.let {
            context.handleSaveButtonAsyn(it)
            notificationManager.cancel(1)
        }
    }

    companion object {
        private const val MAX_RETRY_COUNT = 10
    }
}

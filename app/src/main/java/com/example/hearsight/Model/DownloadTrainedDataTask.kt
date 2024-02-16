package com.example.hearsight.Model


import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class DownloadTrainedDataTask {
    suspend fun downloadLanguage(context: Context, language: String, progressCallback: (Int) -> Unit): Pair<Boolean, String?> {
        return withContext(Dispatchers.IO) {
            val filesDir = context.filesDir
            val tessDataFolder = File(filesDir, "tessdata")
            if (!tessDataFolder.exists()) {
                tessDataFolder.mkdirs()
            }
            val languageDataFile = File(tessDataFolder, "$language.traineddata")
            if (!languageDataFile.exists()) {
                val languageDataUrl = "https://github.com/tesseract-ocr/tessdata/raw/main/$language.traineddata"
                try {
                    val url = URL(languageDataUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val fileLength = connection.contentLength
                        val inputStream: InputStream = connection.inputStream
                        val outputStream = FileOutputStream(languageDataFile)
                        val buffer = ByteArray(1024)
                        var total = 0
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            total += bytesRead
                            val progress = (total * 100 / fileLength)
                            progressCallback(progress)
                        }
                        outputStream.close()
                        inputStream.close()
                        val filePath = languageDataFile.absolutePath
                        Log.d("OCR_Download", "Language data downloaded successfully\n$filePath")
                        return@withContext Pair(true, filePath)
                    } else
                    {
                        Log.e("OCR_Download", "Failed to download language data")
                    }
                } catch (e: Exception)
                {
                    Log.e("OCR_Download", "Error downloading language data", e)
                }
            }
            return@withContext Pair(false, null)
        }
    }
}



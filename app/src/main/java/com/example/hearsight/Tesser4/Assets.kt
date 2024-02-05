package com.example.txtextrct

import android.app.ProgressDialog
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import com.example.hearsight.DataModel.DownloadTrainedDataTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Assets {

    fun getLocalDir(context: Context): File {
        return context.filesDir
    }

    fun getTessDataPath(context: Context): String {
        return getLocalDir(context).absolutePath
    }


    fun extractAssets(context: Context) {
        try {
        val am = context.assets
        val localDir = getLocalDir(context)
        if (!localDir.exists() && !localDir.mkdir()) {
            throw RuntimeException("Can't create directory $localDir")
        }
        val tessDir = File(getTessDataPath(context), "tessdata")
        if (!tessDir.exists() && !tessDir.mkdir()) {
            throw RuntimeException("Can't create directory $tessDir")
        }
            for (assetName in am.list("")!!) {
                val targetFile: File
                targetFile = if (assetName.endsWith(".traineddata"))
                {
                    File(tessDir, assetName)
                } else {
                    File(localDir, assetName)
                }
                if (!targetFile.exists()) {
                    //copyFile(am, assetName, targetFile)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyFile(am: AssetManager, assetName: String, outFile: File) {
        try {
            am.open(assetName).use { `in` ->
                FileOutputStream(outFile).use { out ->
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (`in`.read(buffer).also { read = it } != -1) {
                        out.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun bitmapToFile(context: Context,bitmap: Bitmap): File? {
        try {
            val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(filesDir, "image.jpg")
            val stream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            return imageFile
        }catch (e:Exception)
        {
            e.printStackTrace()
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return null
    }

    fun download_TrainedLanguage(context: Context, lang: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.max = 100
        progressDialog.setTitle("Alert")
        progressDialog.setMessage("Traineddata downloading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.show()
        progressDialog.setCancelable(false)
        val downloadTask = DownloadTrainedDataTask()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val (success, filePath) = downloadTask.downloadLanguage(context, lang) { process ->
                    progressDialog.progress = process
                }
                if (success) {
                    Config.ADD_NEW_LANG = lang
                    var language=""
                    when(lang)
                    {
                        "eng"-> language="English"
                        "hin"-> language="Hindi"
                        "tam"-> language="Tamil"
                        "kan"-> language="Kannada"
                        "tel"-> language="Telugu"
                        "mal"-> language="Malayalam"
                    }
                    Toast.makeText(context, "Download $language successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val fileAlreadyExists = File(filePath).exists()
                    if (fileAlreadyExists) {
                        Toast.makeText(context, "Language $lang already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Download failed. Check your network connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Download failed. File already exists or Check your network connection.", Toast.LENGTH_SHORT).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun isFolderEmpty(): Boolean
    {
        try {
            val directory = File("/data/user/0/com.example.hearsight/files/tessdata/")
            if (directory.exists() && directory.isDirectory)
            {
                val files = directory.listFiles()
                return files == null || files.isEmpty()
            }
            return true
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }
}
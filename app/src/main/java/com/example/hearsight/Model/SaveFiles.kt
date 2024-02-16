package com.example.hearsight.Model

import android.content.Context
import android.content.Context.STORAGE_SERVICE
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class SaveFiles(private val context: Context){
    private val sharedPreferencesHelper = SharedPreferenceBase(context)
    fun createDirectoryAndTextFile(context: Context, subDirectoryName: String, fileName: String, fileContent: ByteArray): String {
        try {
            val storageManager:StorageManager= context.getSystemService(STORAGE_SERVICE) as StorageManager
            val volume=storageManager.storageVolumes.get(0)
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
            {
                val parentDirectoryName="HearSightAudio"
                val parentDirectory=File(volume.directory!!.path+"/Download/$parentDirectoryName")
                if (!parentDirectory.exists())
                {
                    parentDirectory.mkdirs()
                }
                val childDirectory=File(parentDirectory,subDirectoryName)
                if (!childDirectory.exists())
                {
                    childDirectory.mkdirs()
                }
                val saveFile=File(childDirectory,fileName)
                FileOutputStream(saveFile).use { fileOutputStream->
                    fileOutputStream.write(fileContent)
                    Log.e("saved_file_path","${saveFile.absolutePath}")
                }
                return saveFile.absolutePath.toString()
                sharedPreferencesHelper.saveData("save_file",saveFile.absolutePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "File Not Saved", Toast.LENGTH_SHORT).show()
        }
         return ""
    }

}
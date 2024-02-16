package com.example.hearsight.Model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class FileShareFactors {
    fun shareAudioFile(context: Context, audioFile: File) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "audio/mp3"
        val contentUri = FileProvider.getUriForFile(
            context,
            "com.example.hearsight.fileprovider3",
            audioFile
        )
        Log.e("ContentUri","ContentUri:$contentUri")
        shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri)
        val title = "HS"
        val chooserIntent = Intent.createChooser(shareIntent, title)
        if (shareIntent.resolveActivity(context.packageManager) != null)
        {
            context.startActivity(chooserIntent)
        }
    }
}
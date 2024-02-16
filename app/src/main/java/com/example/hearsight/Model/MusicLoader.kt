package com.example.hearsight.Model

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.util.ArrayList

class MusicLoader(private val context: Context, private val contentResolver: ContentResolver) {
    val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val internalUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA
    )
    val selection = null
    val selectionArgs = null
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    val musicList = ArrayList<MusicItem>()

    @SuppressLint("Range")
    fun fetchMusicFiles(): ArrayList<MusicItem> {
        val internalCursor = contentResolver.query(
            internalUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        val externalCursor = contentResolver.query(
            externalUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        try {
            //addMusicItemsFromCursor(internalCursor)
            addMusicItemsFromCursor(externalCursor)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return musicList
        }
    }

    @SuppressLint("Range")
    private fun addMusicItemsFromCursor(cursor: android.database.Cursor?) {
        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID))
                val title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
                Log.e("URI_", "${Uri.fromFile(File(data))}")
                val musicItem = MusicItem(id, title, artist, data)
                musicList.add(musicItem)
            }
        }
    }

    fun renameMusicFile(oldFilePath: String): Boolean {
        val oldFile = File(oldFilePath)
        if (oldFile.exists()) {
            val newFileName = oldFile.name.replace(" ", "_")
            val newFilePath = oldFile.parent + File.separator + newFileName
            val newFile = File(newFilePath)
            if (oldFile.renameTo(newFile)) {
                updateMediaStore(oldFilePath, newFilePath)
                return true
            }
        }
        return false
    }

    private fun updateMediaStore(oldFilePath: String, newFilePath: String) {
        // Update media store with the new file path
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DATA, newFilePath)
        }

        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(oldFilePath)

        contentResolver.update(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            values,
            selection,
            selectionArgs
        )
    }
}

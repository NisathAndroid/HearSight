package com.example.hearsight.Model

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hearsight.R
import java.io.File


class ExistingFilesOpen:AppCompatActivity() {
    lateinit var file_Recyclerview:RecyclerView
    lateinit var existing_file_Adapter:ExistingFilesAdapter
    lateinit var musicplayerAdapter:MusicPlayerAdapter
    lateinit var gifparent:RelativeLayout
    lateinit var musicLoader:MusicLoader
    var pyPath=""
    companion object {
        var isMusic=false
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.existing_file_open_layout)
        file_Recyclerview=findViewById(R.id.savedFiles_recycler)
        gifparent=findViewById(R.id.gifparent)
        musicLoader= MusicLoader(this,contentResolver)
        val bundle=intent.extras
        Log.e("ismusic", isMusic.toString())
        try {
            if (isMusic==true)
            {
                musicAdapterfun()
                isMusic=false
            }
            else
            {
                var filePath=bundle!!.getString("directory_path")
                pyPath= bundle!!.getString("python_path")!!
                getAudioFiles(filePath!!)
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun musicAdapterfun() {
       val musicList=musicLoader.fetchMusicFiles()
        musicplayerAdapter= MusicPlayerAdapter(this,musicList,pyPath)
        file_Recyclerview.layoutManager=LinearLayoutManager(this)
        file_Recyclerview.adapter=musicplayerAdapter
        musicplayerAdapter.notifyDataSetChanged()
    }

    fun getAudioFiles(folderPath: String){
        val audioFiles = ArrayList<ExistingfilesDataClass>()
        try {
            val rootDirectory = File(folderPath)
            val files = rootDirectory.listFiles()
            if (files.isNotEmpty())
            {
                for (file in files) {
                    if (file.isFile && file.name.endsWith(".mp3"))
                    {
                        audioFiles.add(ExistingfilesDataClass(file.name,file.path))
                        if (audioFiles.isEmpty())
                        {
                            gifparent.visibility=View.VISIBLE
                        }
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(file.path)
                        existing_file_Adapter= ExistingFilesAdapter(this,audioFiles,pyPath)
                        val manager=LinearLayoutManager(this)
                        file_Recyclerview.layoutManager=manager
                        file_Recyclerview.adapter=existing_file_Adapter
                    }
                    else
                    {
                        Toast.makeText(this, "$file.name"+"${files.size.toString()}".toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(applicationContext, "File Empty", Toast.LENGTH_SHORT).show()
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
}


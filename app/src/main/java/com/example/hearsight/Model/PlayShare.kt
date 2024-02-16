package com.example.hearsight.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.hearsight.R
import java.io.File


class PlayShare:Activity(){
    lateinit var seekBar: SeekBar
    lateinit var previous:Button
    lateinit var next:Button
    lateinit var play:Button
    lateinit var file_name:TextView
    lateinit var mediaPlayer:MediaPlayer
    lateinit var musicLoader: MusicLoader
    var currentIndex=0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mediaplayer_ui)
        seekBar=findViewById(R.id.seekBar)
        file_name=findViewById(R.id.file_name)
        previous=findViewById(R.id.previous_btn)
        next=findViewById(R.id.next_btn)
        play=findViewById(R.id.Play_btn)
        musicLoader= MusicLoader(this,contentResolver)
        try {
            currentIndex = intent.getIntExtra("audio_position", 0)
            val selectedPath = intent.getStringExtra("selectedTitle")
            var path_arraylist: ArrayList<String>? = when {
                intent.hasExtra("path_list") -> {
                    intent.getStringArrayListExtra("path_list")
                }
                intent.hasExtra("songs_path_arraylist") -> {
                    intent.getStringArrayListExtra("songs_path_arraylist")
                }
                else -> null
            }
            file_name.text = selectedPath.toString()
            if (selectedPath != null && path_arraylist?.isNotEmpty() == true) {
                getAudioPath(path_arraylist)
            } else {
                Toast.makeText(this, "File empty", Toast.LENGTH_SHORT).show()
            }
            intent.replaceExtras(Bundle())

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getAudioPath(file_path_list: ArrayList<String>?)
    {
        mediaPlayer=MediaPlayer()
        mediaPlayer.setDataSource(this, Uri.fromFile(File(file_path_list!![currentIndex])))
        mediaPlayer.prepare()
        play.setOnClickListener {
            if (!(mediaPlayer.isPlaying))
            {
                mediaPlayer.start()
                play.text="Pause"
            }
           else if (mediaPlayer.isPlaying)
            {
               mediaPlayer.pause()
               play.text="Play"
            }
            initializeSeekBar()
        }
        next.setOnClickListener {
            if (currentIndex < file_path_list.size - 1) {
                currentIndex++
                playMediaAtIndex(currentIndex,file_path_list)
            } else
            {
                Toast.makeText(this, "file end", Toast.LENGTH_SHORT).show()
            }
        }
        previous.setOnClickListener {
            playPrevious(file_path_list)
        }

        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    private fun initializeSeekBar() {
        seekBar.max=mediaPlayer.duration
        val handler=Handler()
        handler.postDelayed(object :Runnable{
            override fun run() {
                try {
                    seekBar.progress=mediaPlayer.currentPosition
                    handler.postDelayed(this,1000)
                }catch (e:Exception)
                {
                    seekBar.progress=0
                }
            }

        },0)
    }

    private fun playPrevious(file_path_list: ArrayList<String>) {
        if (currentIndex > 0) {
            currentIndex--
            playMediaAtIndex(currentIndex, file_path_list)
        } else
        {
            Toast.makeText(this, "Previous file empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playMediaAtIndex(currentIndex: Int, path_arraylist: ArrayList<String>) {
        if (currentIndex >= 0 && currentIndex < path_arraylist.size) {
            val mediaPath = path_arraylist[currentIndex]
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(mediaPath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                play.text="Pause"
                this.currentIndex = currentIndex
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.reset()
    }
}
package com.example.hearsight.Activity

import TextToSpeechHelper
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hearsight.DataModel.SaveFiles
import com.example.hearsight.DataModel.TextExtract
import com.example.hearsight.R


class ExcelActivity : AppCompatActivity(),TextToSpeechHelper.OnInitListener{
    private lateinit var extractpdf_txt: TextView
    private lateinit var play: Button
    private lateinit var saveBtn: Button
    lateinit var uriData: Uri
    lateinit var fileConverter: TextExtract
    private lateinit var ttsHelper: TextToSpeechHelper
    lateinit var saveFiles:SaveFiles

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_excel)
        ttsHelper = TextToSpeechHelper(this, this)
        extractpdf_txt = findViewById(R.id.extractpdf_txt) as TextView
        saveBtn = findViewById(R.id.saveBtn) as Button
        play = findViewById(R.id.play) as Button
        saveFiles=SaveFiles(this)
        fileConverter= TextExtract(this)
        val uriData = intent.getParcelableExtra<Uri>("excel_uriData")
        if (uriData!=null)
        {
            val contentResolver: ContentResolver = this.contentResolver
            val textData=fileConverter.getTextFromExcel(uriData,contentResolver)
            extractpdf_txt.text=textData
            play.setOnClickListener {
                val textName=play.text.toString()
                if (textName.equals("Play"))
                {
                    ttsHelper.readTableWithTTS(textData.toString())
                    play.text="Pause"
                }else{
                    ttsHelper.pauseSpeech()
                    play.text="Play"
                }
            }

            saveBtn.setOnClickListener {
                saveFile(textData!!)
            }
        }
        ttsHelper = TextToSpeechHelper(this, this)
    }

    fun saveFile(text: String): String {
        ttsHelper = TextToSpeechHelper(this, object : TextToSpeechHelper.OnInitListener {
            override fun onInitSuccess() {
                val actualFileName = intent.getStringExtra("actual_filename")
                val modifiedName=actualFileName!!.replace(" ","_")
                val fileName = "$modifiedName.mp3"
                val childDirectory="ExcelAudios"
                ttsHelper.getAudioByteArray(this@ExcelActivity,text,object: TextToSpeechHelper.OnAudioGeneratedListener {
                    override fun onAudioGenerated(audioData: ByteArray?) {
                        if(audioData!=null)
                        {
                            val filePath=saveFiles.createDirectoryAndTextFile(this@ExcelActivity,childDirectory,fileName, audioData)
                            runOnUiThread {
                                if (filePath.isNotEmpty()){
                                    Toast.makeText(this@ExcelActivity, "Successfully saved file", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }else
                            Toast.makeText(this@ExcelActivity, "Audio File Empty", Toast.LENGTH_SHORT).show()

                    }
                })
                //val audioByteArray=ttsHelper.getAudioByteArray(text)
                //saveFiles.createDirectoryAndTextFile(this,childDirectory,fileName, audioByteArray!! )
            }
            override fun onInitFailed() {
                Toast.makeText(this@ExcelActivity, "Initialization Failed", Toast.LENGTH_SHORT).show()
            }
        })
        return ""
    }

    override fun onInitSuccess() {

    }

    override fun onInitFailed() {

    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper.stopSpeech()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
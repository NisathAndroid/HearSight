package com.example.hearsight.Activity

import TextToSpeechHelper
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hearsight.Model.ResultTextPreProcessing
import com.example.hearsight.Model.SaveFiles
import com.example.hearsight.Model.SharedPreferenceBase
import com.example.hearsight.Model.TextExtract
import com.example.hearsight.R

class WordActivity : AppCompatActivity(),TextToSpeechHelper.OnInitListener {
    private lateinit var extractpdf_txt: TextView
    private lateinit var play: Button
    private lateinit var saveBtn: Button
    lateinit var pdfConverter: TextExtract
    private lateinit var ttsHelper: TextToSpeechHelper
    lateinit var saveFiles:SaveFiles
    private lateinit var sharedPreferenceBase: SharedPreferenceBase
    private lateinit var resulttextPreProcessing: ResultTextPreProcessing
    var text=""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_document)
        saveFiles=SaveFiles(this)
        ttsHelper=TextToSpeechHelper(this,this)
        extractpdf_txt = findViewById(R.id.extractpdf_txt) as TextView
        saveBtn = findViewById(R.id.saveBtn) as Button
        play = findViewById(R.id.play) as Button
        pdfConverter= TextExtract(this)
        sharedPreferenceBase=SharedPreferenceBase(this)
        val uriData = intent.getParcelableExtra<Uri>("docs_uriData")
        resulttextPreProcessing=ResultTextPreProcessing(this)
        if (uriData!=null)
        {
            val contentResolver=this.contentResolver
            val textResult=pdfConverter.extractTextFromDocument(uriData,contentResolver)
            text=resulttextPreProcessing.removeUnwantedSpaces(textResult)
            extractpdf_txt.text=text
            play.setOnClickListener {
                val textName=play.text.toString()
                if (textName.equals("Play"))
                {
                    ttsHelper.speak(text)
                    Log.e("ttstext",text.toString())
                    play.text="Pause"
                }else{
                    ttsHelper.pauseSpeech()
                    play.text="Play"
                }
            }


            saveBtn.setOnClickListener {
                saveFile(text)
            }
        }
        ttsHelper = TextToSpeechHelper(this, this)
    }

    fun saveFile(text: String): String {
        ttsHelper = TextToSpeechHelper(this, object : TextToSpeechHelper.OnInitListener {
            override fun onInitSuccess() {
                val actualFileName = intent.getStringExtra("actual_filename")
                val changedName=actualFileName!!.replace(" ","_")
                val fileName = "$changedName.mp3"
                val childDirectory="DocumentAudios"
                ttsHelper.getAudioByteArray(this@WordActivity,text,object: TextToSpeechHelper.OnAudioGeneratedListener {
                    override fun onAudioGenerated(audioData: ByteArray?) {
                        if(audioData!=null)
                        {
                            val fileptha=saveFiles.createDirectoryAndTextFile(this@WordActivity,childDirectory,fileName, audioData)
                            runOnUiThread {
                                if (fileptha.isNotEmpty())
                                {
                                    Toast.makeText(this@WordActivity, "File Successfully saved", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }else
                            Toast.makeText(this@WordActivity, "Audio File Empty", Toast.LENGTH_SHORT).show()

                    }
                })

                //val audioByteArray=ttsHelper.getAudioByteArray(text)
                //saveFiles.createDirectoryAndTextFile(this,childDirectory,fileName,audioByteArray!!)
            }
            override fun onInitFailed() {
                Toast.makeText(this@WordActivity, "Initialization Failed", Toast.LENGTH_SHORT).show()
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
}
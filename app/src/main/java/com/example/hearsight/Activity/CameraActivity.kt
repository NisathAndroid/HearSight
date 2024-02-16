package com.example.hearsight.Activity

import TextToSpeechHelper
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import com.example.hearsight.Model.BottomSheetview
import com.example.hearsight.Model.SaveFileNameDialog
import com.example.hearsight.Model.SaveFiles
import com.example.hearsight.Model.TextExtract
import com.example.hearsight.R
import com.example.txtextrct.Assets
import com.example.txtextrct.Config
import com.example.txtextrct.TesseractMainManager
import java.io.File

class CameraActivity : AppCompatActivity(), TextToSpeechHelper.OnInitListener {

    private lateinit var resulText: TextView
    private lateinit var playButton: Button
    private lateinit var saveButton: Button
    private lateinit var stopButton: Button
    private lateinit var textExtract: TextExtract
    private lateinit var ttsHelper: TextToSpeechHelper
    private lateinit var saveFiles: SaveFiles
    private lateinit var mainViewModel: TesseractMainManager
    private lateinit var bottomSheetview: BottomSheetview
    private lateinit var savefileNameDialog: SaveFileNameDialog
    private var extractText = ""
    private var actualFilename = ""
    private var isPaused=false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_camera)
        ttsHelper = TextToSpeechHelper(this, this)
        resulText = findViewById(R.id.extractpdf_txt) as TextView
        saveButton = findViewById(R.id.saveBtn) as Button
        playButton = findViewById(R.id.play) as Button
        stopButton = findViewById(R.id.stopBtn) as Button
        saveFiles = SaveFiles(this)
        textExtract = TextExtract(this)
        mainViewModel = TesseractMainManager(this)
        bottomSheetview= BottomSheetview(this)
        savefileNameDialog= SaveFileNameDialog(this)
        Assets.extractAssets(this)
        val bitmapUriData: Bitmap = intent.getParcelableExtra("capture_image")!!
        val file = intent.getSerializableExtra("capture_image_uri") as File
        actualFilename = file.name
        if (bitmapUriData != null) {
            val dataPath = Assets.getTessDataPath(this)
            mainViewModel.initTesseract(dataPath, Config.TESS_LANG, Config.TESS_ENGINE)
            extractText = textExtract.google_extractTextFromImage(bitmapUriData).toString()
            if (extractText.isNotEmpty())
            {
                resulText.text = extractText
                bottomSheetview.showBottomDialog()
            }else{
                val errormsg="The picture is not clear. Please take a clear and zoomed-in photo."
                resulText.text=errormsg
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show()
            }
            bottomSheetview.getPlayBtn().first.setOnClickListener {
                val play_pause=bottomSheetview.getPlayBtn().second
                when(play_pause.text)
                {
                    "Play"->
                    {
                        ttsHelper.speak(extractText)
                        play_pause.text="Pause"
                    }

                    "Pause"->
                    {
                        isPaused=true
                        ttsHelper.pauseSpeech()
                        play_pause.text="Play"
                    }
                }
            }

            bottomSheetview.getSaveBtn().setOnClickListener {
                savefileNameDialog.initializeDialog(object :SaveFileNameDialog.GetSaveDialogDts{
                    override fun getsavedialog(saveDilog: Dialog, get_name: EditText, error_msg: TextView, save_btn: CardView) {
                        var fileName=""
                        get_name.doOnTextChanged { text, start, before, count ->
                            text?.toString()?.let { newText ->
                                if (newText.length > before) {
                                    val addedText = newText.substring(start, start + count)
                                    ttsHelper.speak(addedText)
                                    fileName = newText
                                    ttsHelper.speak(fileName)
                                } else if (newText.length < before) {
                                    val deletedChar = fileName.getOrNull(start) ?: ""
                                    ttsHelper.speak("backspace $deletedChar")
                                    fileName = newText
                                    ttsHelper.speak(fileName)
                                }
                            }
                        }

                    save_btn.setOnClickListener {
                    if (fileName.isEmpty()) {
                        error_msg.visibility = View.VISIBLE
                    } else {
                        saveFile(extractText,fileName)
                        saveDilog.dismiss()
                        bottomSheetview.cancelBottomDialog()
                    }
                }
               }
                })
            }
            bottomSheetview.getCancelBtn().setOnClickListener {
                bottomSheetview.cancelBottomDialog()
                finish()
            }
        }
        ttsHelper = TextToSpeechHelper(this, this)
    }

    private fun saveFile(text: String, fileName: String) {
        ttsHelper = TextToSpeechHelper(this, object : TextToSpeechHelper.OnInitListener {
            override fun onInitSuccess() {
                val modifiedName = fileName.replace(" ", "_")
                val fileName = "$modifiedName.mp3"
                val childDirectory = "CameraAudios"
                ttsHelper.getAudioByteArray(this@CameraActivity, text, object :
                    TextToSpeechHelper.OnAudioGeneratedListener {
                    override fun onAudioGenerated(audioData: ByteArray?) {
                        if (audioData != null) {
                            val saved_file_path=saveFiles.createDirectoryAndTextFile(this@CameraActivity, childDirectory, fileName, audioData)
                            if (saved_file_path.isNotEmpty())
                            {
                                runOnUiThread {
                                    Toast.makeText(this@CameraActivity, "Save Successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this@CameraActivity, "Audio File Empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }

            override fun onInitFailed() {
                Toast.makeText(this@CameraActivity, "Initialization Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onInitSuccess() {
        // Implement if needed
    }

    override fun onInitFailed() {
        // Implement if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper.stopSpeech()
    }
}

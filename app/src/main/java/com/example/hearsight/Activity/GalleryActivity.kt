package com.example.hearsight.Activity

import TextToSpeechHelper
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import com.example.hearsight.DataModel.BottomSheetview
import com.example.hearsight.DataModel.ProgressDialogCls
import com.example.hearsight.DataModel.SaveFileNameDialog
import com.example.hearsight.DataModel.SaveFiles
import com.example.hearsight.DataModel.SharedPreferenceBase
import com.example.hearsight.DataModel.TextExtract
import com.example.hearsight.R
import com.example.txtextrct.Assets
import com.example.txtextrct.Config
import com.example.txtextrct.TesseractMainManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GalleryActivity : AppCompatActivity(), TextToSpeechHelper.OnInitListener {
    private lateinit var resultTxt: TextView
    private lateinit var textextractorBtn: Button
    private lateinit var bottomSheetview: BottomSheetview
    private lateinit var selectedImg: ImageView
    private lateinit var ttsHelper: TextToSpeechHelper
    private lateinit var saveFiles: SaveFiles
    private lateinit var textExtract: TextExtract
    private lateinit var mainViewModel: TesseractMainManager
    private lateinit var sharedPreferenceBase: SharedPreferenceBase
    private lateinit var savefileNameDialog: SaveFileNameDialog
    private lateinit var pleaseWaitDialog: ProgressDialogCls
    private var bitmapUriData: Uri? = null
    private var temtextStore: String = ""
    private var isPaused=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_gallery)
        initializeViews()
        initializeHelpers()
        setupListeners()
    }

    private fun initializeViews() {
        resultTxt = findViewById(R.id.extractpdf_txt)
        selectedImg = findViewById(R.id.choosedImage)
        textextractorBtn = findViewById(R.id.play)
        bottomSheetview = BottomSheetview(this)
        savefileNameDialog= SaveFileNameDialog(this)
        pleaseWaitDialog=ProgressDialogCls(this)
    }

    private fun initializeHelpers() {
        ttsHelper = TextToSpeechHelper(this, this)
        saveFiles = SaveFiles(this)
        textExtract = TextExtract(this)
        mainViewModel = TesseractMainManager(this)
        sharedPreferenceBase = SharedPreferenceBase(this)
        Assets.extractAssets(this)
        sharedPreferenceBase.clearSharedPreference()?.clear()
        bitmapUriData= intent.getParcelableExtra("gallery_image")
        selectedImg.setImageURI(bitmapUriData)
    }

    private fun setupListeners() {
        textextractorBtn.setOnClickListener {
            runOnUiThread {
                Toast.makeText(this, "Please wait to extract the text", Toast.LENGTH_SHORT).show()
            }
            temtextStore = textExtractwork()
            if (temtextStore.isNotEmpty()) {
                selectedImg.visibility = View.GONE
                resultTxt.text = temtextStore
                Toast.makeText(this, "Successfully text extracted", Toast.LENGTH_SHORT).show()
                resultTxt.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Text extract failed", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetview.getPlayBtn().first.setOnClickListener {
            val play_pause=bottomSheetview.getPlayBtn().second
            when(play_pause.text)
            {
                "Play"->
                {
                    ttsHelper.speak(temtextStore)
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
                override fun getsavedialog(saveDilog: Dialog,get_name: EditText, error_msg: TextView, save_btn: CardView)
                {
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
                            saveFile(temtextStore,fileName,saveDilog)
                            bottomSheetview.cancelBottomDialog()
                        }
                    }
                }

            })

        }


        bottomSheetview.getCancelBtn().setOnClickListener {
            bottomSheetview.showBottomDialog()
            finish()
        }

        bottomSheetview.getCancelBtn().setOnClickListener {
            bottomSheetview.destroy()
            finish()
        }
    }

    private fun textExtractwork(): String {
        val fileUri = uriToFile(bitmapUriData!!)
        val filePath = File(fileUri.toString())
        //val googlevisionApi=GoogleVisionApi()
        //val text=googlevisionApi.google_loud_visionApi(filePath.toString())
        val dataPath = Assets.getTessDataPath(this)
        mainViewModel.initTesseract(dataPath, Config.TESS_LANG, Config.TESS_ENGINE)
        var extractedText = ""
        runOnUiThread {
            extractedText = mainViewModel.recognizeImage(filePath).toString()
            if (extractedText.isNotEmpty()) {
                bottomSheetview.showBottomDialog()
            }
        }
        return extractedText
    }

    private fun saveFile(text: String, fileName: String, saveDilog: Dialog) {
        saveDilog.dismiss()
        try {
            ttsHelper = TextToSpeechHelper(this, object : TextToSpeechHelper.OnInitListener {
                override fun onInitSuccess()
                {
                    val actualFileName = intent.getStringExtra("actual_filename")
                    val modifiedname=fileName!!.replace(" ","_")
                    val fileName = "$modifiedname.mp3"
                    val childDirectory = "GalleryAudios"
                    ttsHelper.getAudioByteArray(this@GalleryActivity, text, object : TextToSpeechHelper.OnAudioGeneratedListener {
                        override fun onAudioGenerated(audioData: ByteArray?) {
                            if (audioData != null) {
                                val filePath=saveFiles.createDirectoryAndTextFile(this@GalleryActivity, childDirectory, fileName, audioData)
                                if (filePath.isNotEmpty())
                                {
                                    runOnUiThread {
                                        Toast.makeText(this@GalleryActivity, "Save Successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                                else
                                {
                                    runOnUiThread {
                                        Toast.makeText(this@GalleryActivity, "Save Successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            } else
                            {
                                Toast.makeText(this@GalleryActivity, "Audio File Empty", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    })
                }

                override fun onInitFailed() {
                    Toast.makeText(this@GalleryActivity, "Initialization Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun convertBitmap(uri: Uri?): Bitmap? {
        return try {
            val inputStream = this.contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream!!.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    fun uriToFile(contentUri: Uri): File? {
        val contentResolver = this.contentResolver
        val outputFile = File(this.cacheDir, "temp_file")
        try {
            val inputStream = contentResolver.openInputStream(contentUri)
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream?.read(buffer).also { bytesRead = it ?: 0 } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream?.close()
            outputStream.close()
            return outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper.stopSpeech()
    }

    override fun onInitSuccess() {
        // Handle TTS initialization success if needed.
    }

    override fun onInitFailed() {
        // Handle TTS initialization failure if needed.
    }
}


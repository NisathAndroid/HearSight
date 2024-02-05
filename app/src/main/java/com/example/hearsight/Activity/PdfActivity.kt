package com.example.hearsight.Activity

import TextToSpeechHelper
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hearsight.DataModel.LanguageDialog
import com.example.hearsight.DataModel.NotificationProgressbar
import com.example.hearsight.DataModel.PdftoBitmap
import com.example.hearsight.DataModel.SaveFileNameDialog
import com.example.hearsight.DataModel.SaveFiles
import com.example.hearsight.DataModel.SharedPreferenceBase
import com.example.hearsight.DataModel.ProgressDialogCls
import com.example.hearsight.DataModel.TextExtract
import com.example.hearsight.DataModel.TextExtractionBackgroundTask
import com.example.hearsight.Interface.FetchData
import com.example.hearsight.R
import com.example.txtextrct.Assets
import com.example.txtextrct.Config
import com.example.txtextrct.TesseractMainManager
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.lang.NullPointerException

class PdfActivity : AppCompatActivity(), TextToSpeechHelper.OnInitListener, FetchData {
    private lateinit var extractPdfTxt: TextView
    private lateinit var play: Button
    private lateinit var saveBtn: Button
    private lateinit var nextTxtBtn: TextView
    private lateinit var previousTxtBtn: TextView
    private lateinit var uriData: Uri
    private lateinit var textExtract: TextExtract
    private lateinit var ttsHelper: TextToSpeechHelper
    private lateinit var tesseractManager: TesseractMainManager
    private lateinit var splitPdfFile: PdftoBitmap
    private lateinit var saveFileNameDialog: SaveFileNameDialog
    private lateinit var simpleDialogBox: ProgressDialogCls
    private lateinit var pageNo: TextView
    private var textResultExtraction = ""
    private lateinit var saveFiles: SaveFiles
    private lateinit var languageDialog: LanguageDialog
    private lateinit var rootView: ScrollView
    private lateinit var sharedPreferenceBase: SharedPreferenceBase
    private var isTextRecognitionInProgress = false
    private var currentPage = 0
    private var totalPage = 0
    lateinit var md:Dialog

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_bank_statement)

        initializeViews()
        initializeComponents()
        createNotificationChannel()
        processIntentData()
    }

    private fun createNotificationChannel()
    {
        val notificationProgressbar = NotificationProgressbar(this)
        notificationProgressbar.createNotificationChannel()
    }

    private fun initializeViews() {
        extractPdfTxt = findViewById(R.id.extractpdf_txt)
        saveBtn = findViewById(R.id.saveBtn)
        play = findViewById(R.id.play)
        nextTxtBtn = findViewById(R.id.next_id)
        previousTxtBtn = findViewById(R.id.previous_id)
        pageNo = findViewById(R.id.page_no)
        rootView = findViewById(R.id.rootView)
    }

    private fun initializeComponents() {
        saveFiles = SaveFiles(this)
        ttsHelper = TextToSpeechHelper(this, this)
        textExtract = TextExtract(this)
        languageDialog = LanguageDialog(this)
        splitPdfFile = PdftoBitmap(this)
        tesseractManager = TesseractMainManager(this)
        saveFileNameDialog = SaveFileNameDialog(this)
        simpleDialogBox = ProgressDialogCls(this)
        sharedPreferenceBase = SharedPreferenceBase(this)
        Assets.extractAssets(this)
    }

    private fun processIntentData() {
        uriData = intent.getParcelableExtra<Uri>("uriData")!!
        try {
            if (uriData != null) {
                handlePdfIntent(uriData)
            }
        } catch (e: NullPointerException) {
            Log.e("PDF_ACT", "${e.toString()}")
        }
    }

    private fun handlePdfIntent(uriData: Uri) {
        md = simpleDialogBox.mDialog()
        md.show()
        val identifyLangCode = textExtract.extractTextFromPDF(uriData)
        textExtract.languageDetection(identifyLangCode.toString())
        Config.DETECT_TEXT = sharedPreferenceBase.getString("lang_code", "")
        sharedPreferenceBase.clearSharedPreference()
        splitPdfFile.splitPdfPages(uriData, object : PdftoBitmap.GetPdfFileDetails {
            override fun getdetails(imageFilePathList: MutableList<String>, totalPages: Int, pageIndex: Int) {
                totalPage = totalPages
                if (imageFilePathList.isNotEmpty()) {
                    initTesseract()
                    handleTextRecognition(md, imageFilePathList)
                    setupButtonClickListeners(imageFilePathList)
                }
            }
        })
    }

    private fun initTesseract() {
        val dataPath = Assets.getTessDataPath(this@PdfActivity)
        md.dismiss()
        if (Config.DETECT_TEXT == "kn") {
            tesseractManager.initTesseract(dataPath, Config.DETECT_TEXT, Config.TESS_ENGINE)
        } else {
            tesseractManager.initTesseract(dataPath, Config.TESS_LANG, Config.TESS_ENGINE)
        }
    }

    private fun handleTextRecognition(md: Dialog, imageFilePathList: MutableList<String>) {
        textResultExtraction = tesseractManager.recognizeImage(File(imageFilePathList[currentPage]))!!
        if (textResultExtraction.isEmpty()) {
            textResultExtraction = textExtract.extractTextFromPDF(uriData).toString()
            runOnUiThread {
                extractPdfTxt.text = textResultExtraction
                md.dismiss()
            }
        }

        runOnUiThread {
            extractPdfTxt.text = textResultExtraction
            pageNo.text = "TotalPages:\t" + "" + "$totalPage"
            md.dismiss()
        }
    }

    private fun setupButtonClickListeners(imageFilePathList: MutableList<String>) {
        runOnUiThread {
            try {
                nextTxtBtn.setOnClickListener {
                    play.text="Pause"
                    ttsHelper.pauseSpeech()
                    handleNextButton(imageFilePathList)
                }

                previousTxtBtn.setOnClickListener {
                    play.text="Pause"
                    ttsHelper.pauseSpeech()
                    handlePreviousButton(imageFilePathList)
                }

                play.setOnClickListener {
                    handlePlayButton()
                }

                saveBtn.setOnClickListener {
                    handleSaveButton(imageFilePathList)
                }
            }catch (e:Exception)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun handleNextButton(imageFilePathList: MutableList<String>) {
        if (isTextRecognitionInProgress) {
            return
        }
        nextTxtBtn.isEnabled = false
        Toast.makeText(this@PdfActivity, "Please wait to extract the text", Toast.LENGTH_SHORT).show()
        if (currentPage < totalPage) {
            currentPage++
            if (currentPage < imageFilePathList.size) {
                isTextRecognitionInProgress = true
                performTextRecognition(imageFilePathList)
            }
        } else {
            val snackBar = Snackbar.make(rootView, "Page end", Snackbar.LENGTH_LONG)
            snackBar.show()
        }
    }

    private fun handlePreviousButton(imageFilePathList: MutableList<String>) {
        if (isTextRecognitionInProgress) {
            return
        }
        previousTxtBtn.isEnabled = false
        if (currentPage >= 0) {
            currentPage--
            if (currentPage >= 0) {
                isTextRecognitionInProgress = true
                performTextRecognition(imageFilePathList)
            }
        } else {
            val snackBar = Snackbar.make(rootView, "You are on the first page", Snackbar.LENGTH_LONG)
            snackBar.show()
        }
    }

    private fun performTextRecognition(imageFilePathList: MutableList<String>) {
        Thread {
            textResultExtraction = tesseractManager.recognizeImage(File(imageFilePathList[currentPage])) ?: ""
            runOnUiThread {
                if (textResultExtraction.isNotEmpty()) {
                    extractPdfTxt.text = textResultExtraction
                }
                ttsHelper.speak(textResultExtraction)
                isTextRecognitionInProgress = false
                nextTxtBtn.isEnabled = true
                previousTxtBtn.isEnabled = true
            }
        }.start()
    }

    private fun handlePlayButton() {
            val textName=play.text.toString()
            if (textName.equals("Play"))
            {
                ttsHelper.speak(textResultExtraction)
                play.text="Pause"
            }else{
                ttsHelper.pauseSpeech()
                play.text="Play"
            }
    }


    fun handleSaveButton(imageFilePathList: MutableList<String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Toast.makeText(this@PdfActivity, "Please wait", Toast.LENGTH_SHORT).show()

        TextExtractionBackgroundTask(
            this@PdfActivity,
            imageFilePathList,
            tesseractManager,
            notificationManager).execute()

//        saveFileNameDialog.initializeDialog(object : SaveFileNameDialog.GetSaveDialogDts {
//            override fun getsavedialog(saveDialog: Dialog, getName: EditText, errorMsg: TextView, saveBtn: CardView) {
//                var fileName = ""
//                getName.doOnTextChanged { text, _, _, _ ->
//                    text?.toString()?.let {
//                        if (it.isNotEmpty()) {
//                            ttsHelper.speak(it)
//                            fileName = it
//                        }
//                    }
//                }
//                saveBtn.setOnClickListener {
//                    if (fileName.isEmpty()) {
//                        errorMsg.visibility = View.VISIBLE
//                    } else {
//
//                    }
//                }
//            }
//        })
    }

    fun handleSaveButtonAsyn(result: String) {
        saveFile(result)
        isTextRecognitionInProgress = false
        saveBtn.visibility = View.VISIBLE
    }


    private fun saveFile(text: String) {
        try {
            ttsHelper = TextToSpeechHelper(this, object : TextToSpeechHelper.OnInitListener {
                override fun onInitSuccess() {
                    val actualFileName = intent.getStringExtra("actual_filename") as String
                    val modifiedName=actualFileName.replace(" ","_")
                    val fileName = "$modifiedName.mp3"
                    val childDirectory = "PdfFileAudios"
                    ttsHelper.getAudioByteArray(this@PdfActivity, text, object : TextToSpeechHelper.OnAudioGeneratedListener {
                        override fun onAudioGenerated(audioData: ByteArray?) {
                            if (audioData != null) {
                                val filePath=saveFiles.createDirectoryAndTextFile(this@PdfActivity, childDirectory, fileName, audioData)
                                runOnUiThread {
                                    if (filePath.isNotEmpty())
                                    {
                                        Toast.makeText(this@PdfActivity, "Successfully saved", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            } else {
                                Toast.makeText(this@PdfActivity, "Audio File Empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }

                override fun onInitFailed() {
                    Toast.makeText(this@PdfActivity, "Initialization Failed", Toast.LENGTH_SHORT).show()
                }
            })

        }catch (e:Exception)
        {

        }
    }

    override fun langDetect(result: String) {
        Toast.makeText(this, "$result", Toast.LENGTH_SHORT).show()
    }

    override fun onInitSuccess() {
    }

    override fun onInitFailed() {
    }

    override fun onDestroy() {
        super.onDestroy()
        this.stopService(Intent(this, PdftoBitmap::class.java))
        ttsHelper.stopSpeech()
    }
}

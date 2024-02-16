package com.example.hearsight.Fragments

import TextToSpeechHelper
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import com.example.hearsight.Model.PdftoBitmap
import com.example.hearsight.Model.SaveFileNameDialog
import com.example.hearsight.Model.SaveFiles
import com.example.hearsight.Model.TextExtract
import com.example.hearsight.R
import com.example.txtextrct.Assets
import com.example.txtextrct.Config
import com.example.txtextrct.TesseractMainManager
import com.google.android.material.snackbar.Snackbar
import java.io.File

class E_Book : Fragment(),TextToSpeechHelper.OnInitListener {
    private lateinit var extractpdf_txt: TextView
    private lateinit var play: Button
    private lateinit var saveBtn: Button
    private lateinit var stopBtn: Button
    lateinit var uriData: Uri
    lateinit var pdfConverter: TextExtract
    private lateinit var ttsHelper: TextToSpeechHelper
    lateinit var saveFiles:SaveFiles
    private lateinit var spilitpdfFile: PdftoBitmap
    private var totalPage=0
    private lateinit var mainViewModel:TesseractMainManager
    private var extractText=""
    private lateinit var ebook_previous_id:TextView
    private lateinit var ebook_next_id:TextView
    private lateinit var ebook_page_no:TextView
    private var nextPage=0
    private lateinit var ebook_rootview:ScrollView
    private var tts_text=""
    private lateinit var savefilenameDialog:SaveFileNameDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_ebook, container, false)
        ttsHelper = TextToSpeechHelper(requireContext(), this)
        extractpdf_txt = view.findViewById(R.id.extractpdf_txt) as TextView
        saveBtn = view.findViewById(R.id.saveBtn) as Button
        play = view.findViewById(R.id.play) as Button
        stopBtn = view.findViewById(R.id.stopBtn) as Button
        ebook_next_id = view.findViewById(R.id.ebook_next_id) as TextView
        ebook_previous_id = view.findViewById(R.id.ebook_previous_id) as TextView
        ebook_page_no = view.findViewById(R.id.ebook_page_no) as TextView
        ebook_rootview = view.findViewById(R.id.ebook_rootview) as ScrollView
        pdfConverter= TextExtract(requireContext())
        saveFiles=SaveFiles(requireContext())
        spilitpdfFile= PdftoBitmap(requireContext())
        mainViewModel= TesseractMainManager(requireContext())
        savefilenameDialog= SaveFileNameDialog(requireContext())
        uriData = arguments?.getParcelable("ebook_uriData")!!
        if (uriData!=null)
        {
            val serviceIntent= Intent(requireContext(),PdftoBitmap::class.java)
            serviceIntent.putExtra("pdfUri",uriData)
            requireContext().startService(serviceIntent)

                spilitpdfFile.splitPdfPages(uriData,object : PdftoBitmap.GetPdfFileDetails {
                    override fun getdetails(imagefilepathlist: MutableList<String>, totalPages: Int, pageIndex: Int) {
                        totalPage=totalPages
                        val dataPath = Assets.getTessDataPath(requireContext())
                        mainViewModel.initTesseract(dataPath, Config.TESS_LANG, Config.TESS_ENGINE)
                        extractText= mainViewModel.recognizeImage(File(imagefilepathlist[nextPage]))!!
                        extractpdf_txt.text=extractText
                        ebook_page_no.text="Page No:\t1"+"/"+"$totalPage"
                        if (extractText.isNotEmpty()){
                            ttsHelper.speak(extractText)
                        }
                        ebook_next_id.setOnClickListener {
                            if (nextPage < totalPage) {
                                nextPage++
                                if (nextPage < imagefilepathlist.size) {
                                    extractText = mainViewModel.recognizeImage(File(imagefilepathlist[nextPage]))!!
                                    extractpdf_txt.text = extractText
                                    ttsHelper.speak(extractText)
                                }
                            } else
                            {
                                val snackBar = Snackbar.make(ebook_rootview, "Page end", Snackbar.LENGTH_LONG)
                                snackBar.show()
                            }
                        }
                        ebook_previous_id.setOnClickListener {
                            if (nextPage > 0) {
                                nextPage--
                                extractText = mainViewModel.recognizeImage(File(imagefilepathlist[nextPage]))!!
                                extractpdf_txt.text = extractText
                                ttsHelper.speak(extractText)
                            } else {
                                val snackBar = Snackbar.make(ebook_rootview, "You are on the first page", Snackbar.LENGTH_LONG)
                                snackBar.show()
                            }
                        }
                        play.setOnClickListener {
                            ttsHelper.speak(extractText)
                        }
                        stopBtn.setOnClickListener {
                            ttsHelper.stopSpeech()
                            saveBtn.visibility=View.VISIBLE
                        }
                        saveBtn.setOnClickListener {
                            extractText= mainViewModel.recognizeImage(File(imagefilepathlist[nextPage]))!!
                            extractpdf_txt.text=extractText
                            val textBuilder = StringBuilder()
                            for(i in 0 until imagefilepathlist.size)
                            {
                                val text= mainViewModel.recognizeImage(File(imagefilepathlist[i]))!!
                                text.let {
                                    textBuilder.append(it)
                                    textBuilder.append("\n")
                                }
                            }
                            tts_text=textBuilder.toString()

                            savefilenameDialog.initializeDialog(object : SaveFileNameDialog.GetSaveDialogDts{
                                override fun getsavedialog(saveDialog: Dialog, get_name: EditText, error_msg: TextView, save_btn: CardView) {
                                    var file_name = ""
                                    get_name.doOnTextChanged { text, _, _, _ ->
                                        text?.toString()?.let {
                                            if (it.isNotEmpty()) {
                                                ttsHelper.speak(it)
                                                file_name = it
                                            }
                                        }
                                    }
                                    save_btn.setOnClickListener {
                                        if (file_name.isEmpty()) {
                                            error_msg.visibility = View.VISIBLE
                                        } else {
                                            saveFile(tts_text, saveDialog, file_name)
                                        }
                                    }
                                }
                            })
                        }
                    }
                })
        }
        ttsHelper = TextToSpeechHelper(requireContext(), this)
        return view
    }

    fun saveFile(text: String, saveDialog: Dialog, file_name: String): String {
        saveDialog.dismiss()
        ttsHelper = TextToSpeechHelper(requireContext(), object : TextToSpeechHelper.OnInitListener {
            override fun onInitSuccess() {
                val actual_file_name = requireArguments().getString("actual_filename") as String
                val fileName = "$file_name.mp3"
                val childDirectory="EbookAudios"
                ttsHelper.getAudioByteArray(requireContext(),text,object: TextToSpeechHelper.OnAudioGeneratedListener {
                    override fun onAudioGenerated(audioData: ByteArray?) {
                        if(audioData!=null)
                        {
                            saveFiles.createDirectoryAndTextFile(requireContext(),childDirectory,fileName, audioData)
                        }else
                            Toast.makeText(requireContext(), "Audio File Empty", Toast.LENGTH_SHORT).show()

                    }
                })

                //val audioByteArray=ttsHelper.getAudioByteArray(text)
               // saveFiles.createDirectoryAndTextFile(requireContext(),childDirectory,fileName, audioByteArray!!)
            }
            override fun onInitFailed() {
                Toast.makeText(requireContext(), "Initialization Failed", Toast.LENGTH_SHORT).show()
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
        view?.importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_YES
        view?.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        view?.importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }
    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stopSpeech()
    }

}
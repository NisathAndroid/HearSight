package com.example.hearsight.Activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hearsight.DataModel.Connect_to_Device
import com.example.hearsight.DataModel.ExistingFilesOpen
import com.example.hearsight.DataModel.ExistingFilesOpen.Companion.isMusic
import com.example.hearsight.DataModel.LanguageDialog
import com.example.hearsight.DataModel.PlayShare
import com.example.hearsight.DataModel.PythonDirectoryPaths
import com.example.hearsight.DataModel.SaveFiles
import com.example.hearsight.DataModel.SharedPreferenceBase
import com.example.hearsight.DataModel.ProgressDialogCls
import com.example.hearsight.Fragments.E_Book
import com.example.hearsight.R
import com.example.txtextrct.Assets
//import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
//import com.zegocloud.uikit.service.defines.ZegoUIKitUser


class MainActivity : AppCompatActivity() {
    lateinit var bankCard:CardView
    lateinit var excelCard:CardView
    lateinit var ebookCard:CardView
    lateinit var docsCard:CardView
    lateinit var captureCard:CardView
    lateinit var gallery_picture:CardView
    lateinit var entertainment:CardView
    lateinit var connect_to_device:CardView
    private val PICK_FILE_PDF_REQUEST_CODE=1001
    private val PICK_FILE_DOCS_REQUEST_CODE=2002
    private val PICK_FILE_EXCEL_REQUEST_CODE=3003
    private val PICK_FILE_EBOOK_REQUEST_CODE=4004
    private val CAMERA_CAPTURE_REQUEST_CODE=5005
    private val GALLERY_PICTURE_REQ_CODE=6006
    private val ebook=E_Book()
    private lateinit var languageDialog: LanguageDialog
    private lateinit var progressdialogCls: ProgressDialogCls
    private lateinit var  dialog:Dialog
    private lateinit var playShare: PlayShare
    private lateinit var saveFiles:SaveFiles
    lateinit var connect_to_device_txt:TextView
    lateinit var newFileBtn:Button
    lateinit var recentFileBtn:Button
//    lateinit var videocallbtn: ZegoSendCallInvitationButton
    lateinit var existingClassIntent:Intent
    lateinit var sharedPreference:SharedPreferenceBase
    val existingClassbundle = Bundle()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val language=resources.getStringArray(R.array.languages)
        if(Assets.isFolderEmpty()==true)
        {
            for (lang in language)
            {
                Assets.download_TrainedLanguage(this,lang)
            }
        }
        playShare= PlayShare()
        dialog = Dialog(this)
        existingClassIntent=Intent(this, ExistingFilesOpen::class.java)
        dialog.setContentView(R.layout.bank_statement_dialog)
        dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        newFileBtn=dialog.findViewById(R.id.newFileButton)
        recentFileBtn=dialog.findViewById(R.id.existingFileButton)
        connect_to_device_txt=findViewById(R.id.connect_to_device_txt)
        saveFiles= SaveFiles(this)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF000000")))
        languageDialog= LanguageDialog(this)
        progressdialogCls= ProgressDialogCls(this)
        bankCard = findViewById(R.id.bank_statement_card)
        excelCard = findViewById(R.id.excel_card)
        ebookCard = findViewById(R.id.ebookcard)
        docsCard = findViewById(R.id.docs_card)
        captureCard = findViewById(R.id.captureCard)
        connect_to_device = findViewById(R.id.connect_to_device)
        gallery_picture = findViewById(R.id.gallery_picture)
        entertainment = findViewById(R.id.entertainment_card)
//        videocallbtn=findViewById(R.id.vcall)
        sharedPreference= SharedPreferenceBase(this)
//        zogoCloudIntegration()
        bankCard.setOnClickListener {
            bankStatement()
        }
        docsCard.setOnClickListener {
            wordFunction()
        }
        excelCard.setOnClickListener {
            excelFunction()
        }
        ebookCard.setOnClickListener {
            ebookFunction()
        }

        connect_to_device.setOnClickListener {
            val connect_to_Device = Connect_to_Device(this)
            val textName=connect_to_device_txt.text.toString()
            when(textName)
            {
                "Connect to Device"->{
                    connect_to_Device.ConnectDevice(connect_to_device_txt)
                }

                "Disconnect to Device"->{
                    connect_to_Device.disconnect_to_device(connect_to_device_txt)
                }
            }

        }

        entertainment.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), CAMERA_CAPTURE_REQUEST_CODE)
                }else
                {
                    isMusic=true
                    val goto="Music"
                    existingClassbundle.putString("python_path",PythonDirectoryPaths.MEDIA_PATH)
                    existingClassbundle.putString("music",goto)
                    existingClassIntent.putExtras(existingClassbundle)
                    startActivity(existingClassIntent)
                }
            }
            else
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA_CAPTURE_REQUEST_CODE)
                }else
                {
                    isMusic=true
                    val goto="Music"
                    existingClassbundle.putString("python_path",PythonDirectoryPaths.MEDIA_PATH)
                    existingClassbundle.putString("music",goto)
                    existingClassIntent.putExtras(existingClassbundle)
                    startActivity(existingClassIntent)
                }

            }
        }
        captureCard.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_CAPTURE_REQUEST_CODE)
            }
            else {
                cameraFunction()
            }
        }

        gallery_picture.setOnClickListener {
            take_pic_Gallery()
        }

    }

//    private fun zogoCloudIntegration() {
//        videocallbtn.setIsVideoCall(true)
//        videocallbtn.resourceID="zego_uikit_call_1"
//        videocallbtn.setInvitees(Collections.singletonList(ZegoUIKitUser("sunil","sunil")))
//    }


    private fun take_pic_Gallery() {
        newFileBtn.setOnClickListener {
            chooseGalleryImage()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/GalleryAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassbundle.putString("python_path",PythonDirectoryPaths.MEDIA_PATH)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }

    private fun chooseGalleryImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
        startActivityForResult(intent, GALLERY_PICTURE_REQ_CODE)
    }

    private fun cameraFunction() {
        newFileBtn.setOnClickListener {
            takingPicture()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/CameraAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassbundle.putString("python_path",PythonDirectoryPaths.MEDIA_PATH)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }

    private fun takingPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null)
        {
            startActivityForResult(takePictureIntent, CAMERA_CAPTURE_REQUEST_CODE)
        }
        else
            Toast.makeText(this, "Camera not found", Toast.LENGTH_SHORT).show()
    }

    private fun bankStatement() {
        newFileBtn.setOnClickListener {
            pdfFile()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/PdfFileAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassbundle.putString("python_path",PythonDirectoryPaths.DOCUMENTS_PATH)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }

    private fun ebookFunction() {
        newFileBtn.setOnClickListener {
            new_EBook_file()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/EbookAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }

    private fun new_EBook_file() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/epub+zip"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
            "application/epub+zip",
            "application/pdf",
            "text/plain"
        ))
        startActivityForResult(intent, PICK_FILE_EBOOK_REQUEST_CODE)
    }

    private fun excelFunction() {
        newFileBtn.setOnClickListener {
            newExcelfile()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/ExcelAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassbundle.putString("python_path",PythonDirectoryPaths.DOCUMENTS_PATH)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }

    private fun newExcelfile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.ms-excel"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ))
        startActivityForResult(intent, PICK_FILE_EXCEL_REQUEST_CODE)
    }

    private fun wordFunction() {
        newFileBtn.setOnClickListener {
            newDocsFile()
        }
        recentFileBtn.setOnClickListener {
            val path="/storage/emulated/0/Download/HearSightAudio/DocumentAudios/"
            existingClassbundle.putString("directory_path",path)
            existingClassbundle.putString("python_path",PythonDirectoryPaths.DOCUMENTS_PATH)
            existingClassIntent.putExtras(existingClassbundle)
            startActivity(existingClassIntent)
        }
        dialog.show()
    }
    private fun pdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
        startActivityForResult(intent, PICK_FILE_PDF_REQUEST_CODE)
    }

    private fun newDocsFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
            "application/docx",
            "application/msword",
            "application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        ))
        startActivityForResult(intent, PICK_FILE_DOCS_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val fileUri: Uri? = data?.data
            if (fileUri != null) {
                val actualfile_name = getFilenameFromUri(fileUri)
                val intent = Intent(this, PdfActivity::class.java)
                intent.putExtra("uriData", fileUri)
                intent.putExtra("actual_filename", actualfile_name)
                sharedPreference.saveData("uriData", fileUri.toString())
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == PICK_FILE_DOCS_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val fileUri: Uri? = data?.data
            if (fileUri != null) {
                val actualFileName = getFilenameFromUri(fileUri)
                val intent = Intent(this, WordActivity::class.java)
                intent.putExtra("docs_uriData", fileUri)
                intent.putExtra("actual_filename", actualFileName)
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == PICK_FILE_EXCEL_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val fileUri: Uri? = data?.data
            if (fileUri != null) {
                val actualFileName = getFilenameFromUri(fileUri)
                val intent = Intent(this, ExcelActivity::class.java)
                intent.putExtra("excel_uriData", fileUri)
                intent.putExtra("actual_filename", actualFileName)
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == PICK_FILE_EBOOK_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val fileUri: Uri? = data?.data
            val path=data!!.data!!.path
            if (fileUri!=null)
            {
                val actualfile_name=getFilenameFromUri(fileUri)
                val bundle = Bundle()
                bundle.putParcelable("ebook_uriData", fileUri)
                bundle.putString("actual_filename",actualfile_name)
                bundle.putString("actual_path",path)
                ebook.arguments = bundle
                dialog.dismiss()
                setFragment(ebook)
            }
            else
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT)
        }
        else if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                val uri = Assets.bitmapToFile(this, imageBitmap)
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("capture_image", imageBitmap)
                intent.putExtra("capture_image_uri", uri)
                intent.putExtra("actual_filename", imageBitmap)
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode==GALLERY_PICTURE_REQ_CODE&&resultCode==Activity.RESULT_OK)
        {
            val galleryUri: Uri? = data?.data
            if (galleryUri != null) {
                val actualFileName = getFilenameFromUri(galleryUri)
                val intent = Intent(this, GalleryActivity::class.java)
                intent.putExtra("gallery_image", galleryUri)
                intent.putExtra("actual_filename", actualFileName)
                startActivity(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Select file failed", Toast.LENGTH_SHORT).show()
            }
        }
        else
            Toast.makeText(this, "Unable to get the file at the moment", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("Range")
    private fun getFilenameFromUri(fileUri: Uri): String {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        cursor.use {
            if (it?.moveToFirst() == true) {
                val displayName = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                val fileNameWithoutExtension = removeFileExtension(displayName)
                return fileNameWithoutExtension
            }
        }
        return ""
    }

    private fun removeFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf(".")
        return if (dotIndex > 0) {
            fileName.substring(0, dotIndex)
        } else {
            fileName
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        menu!!.findItem(R.id.setting).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.download->{
                languageDialog.SetDialog()
                languageDialog.setTesseractLangAdapter()
                return true
            }

            R.id.reading_lang->
            {
                languageDialog.SetDialog()
                languageDialog.setTexttoSpeechAdapter()
                return true
            }

            R.id.voice_pitch->{
                progressdialogCls
                progressdialogCls.pitch_speedrate("pitch").show()
                return true
            }

            R.id.voice_speed_rate->{
                progressdialogCls.pitch_speedrate("speed").show()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    fun setFragment(fragment:Fragment)
    {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.mainFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object{
        var isConnectServer=false
    }
}

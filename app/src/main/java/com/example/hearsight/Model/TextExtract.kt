package com.example.hearsight.Model

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.Parser
import org.apache.tika.sax.BodyContentHandler
import org.zwobble.mammoth.DocumentConverter
import org.zwobble.mammoth.Result
import java.io.InputStream


class TextExtract(private val context: Context) {
private  val MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
private  val FILE_EXTENSION_DOCX = "docx"
private val sharedPreferenceBase=SharedPreferenceBase(context)

    fun extractTextFromPDF(uri: Uri): List<String> {
        val pageTexts = mutableListOf<String>()

        try {
            val contentResolver: ContentResolver = context.contentResolver
            val pdf = contentResolver.openInputStream(uri)
            val reader = PdfReader(pdf)
            val numPages: Int = reader.numberOfPages

            for (i in 0 until numPages) {
                val pageText = PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }
                pageTexts.add(pageText)
                // Add a separator or newline between pages
                pageTexts.add("\n\n\n----------- Page End -----------\n\n\n")
            }

            reader.close()

        } catch (e: Exception) {
            println(e)
        }

        return pageTexts
    }

     //Excel text extract
    fun getTextFromExcel(fileUri: Uri?, contentResolver: ContentResolver): String {
        val allRowsTextBuilder = StringBuilder()
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri!!)
            val workbook = WorkbookFactory.create(inputStream)
            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                for (row in sheet) {
                    val rowTextBuilder = StringBuilder()
                    for (cell in row) {
                        when (cell.cellType) {
                            CellType.STRING -> rowTextBuilder.append(cell.stringCellValue).append(" ")
                            CellType.NUMERIC -> {
                                val numericValue = cell.numericCellValue
                                val formattedValue = if (numericValue.isInt()) {
                                    numericValue.toInt().toString()
                                } else {
                                    numericValue.toString()
                                }
                                rowTextBuilder.append(formattedValue).append(" ")
                            }
                            // Add handling for other cell types as necessary
                            else -> ""
                        }
                    }
                    val rowText = rowTextBuilder.toString()
                    allRowsTextBuilder.append(rowText).append("\n") // Append each row to the allRowsTextBuilder with a newline character
                    Log.e("_TAG_ROW", rowText)
                }
            }
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val allRowsText = allRowsTextBuilder.toString().trim()
        return allRowsText
    }
    private fun Double.isInt() = this.toInt().toDouble() == this


    fun extractTextFromDocument(uri: Uri, contentResolver: ContentResolver): String {
        try {
            val mimeType = contentResolver.getType(uri)
            val inputStream = contentResolver.openInputStream(uri)
            return when {
                mimeType != null && mimeType != MIME_TYPE_DOCX -> {
                    extractTextUsingTika(inputStream,uri,contentResolver)
                }
                else ->
                {
                    val result: Result<String>? = DocumentConverter().extractRawText(inputStream)
                    result?.getValue() ?: "File not read"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Failed to extract the text"
        }
    }

    private fun extractTextUsingTika(inputStream: InputStream?, uri: Uri, contentResolver: ContentResolver): String {
        return try {
            val fileExtension = contentResolver.getType(uri)?.substringAfterLast('.')
            val mimeType = contentResolver.getType(uri)
            val parser = AutoDetectParser()
            val textContentHandler = BodyContentHandler()
            Log.e("mimeType", uri.toString())
            when (fileExtension) {
                FILE_EXTENSION_DOCX -> {
                    val xwpfWordExtractor = XWPFWordExtractor(XWPFDocument(inputStream))
                    xwpfWordExtractor.text
                }

                "doc", "dot", "dotx", "docm", "dotm", "rtf" -> {
                    val metadata = org.apache.tika.metadata.Metadata()
                    val context = ParseContext()
                    context.set(Parser::class.java, parser)
                    parser.parse(inputStream, textContentHandler, metadata, context)
                    textContentHandler.toString()
                }

                "txt" -> {
                    val textContent = inputStream!!.bufferedReader().use { it.readText() }
                    textContent
                }

                "dot" -> {
                    "Text content for .dot files"
                }

                else -> {
                    if ("doc" == mimeType || "application/msword" == mimeType) {
                        val xwpfWordExtractor = XWPFWordExtractor(XWPFDocument(inputStream))
                        xwpfWordExtractor.text
                    } else {
                        Log.e("File Format", "Unsupported file format")
                        ""
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Extraction Error", "Error extracting text from file: ${e.message}")
            "File not supported"
        } finally {
            inputStream?.close()
        }
    }



    //Google play service
    fun google_extractTextFromImage(imageBitmap: Bitmap): StringBuilder? {
        try {
            val textRecognizer = com.google.android.gms.vision.text.TextRecognizer.Builder(context).build()
            if (!textRecognizer.isOperational)
            {
                Log.e(TAG, "Text recognizer is not operational.")
            }
            val frame = Frame.Builder().setBitmap(imageBitmap).build()
            val textBlocks = textRecognizer.detect(frame)
            val extractedText = StringBuilder()
            for (i in 0 until textBlocks.size())
            {
                val textBlock = textBlocks.valueAt(i)
                extractedText.append(textBlock.value)
                extractedText.append("\n")
            }
            return extractedText
        }catch (e:Exception){
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Picture not clear", Toast.LENGTH_SHORT).show()
                (context as Activity).finish()
            }
        }
        return null
    }

    fun languageDetection(text: String)
    {
        try {
            Log.e("languageDetection",text.toString())
            val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode!="und")
                    {
                        sharedPreferenceBase.saveData("lang_code",languageCode)
                    }
                    else
                        Log.e("LangCodeTag","Could not detect the language code")
                }

                .addOnFailureListener {
                    Log.i(TAG, "Failure.")
                }
        }catch (e:Exception)
        {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}



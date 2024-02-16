package com.example.hearsight.Model

import android.app.Application
import android.widget.Toast
import com.example.hearsight.Activity.CameraActivity
import com.example.hearsight.R
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.protobuf.ByteString
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Base64


class GoogleVisionApi: Application() {

    fun decodeApiKeyFromJsonFile(jsonFile: File): String {
        val json = jsonFile.readText()

        val jsonObject = JSONObject(json)

        val apiKey = jsonObject.getString("private_key")

        val decodedApiKey = Base64.getDecoder().decode(apiKey)

        return decodedApiKey.toString()
    }

    fun google_cloud_visionApi(context: CameraActivity, filepath: String): String {
        try {
            val resourceId = R.raw.gcvkey
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(inputStream)
            val imageAnnotatorClient = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder().setCredentialsProvider { credentials }.build())
            val imageBytes = ByteString.readFrom(FileInputStream(filepath))
            val image = Image.newBuilder().setContent(imageBytes).build()
            val feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build()
            val request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build()
            val response = imageAnnotatorClient.batchAnnotateImages(listOf(request))
            val textAnnotations = response.responsesList[0].textAnnotationsList
            for (annotation in textAnnotations) {
                println("Extracted Text: ${annotation.description}")
                Toast.makeText(this, "${annotation.description.toString()}", Toast.LENGTH_SHORT).show()
            }
            return textAnnotations.toString()
            //imageAnnotatorClient.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
        //return ""
    }
}
package com.example.hearsight.DataModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.LanguageServiceClient
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ImageQualityEnhance(private val context: Context):Application() {

    override fun onCreate() {
        super.onCreate()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun enhanceImage(bitmap: Bitmap): Bitmap {
        // Perform image enhancement operations here
        val enhancedBitmap = applyEnhancements(bitmap)
        return enhancedBitmap
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyEnhancements(bitmap: Bitmap): Bitmap {
        // Convert to grayscale
        val grayscaleBitmap = toGrayscale(bitmap)

        // Sharpening
        //val sharpenedBitmap = applySharpening(grayscaleBitmap)

        // Contrast adjustment
        val contrastAdjustedBitmap = adjustContrast(grayscaleBitmap)

        // Brightness adjustment
        val brightnessAdjustedBitmap = adjustBrightness(contrastAdjustedBitmap,1.2f)

        // Binarization (thresholding)
        val binarizedBitmap = applyThresholding(brightnessAdjustedBitmap,128)

        return binarizedBitmap
    }

    private fun toGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Converts to grayscale
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayscaleBitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun adjustContrast(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val contrastAdjustedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val contrastFactor = 1.5f // Adjust this value to control the contrast level

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)

                // Extract the individual color channels (R, G, B)
                val red = Color.red(pixelColor)
                val green = Color.green(pixelColor)
                val blue = Color.blue(pixelColor)

                // Apply contrast adjustment to each color channel
                val newRed = ((red - 128) * contrastFactor + 128).coerceIn(0f, 255f)
                val newGreen = ((green - 128) * contrastFactor + 128).coerceIn(0f, 255f)
                val newBlue = ((blue - 128) * contrastFactor + 128).coerceIn(0f, 255f)

                // Set the adjusted color to the output bitmap
                contrastAdjustedBitmap.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }

        return contrastAdjustedBitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun adjustBrightness(bitmap: Bitmap, brightnessFactor: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val brightnessAdjustedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)
                val red = Color.red(pixelColor)
                val green = Color.green(pixelColor)
                val blue = Color.blue(pixelColor)
                val newRed = (red * brightnessFactor).coerceIn(0f, 255f)
                val newGreen = (green * brightnessFactor).coerceIn(0f, 255f)
                val newBlue = (blue * brightnessFactor).coerceIn(0f, 255f)

                // Set the adjusted color to the output bitmap
                brightnessAdjustedBitmap.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }

        return brightnessAdjustedBitmap
    }

    private fun applyThresholding(bitmap: Bitmap, threshold: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val thresholdedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)
                val intensity = Color.red(pixelColor) // Assuming a grayscale image

                // Compare the pixel's intensity to the threshold
                val newColor = if (intensity >= threshold) {
                    Color.WHITE // Foreground (above or equal to threshold)
                } else {
                    Color.BLACK // Background (below threshold)
                }

                // Set the new color to the output bitmap
                thresholdedBitmap.setPixel(x, y, newColor)
            }
        }

        return thresholdedBitmap
    }

//    fun detectLanguage(text: String): String? {
//        try {
//            // Create a language service client
//            val languageService = LanguageServiceClient.create()
//
//            // Create a document for the given text
//            val document = Document.newBuilder()
//                .setContent(text)
//                .setType(Document.Type.PLAIN_TEXT)
//                .build()
//            // Use the language service to detect the language
//            val response = languageService.classifyText(document)
//
//            // Get the language code from the response
//            val languageCode = response.language
//
//            // Close the language service client
//            languageService.close()
//
//            return languageCode
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return null
//    }

}
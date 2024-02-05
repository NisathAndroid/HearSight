import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.hearsight.DataModel.ProgressDialogCls
import com.example.hearsight.DataModel.SharedPreferenceBase
import java.io.File
import java.util.Locale

class TextToSpeechHelper(private val context: Context, private val onInitListener: OnInitListener) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var paused: Boolean = false
    private var pausedPosition: Int = 0
    var pitch=0f
    var speechSpeedRate=0f
    var tts_speak_lang=""
    private val sharePrf=SharedPreferenceBase(context)
    init {
        tts = TextToSpeech(context, this)
    }

    interface OnAudioGeneratedListener {
        fun onAudioGenerated(audioData: ByteArray?)
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                if (tts == null) {
                    tts = TextToSpeech(context, this)
                }
                val pitchString = sharePrf.getString("set_pitch", "") ?: ""
                val speechLangString = sharePrf.getString("TTS_lang_code", "") ?: ""
                val speechSpeedRateString = sharePrf.getString("setSpeechRate", "") ?: ""
                pitch = pitchString.toFloatOrNull() ?: 1f
                tts_speak_lang = if (speechLangString.isNotEmpty()) speechLangString else "en"
                speechSpeedRate = speechSpeedRateString.toFloatOrNull() ?: 0.9f
                tts?.setPitch(pitch)
                tts?.setSpeechRate(speechSpeedRate)
                val result = tts?.setLanguage(Locale(tts_speak_lang, "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    onInitListener?.onInitFailed()
                } else {
                    onInitListener?.onInitSuccess()
                }
            } else {
                onInitListener?.onInitFailed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TTS_INIT", "Exception during initialization: ${e.message}")
            onInitListener?.onInitFailed()
        }
    }

    fun speak(text: String) {
        if (paused) {
            tts!!.speak(text.substring(pausedPosition), TextToSpeech.QUEUE_ADD, null, null)
            paused = false
        } else {
            tts!!.playSilentUtterance(0, TextToSpeech.QUEUE_ADD, null)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun readTableWithTTS(tableText: String) {
        val rows = tableText.trim().split("\n")
        for (row in rows) {
            val columns = row.split("\t")
            for (column in columns) {
                tts!!.speak(column, TextToSpeech.QUEUE_ADD, null, null)
            }
            tts!!.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null) // Pause between rows
        }
    }

    fun pauseSpeech() {
        if (tts?.isSpeaking == true) {
            paused = true
            pausedPosition = tts!!.stop()
        }
    }

    fun stopSpeech() {
        tts?.stop()
        tts?.shutdown()
    }

    fun getAudioByteArray(context: Context, text: String, onAudioGeneratedListener: OnAudioGeneratedListener) {
        try {
            (context as Activity).runOnUiThread {
                TextToSpeech(context, TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val utteranceId = "saveAudioId"
                        val params = HashMap<String, String>()
                        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
                        val tempFile = File(context.cacheDir, "temp_audio_file.mp3")
                        val simpleDialogbox = ProgressDialogCls(context)
                        val dialog = simpleDialogbox.mDialog()
                        dialog.show()
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {}

                            override fun onDone(utteranceId: String?) {
                                dialog.dismiss()
                                val audioData = tempFile.readBytes()
                                onAudioGeneratedListener.onAudioGenerated(audioData)
                                tempFile.delete()
                                tts?.stop()
                                tts?.shutdown()
                            }

                            override fun onError(utteranceId: String?) {
                                onAudioGeneratedListener.onAudioGenerated(null)
                                tempFile.delete()
                                tts?.stop()
                                tts?.shutdown()
                            }
                        })

                        // Adjust chunkSize based on your requirements
                        val chunkSize = 4000
                        val chunks = text.chunked(chunkSize)

                        // Adjust delayBetweenChunks based on your requirements
                        val delayBetweenChunks = 1000

                        for (chunk in chunks) {
                            tts?.synthesizeToFile(chunk, params, tempFile.toString())
                            Thread.sleep(delayBetweenChunks.toLong())
                        }
                    } else {
                        onAudioGeneratedListener.onAudioGenerated(null)
                    }
                })
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }


    private fun modifyTextForContinuousReading(originalText: String): String? {
        return originalText.replace("\n".toRegex(), ", ")
    }

    fun ByteArray.toByteBuffer() = java.nio.ByteBuffer.wrap(this)
    interface OnInitListener {
        fun onInitSuccess()
        fun onInitFailed()
    }
}

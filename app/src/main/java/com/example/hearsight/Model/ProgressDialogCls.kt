package com.example.hearsight.Model

import android.app.Dialog
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.hearsight.R
import java.util.Locale


class ProgressDialogCls(private val context: Context) {
    private var progressDialog: Dialog? = null
    private var textToSpeech: TextToSpeech? = null
    val sharedPref=SharedPreferenceBase(context)
    fun mDialog(): Dialog {
        progressDialog = Dialog(context)
        progressDialog!!.setContentView(R.layout.please_wait_dialog)
        var set_value= progressDialog!!.findViewById<Button>(R.id.set_value)
        var seekBar= progressDialog!!.findViewById<SeekBar>(R.id.seekBar)
        set_value.visibility=View.GONE
        seekBar.visibility=View.GONE
        progressDialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        progressDialog!!.setCanceledOnTouchOutside(false)
        return progressDialog!!
    }

    fun pitch_speedrate(spliter: String): Dialog {
        progressDialog = Dialog(context)
        progressDialog!!.setContentView(R.layout.please_wait_dialog)
        var progressBar= progressDialog!!.findViewById<ProgressBar>(R.id.progressBar)
        var progressHead= progressDialog!!.findViewById<TextView>(R.id.progressHead)
        var processing= progressDialog!!.findViewById<TextView>(R.id.processing)
        var set_value= progressDialog!!.findViewById<Button>(R.id.set_value)
        var seekBar= progressDialog!!.findViewById<SeekBar>(R.id.seekBar)
        progressHead.text="Set voice pitch"
        progressBar.visibility=View.GONE
        progressDialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        progressDialog!!.setCanceledOnTouchOutside(false)
        set_value.setOnClickListener { progressDialog!!.dismiss() }
        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                    Log.d("ProgressDialogCls", "TextToSpeech initialized successfully")
                }
            } else {
                Log.e("ProgressDialogCls", "TextToSpeech initialization failed with status: $status")
            }
        })


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textToSpeech?.let {
                    processing.text = "Progress: $progress"
                    val progress_value = progress.toFloat() / 50.0f
                    when(spliter)
                    {
                        "pitch"-> setPitch(it,progress_value,set_value)
                        "speed"-> setSpeed(it,progress_value,set_value)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        return progressDialog!!
    }

    private fun setPitch(it: TextToSpeech, progress_value: Float, set_value: Button) {

        it.setPitch(progress_value)
        it.speak("Welcome to hearsight", TextToSpeech.QUEUE_FLUSH, null, null)
        set_value.setOnClickListener {
            sharedPref.saveData("set_pitch",progress_value.toString())
            Toast.makeText(context, "voice pitch set successfully", Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
        }

    }

    private fun setSpeed(it: TextToSpeech, progress_value: Float, set_value: Button) {
        it.setSpeechRate(progress_value)
        it.speak("Welcome to hearsight", TextToSpeech.QUEUE_FLUSH, null, null)
        set_value.setOnClickListener {
            sharedPref.saveData("setSpeechRate",progress_value.toString())
            Toast.makeText(context, "Speech rate set successfully", Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
        }
    }

}

package com.example.hearsight.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hearsight.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetview(private val context: Context) {
    private lateinit var save_btn: LinearLayout
    private lateinit var cancel_btn: LinearLayout
    private lateinit var play_btn: LinearLayout
    private lateinit var stop_btn: LinearLayout
    private lateinit var play_pause: TextView
    private val bottomDialog: BottomSheetDialog = createDialog()

    fun showBottomDialog() {
        (context as Activity).runOnUiThread {
            bottomDialog.show()
        }
    }

    fun cancelBottomDialog() {
        bottomDialog.dismiss()
    }

    fun destroy() {
        bottomDialog.dismiss()
    }

    fun getSaveBtn(): LinearLayout {
        return save_btn
    }

    fun getCancelBtn(): LinearLayout {
        return cancel_btn
    }

    fun getPlayBtn(): Pair<LinearLayout, TextView> {
        return Pair(play_btn,play_pause)
    }

    fun getStopBtn(): LinearLayout {
        return stop_btn
    }

    @SuppressLint("MissingInflatedId")
    private fun createDialog(): BottomSheetDialog {
        val bottomDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottomsheetview, null)
        save_btn = view.findViewById(R.id.save_container_id)
        cancel_btn = view.findViewById(R.id.cancel_container_id)
        play_btn = view.findViewById(R.id.play_container_id)
        stop_btn = view.findViewById(R.id.stop_container_id)
        play_pause = view.findViewById(R.id.play_pause)
        bottomDialog.setContentView(view)
        bottomDialog.setCancelable(false)
        return bottomDialog
    }
}

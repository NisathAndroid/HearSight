package com.example.hearsight.DataModel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.hearsight.R

class SaveFileNameDialog(private val context: Context) {
    private lateinit var saveDilog:Dialog
    @SuppressLint("SuspiciousIndentation")
    fun initializeDialog(getsaveDialogDts:GetSaveDialogDts)
    {
        saveDilog= Dialog(context)
        saveDilog.setContentView(R.layout.save_dialog)
        saveDilog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        val text_input_edit_text=saveDilog.findViewById<EditText>(R.id.editTextName)
        val save_btn=saveDilog.findViewById<CardView>(R.id.dialog_save_btn_id)
        val error_msg=saveDilog.findViewById<TextView>(R.id.error_msg)
        getsaveDialogDts.getsavedialog(saveDilog,text_input_edit_text,error_msg,save_btn)
        saveDilog.show()
    }

    interface GetSaveDialogDts{
        fun getsavedialog(
            saveDilog: Dialog,
            get_name: EditText,
            error_msg: TextView,
            save_btn: CardView
        )
    }
}
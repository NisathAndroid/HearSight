package com.example.hearsight.DataModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hearsight.R

class LanguageAdapter(private val context:Context,private val language_chooser_dataclass:ArrayList<LanguageChooserDataClass>,val onclicklistener:OnClickListener):RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    interface OnClickListener
    {
        fun onitemclicklistener(tts_lang_code: String, tes_lang_code: String)
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val language_name=itemView.findViewById<TextView>(R.id.language_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val v=LayoutInflater.from(context).inflate(R.layout.language_list,parent,false)
       return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return language_chooser_dataclass.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.language_name.text=language_chooser_dataclass[position].language_name
        holder.itemView.setOnClickListener {
            val tts_lang_code=language_chooser_dataclass[position].tts_language_code
            val tes_lang_code=language_chooser_dataclass[position].tes_language_code
            onclicklistener.onitemclicklistener(tts_lang_code,tes_lang_code)
        }
    }
}


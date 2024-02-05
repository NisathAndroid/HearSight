package com.example.hearsight.DataModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hearsight.Activity.MainActivity
import com.example.hearsight.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ExistingFilesAdapter(private val context: Activity, private val existingData: ArrayList<ExistingfilesDataClass>, val pythonPath: String, ) : RecyclerView.Adapter<ExistingFilesAdapter.ViewHolder>() {
    private val file_share_proto=FileShareCheck(context)
    private val intent = Intent(context, PlayShare::class.java).apply {
        putExtras(Bundle())
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.file_name_id)
        val play:Button = itemView.findViewById(R.id.play)
        val sharebtn: Button = itemView.findViewById(R.id.share)
        val deletebtn: Button = itemView.findViewById(R.id.delete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_existine_files, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return existingData.size
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        if (existingData.isNotEmpty()) {
            val path = existingData[position].fileUri
            val tempPathlist: ArrayList<String> = ArrayList(existingData.map { it.fileUri })
            holder.deletebtn.setOnClickListener {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    existingData.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,existingData.size)
                    Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show()
                }else
                {
                    Toast.makeText(context, "file not exit", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.setOnClickListener {
                startPlayShareActivity(path, tempPathlist, position)
            }
            holder.filename.text = existingData[position].filename
            val audioPath=File(path)
            holder.sharebtn.setOnClickListener {
//                val fileshareFactors=FileShareFactors()
//                fileshareFactors.shareAudioFile(context,audioPath)
                if (MainActivity.isConnectServer)
                {
                   shareFile(path)
                }
                else
                {
                    Toast.makeText(context, "Please connect device", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    private fun startPlayShareActivity(path: String, tempPathlist: ArrayList<String>, position: Int) {
        intent.apply {
            putExtra("selectedTitle", path)
            putStringArrayListExtra("path_list", tempPathlist)
            putExtra("audio_position", position)
        }
        context.startActivity(intent)
    }

    private fun shareFile(path: String) {
        val isDeveloperMode_Enabled=file_share_proto.isDeveloperModeEnable()
        val isUsbDebuggingEnabled=file_share_proto.isUsbDebuggingEnabled()
        val isHotspotEnable=file_share_proto.isWifiApEnabled()
        val isUsbEnable=file_share_proto.isConnectedToUSB()
        try {
            if (isDeveloperMode_Enabled)
            {
                if (isUsbDebuggingEnabled)
                {
                    if (isHotspotEnable)
                    {
                        if (isUsbEnable)
                        {
                            val isIpEnable=file_share_proto.read_ip_External(Environment.getExternalStorageDirectory())
                            if (isIpEnable.isNotEmpty())
                            {
                                val message="adb pull $path $pythonPath"
                                val fileshareIpConfig = FileShareIpConfig(context)
                                GlobalScope.launch(Dispatchers.Main) {
                                    val isSuccess=fileshareIpConfig.send_message_to_terminal(message,isIpEnable)
                                    if (isSuccess)
                                    {
                                        Toast.makeText(context, "File send successfully", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, "Send file failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }else{
                                Toast.makeText(context, "Ip address missing", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(context, "Please plug USB cable", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(context, "Please turn on your Hotspot", Toast.LENGTH_SHORT).show()
                        file_share_proto.enableHotspot("Meow","hearsight")
                    }
                }else
                {
                    Toast.makeText(context, "Please turn on your Usb Debugging mode here.", Toast.LENGTH_SHORT).show()
                    file_share_proto.setUsbDebuggingEnabled()
                }
            }else{
                Toast.makeText(context, "Please enable the developer option", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                context.startActivity(intent)
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

}


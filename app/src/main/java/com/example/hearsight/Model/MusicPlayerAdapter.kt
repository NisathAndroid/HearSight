package com.example.hearsight.Model

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hearsight.Activity.MainActivity
import com.example.hearsight.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicPlayerAdapter(
    private val context: Context,
    private val musilist: ArrayList<MusicItem>,
    private val pyPath: String
):RecyclerView.Adapter<MusicPlayerAdapter.ViewHolder>() {
    private val file_share_proto=FileShareCheck(context)
    val intent= Intent(context,PlayShare::class.java)
    val bundle= Bundle()
    val tempPathlist:ArrayList<String> = ArrayList()


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val musicName=itemView.findViewById<TextView>(R.id.file_name_id)
        val playbtn=itemView.findViewById<Button>(R.id.play)
        val share=itemView.findViewById<Button>(R.id.share)
        val audiofileimage=itemView.findViewById<ImageView>(R.id.art)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.fragment_existine_files,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musilist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.musicName.text=musilist[position].title
        val image=getMusicart(musilist[position].data)

        if (image!=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.audiofileimage)
        }
        holder.playbtn.setOnClickListener {
            for (i in 0..musilist.size-1)
            {
                tempPathlist.add(musilist[i].data)
            }
            bundle.putString("selectedPath",musilist[position].data)
            bundle.putString("selectedTitle",musilist[position].title)
            bundle.putStringArrayList("songs_path_arraylist",tempPathlist)
            bundle.putInt("audio_position",position)
            intent.putExtras(bundle)
            context.startActivity(intent)
            intent.replaceExtras(Bundle())
        }

        holder.share.setOnClickListener {
            if (MainActivity.isConnectServer)
            {
                val uri=musilist[position].data.toString()
                if (uri!=null)
                {
                    shareFile(uri)
                }
            }else{
                Toast.makeText(context, "Please connect the device", Toast.LENGTH_SHORT).show()
            }
        }
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
                                val message="adb pull $path $pyPath"
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

    private fun getMusicart(uri: String): ByteArray? {
        val mediametadataretriver=MediaMetadataRetriever()
        mediametadataretriver.setDataSource(uri)
        val art=mediametadataretriver.embeddedPicture
        mediametadataretriver.release()
        return art
    }
}
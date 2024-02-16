package com.example.hearsight.Model

import TextToSpeechHelper
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import com.example.hearsight.Activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Connect_to_Device(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    private val PASSWORD_CHANGED_KEY = "password_changed"
    private val file_share_proto=FileShareCheck(context)
    private val isDeveloperMode_Enabled=file_share_proto.isDeveloperModeEnable()
    private val isUsbDebuggingEnabled=file_share_proto.isUsbDebuggingEnabled()
    private val isHotspotEnable=file_share_proto.isWifiApEnabled()
    private val isUsbEnable=file_share_proto.isConnectedToUSB()
    lateinit var  ttsHelper :TextToSpeechHelper

    fun ConnectDevice(connect_to_device_txt: TextView)
    {
        if (!hasPasswordChanged()) {
            changeHotspotPassword()
        }
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
                                val fileshareIpConfig = FileShareIpConfig(context)
                                GlobalScope.launch(Dispatchers.Main) {
                                    val isSuccess=fileshareIpConfig.send_message_to_terminal("connecttodevice",isIpEnable)
                                    if (isSuccess)
                                    {
                                        Thread.sleep(1000)
                                        ttsHelper=TextToSpeechHelper(context,object :TextToSpeechHelper.OnInitListener{
                                            override fun onInitSuccess() {
                                                MainActivity.isConnectServer=true
                                                connect_to_device_txt.text = "Disconnect to Device"
                                                ttsHelper.speak("ready to share")
                                            }
                                            override fun onInitFailed() {
                                            }
                                        })
                                    }
                                    else{
                                        Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show()
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

    fun disconnect_to_device(connect_to_device_txt: TextView)
    {
        try {
            if (!hasPasswordChanged()) {
                changeHotspotPassword()
            }
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
                                    val fileshareIpConfig = FileShareIpConfig(context)
                                    GlobalScope.launch(Dispatchers.Main) {
                                        val isSuccess=fileshareIpConfig.send_message_to_terminal("disconnecttodevice",isIpEnable)
                                        if (isSuccess)
                                        {
                                            Thread.sleep(1000)
                                            ttsHelper=TextToSpeechHelper(context,object :TextToSpeechHelper.OnInitListener{
                                                override fun onInitSuccess() {
                                                    MainActivity.isConnectServer=false
                                                    connect_to_device_txt.text = "Connect to Device"
                                                    ttsHelper.speak("Disconnect")
                                                }
                                                override fun onInitFailed() {
                                                }
                                            })
                                        }
                                        else{
                                            Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show()
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
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun changeHotspotPassword() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Notice")
        alertDialogBuilder.setMessage("Change your hotspot password for Hearsight device. Click OK to copy the password, then paste it into your hotspot settings. Click Cancel if not now.")
        alertDialogBuilder.setPositiveButton("Ok") { dialog, which ->
            val newPassword = "hearsight"
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Hearsight Password", newPassword)
            clipboardManager.setPrimaryClip(clipData)
            val passwordChangedSuccessfully = changePasswordLogic()
            if (passwordChangedSuccessfully) {
                setPasswordChanged()
                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Password change failed", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        alertDialogBuilder.show()
    }

    private fun hasPasswordChanged(): Boolean {
        return sharedPreferences.getBoolean(PASSWORD_CHANGED_KEY, false)
    }

    private fun setPasswordChanged() {
        sharedPreferences.edit().putBoolean(PASSWORD_CHANGED_KEY, true).apply()
    }

    private fun changePasswordLogic(): Boolean {
        try {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val cn = ComponentName(
                "com.android.settings",
                "com.android.settings.TetherSettings"
            )
            intent.component = cn
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
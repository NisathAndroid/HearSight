package com.example.hearsight.DataModel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Date

class FileShareCheck(private val context:Context) {

    fun isDeveloperModeEnable(): Boolean {
        return try {
            Settings.Secure.getInt(context.contentResolver,Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,0)==1
        }catch (e:Settings.SettingNotFoundException)
        {
            false
        }
    }

    fun isUsbDebuggingEnabled(): Boolean {
        return try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    fun setUsbDebuggingEnabled() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            context.startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    fun isWifiApEnabled(): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        return if (wifiManager != null) {
            try {
                val method = wifiManager.javaClass.getMethod("isWifiApEnabled")
                method.invoke(wifiManager) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    fun enableHotspot(ssid: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = false
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = ssid
            wifiConfig.preSharedKey = password
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                    override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                        super.onStarted(reservation)
                        Toast.makeText(context, "Hotspot turned on", Toast.LENGTH_SHORT).show()
                    }

                    override fun onStopped() {
                        super.onStopped()
                    }

                    override fun onFailed(reason: Int) {
                        super.onFailed(reason)
                    }
                }, null)
            } else {

            }
        }
    }


    fun isConnectedToUSB(): Boolean {
        val intent = context.registerReceiver(null, IntentFilter("android.hardware.usb.action.USB_STATE"))
        return intent?.extras?.getBoolean("connected") ?: false
    }

//    fun read_ip_External(externalStorageDirectory: File): String {
//        val namelist = ArrayList<String>()
//        try {
//            if (externalStorageDirectory.isDirectory) {
//                val fileList = externalStorageDirectory.listFiles()
//                for (i in fileList.indices) {
//                    val fileName = fileList[i].name
//                    if (fileList[i].isDirectory && fileName.startsWith("ip"))
//                    {
//                        val _date=Date(fileList[i].lastModified())
//                        Log.e("__date", _date.toString())
//                        val modifiedName = fileName.replace("ip", "").replace("_", ".")
//                        namelist.add(modifiedName)
//                        val folderToDelete = File(externalStorageDirectory, fileName)
//                        return modifiedName
//                    }
//                    Log.e("filesName", fileName)
//                }
//            } else {
//                Log.e("Error", "Not a directory")
//            }
//        } catch (io: IOException) {
//            io.printStackTrace()
//        }
//        return ""
//    }

    fun read_ip_External(externalStorageDirectory: File): String {
        var latestUpdatedFolder: File? = null

        try {
            if (externalStorageDirectory.isDirectory) {
                val fileList = externalStorageDirectory.listFiles()
                for (i in fileList.indices) {
                    val fileName = fileList[i].name
                    if (fileList[i].isDirectory && fileName.startsWith("ip")) {
                        if (latestUpdatedFolder == null || fileList[i].lastModified() > latestUpdatedFolder.lastModified()) {
                            latestUpdatedFolder = fileList[i]
                        }
                    }
                    Log.e("filesName", fileName)
                }

                latestUpdatedFolder?.let {
                    val modifiedName = it.name.replace("ip", "").replace("_", ".")
                    val formattedDate = Date(it.lastModified()).toString()
                    Log.e("__date", "Folder Name: $modifiedName, Last Modified Date: $formattedDate")
                    return modifiedName
                }
            } else {
                Log.e("Error", "Not a directory")
            }
        } catch (io: IOException) {
            io.printStackTrace()
        }

        return ""
    }
}
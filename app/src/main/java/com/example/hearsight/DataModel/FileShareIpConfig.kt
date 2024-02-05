package com.example.hearsight.DataModel

import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.Socket


class FileShareIpConfig(private val context:Context) {
    //ADB
    suspend fun send_message_to_terminal(message: String, ip: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val socket = Socket(ip, 12341)
                val outputStream = socket.getOutputStream()
                val writer = PrintWriter(outputStream, true)
                writer.println(message)
                println("Sent data: $message")
                socket.close()
                true // Message sent successfully
            } catch (e: Exception) {
                e.printStackTrace()
                false // Failed to send message
            }
        }
    }

    private fun isWifiApEnabled(): Boolean {

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
}
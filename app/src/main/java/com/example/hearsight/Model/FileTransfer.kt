package com.example.hearsight.Model

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FileTransfer:Application() {
     val usbReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action=intent!!.action
            if(action!=null)
            {

            }
        }
    }
}
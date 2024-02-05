package com.example.hearsight.DataModel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.Toast

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
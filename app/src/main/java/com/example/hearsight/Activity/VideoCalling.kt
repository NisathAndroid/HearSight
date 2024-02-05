//package com.example.hearsight.Activity
//
//import android.annotation.SuppressLint
//import android.app.Application
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
//import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
//import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
//import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
//
//
//class VideoCalling : AppCompatActivity() {
//lateinit var getUserID:EditText
//lateinit var startBtn:Button
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(com.example.hearsight.R.layout.activity_vidio_calling)
//        getUserID=findViewById(com.example.hearsight.R.id.user_id)
//        startBtn=findViewById(com.example.hearsight.R.id.start)
//        startBtn.setOnClickListener {
//            val id=getUserID.text.toString()
//            if (!(id.equals("")))
//            {
//                startMyService(id)
//                val intent=Intent(this,CallActivitiy::class.java)
//                intent.putExtra("user_id",id)
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun startMyService(userId:String)
//    {
//        val application: Application =getApplication()
//        val appId:Long=1066498199
//        val appSign=getString(com.example.hearsight.R.string.App_Sign)
//        val userName=userId
//        val userID=userId
//        val invitationConfig= ZegoUIKitPrebuiltCallInvitationConfig()
//        invitationConfig.notifyWhenAppRunningInBackgroundOrQuit=true
//        val notificationConfig= ZegoNotificationConfig()
//        notificationConfig.sound="zego_uikit_sound_call"
//        notificationConfig.channelID="HearSightCallId"
//        notificationConfig.channelName="HearSightChannel"
//        ZegoUIKitPrebuiltCallInvitationService.init(application,appId,appSign,userID,userName,invitationConfig)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        ZegoUIKitPrebuiltCallInvitationService.unInit()
//    }
//}

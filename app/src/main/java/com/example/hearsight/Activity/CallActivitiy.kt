//package com.example.hearsight.Activity
//
//import android.annotation.SuppressLint
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.EditText
//import com.example.hearsight.R
//import java.util.Collections
//
//class CallActivitiy : AppCompatActivity() {
//    lateinit var userName:EditText
//    //lateinit var header_txt:TextView
//    lateinit var videocallbtn:ZegoSendCallInvitationButton
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_call_activtiy)
//        userName=findViewById(R.id.targetid)
//        videocallbtn=findViewById(R.id.vcall)
//        //header_txt=findViewById(R.id.header_txt)
//        val userId=intent.getStringExtra("user_id")
//        //header_txt.text="Hi\t\t$userId\t...et make a call"
//        userName.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                val targetUserId=userName.text.toString().trim()
//                videoCallService(targetUserId)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//        })
//    }
//
//    fun videoCallService(targetuserId: String) {
//        videocallbtn.setIsVideoCall(true)
//        videocallbtn.resourceID="zego_uikit_call"
//        videocallbtn.setInvitees(Collections.singletonList(ZegoUIKitUser(targetuserId,targetuserId)))
//    }
//}
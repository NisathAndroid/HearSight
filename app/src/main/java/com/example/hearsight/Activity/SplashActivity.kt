package com.example.hearsight.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.example.hearsight.R
//import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
//import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
//import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService

class SplashActivity : AppCompatActivity() {
    private val animationDuration = 2000L
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        val splash_logo=findViewById<ImageView>(R.id.splash_logo)
        val welcomeText=findViewById<TextView>(R.id.textview)
        val audio_vision=findViewById<TextView>(R.id.audio_vision)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

//        VideoCallRegistration("shakthi")
        val logoAnimator = ObjectAnimator.ofFloat(splash_logo, "translationX", -200f, 0f)
        logoAnimator.duration = animationDuration
        logoAnimator.start()
        val welcomeAnimator = ObjectAnimator.ofFloat(welcomeText, "translationX", 200f, 0f)
        welcomeAnimator.duration = animationDuration
        welcomeAnimator.start()
        val alphaAnimator = ObjectAnimator.ofFloat(audio_vision, View.ALPHA, 0f, 1f)
        alphaAnimator.duration = animationDuration
        alphaAnimator.interpolator = AccelerateDecelerateInterpolator()
        alphaAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                audio_vision.visibility = if (audio_vision.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
                alphaAnimator.start()
            }
        })
        alphaAnimator.start()

        logoAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                finish()
            }
        })
    }

//    private fun VideoCallRegistration(userId: String) {
//        val application: Application = getApplication()
//        val appId: Long = getString(R.string.new_zego_sign_app_id_no).toLongOrNull() ?: 0L
//        val appSign: String = getString(R.string.new_zego_signin_code)
//        val userName: String = userId
//        val userID: String = userId
//        val invitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
//        invitationConfig.notificationConfig
//        //invitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true
//        val notificationConfig = ZegoNotificationConfig()
//        notificationConfig.sound = "zego_uikit_sound_call"
//        notificationConfig.channelID = "hearsightchannelID"
//        notificationConfig.channelName = getString(R.string.VCchannel_name)
//        ZegoUIKitPrebuiltCallInvitationService.init(application, appId, appSign, userID, userName, invitationConfig)
//    }
}
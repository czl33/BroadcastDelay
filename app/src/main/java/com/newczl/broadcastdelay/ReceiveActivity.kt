package com.newczl.broadcastdelay

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.util.TimeUtils
import java.util.TimeZone
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ReceiveActivity : AppCompatActivity() {
    companion object{
        const val SEND_BROADCAST = 0x1001;
        const val TAG = "ReceiveActivity";

    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForegroundService(Intent(this,ReceiveService::class.java))
        finish()
//        val random = java.util.Random().nextInt(Status.max+1)
//        Log.i(TAG,"random :$random")
//        if(Status.data.isNotEmpty()){
//            handler.sendEmptyMessageDelayed(SEND_BROADCAST,random * getTimeType(Status.type));
//        }else{
//            finish()
//        }
    }

}
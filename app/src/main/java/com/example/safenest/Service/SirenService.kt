package com.example.safenest.Service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.safenest.R
import com.example.safenest.Receiver.PowerButtonReceiver

class SirenService : Service() {

    var screenOnOffReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.priority = 100

        screenOnOffReceiver = PowerButtonReceiver()
        registerReceiver(screenOnOffReceiver, intentFilter)

        Log.d(PowerButtonReceiver.SCREEN_TOGGLE_TAG, "Service onCreate: screenOnOffReceiver is registered.")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (screenOnOffReceiver != null) {
            unregisterReceiver(screenOnOffReceiver)
            Log.d(PowerButtonReceiver.SCREEN_TOGGLE_TAG, "Service onDestroy: screenOnOffReceiver is unregistered.")
        }
    }
}

package com.example.safenest.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.safenest.Service.SirenService

class PowerButtonReceiver : BroadcastReceiver() {
    private var pressCount = 0
    private var lastPressTime: Long = 0
    private val requiredPressCount = 3
    private val interval: Long = 2000 // Maximum time interval between presses

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF || intent?.action == Intent.ACTION_SCREEN_ON) {
            val currentTime = System.currentTimeMillis()

            // Check if time between presses is within the allowed interval
            if (currentTime - lastPressTime <= interval) {
                pressCount++
                Log.d("Phonenumber","$pressCount")
            } else {
                pressCount = 1 // Reset if time interval exceeds
            }

            lastPressTime = currentTime

            if (pressCount >= requiredPressCount) {
                // Trigger the siren
                triggerSiren(context)
                pressCount = 0 // Reset after triggering
            }
        }
    }

    private fun triggerSiren(context: Context?) {
        val intent = Intent(context, SirenService::class.java)
        context?.startService(intent) // Start a service to play the siren
    }
}


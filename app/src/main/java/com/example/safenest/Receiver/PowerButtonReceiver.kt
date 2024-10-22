package com.example.safenest.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import com.example.safenest.R
import com.example.safenest.Service.SirenService


class PowerButtonReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null
    private var powerBtnTapCount = 0
    companion object {
        const val SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        // Handling power button tap count
        if ((Intent.ACTION_SCREEN_OFF == action || Intent.ACTION_SCREEN_ON == action) && powerBtnTapCount == 0) {
            powerBtnTapCount++
        } else if (powerBtnTapCount > 0) {
            if (Intent.ACTION_SCREEN_OFF == action || Intent.ACTION_SCREEN_ON == action) {
                powerBtnTapCount++
                Log.d("mystringcheck","power button taps : $powerBtnTapCount")
            }
        }

        // Initialize MediaPlayer for the siren sound
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.siren)
        }

        // Start playing loud siren when power button is tapped 6 times
        if (powerBtnTapCount == 2) {
            mediaPlayer?.start()
            mediaPlayer?.isLooping = true
            powerBtnTapCount = 0 // Reset the tap count
        }
    }
}





package com.example.safenest.Receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.safenest.Service.GeofencingService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

class GeofencingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Toast.makeText(context,"Geofence Triggered",Toast.LENGTH_SHORT).show()
        Log.d("mystringcheck","geofence triggered")

        val serviceIntent = Intent(context, GeofencingService::class.java)
        serviceIntent.putExtras(intent)  // Pass along the received intent
        context.startService(serviceIntent)
    }



}
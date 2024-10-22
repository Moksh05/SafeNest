package com.example.safenest.Receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.safenest.MainActivity
import com.example.safenest.NotificationHelper
import com.example.safenest.Service.GeofencingService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingEvent.fromIntent

class GeofencingReceiver : BroadcastReceiver() {

    private lateinit var notificationHelper: NotificationHelper
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_SHORT).show()
        Log.d("mystringcheck", "Geofence Triggered - Toast shown inside receiver")

        notificationHelper = NotificationHelper(context)
        val geofencingEvent = fromIntent(intent ?: return)
        if (geofencingEvent!!.hasError()) {
            Log.e("mystringcheck", "Error code: ${geofencingEvent.errorCode} inside receiver")
            return
        }

        // Get the transition type
        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d("mystringcheck", "Geofence Transition Detected: $geofenceTransition inside receiver")

        // Check if the transition is of interest (enter or exit)
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the triggering geofences
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            if (triggeringGeofences != null) {
                for (geofence in triggeringGeofences) {
                    Log.d("mystringcheck", "Geofence triggered: ${geofence.requestId} inside receiver")
                    // Handle the geofence transition here (e.g., notify the user, start an action, etc.)
                    handleGeofenceAction(geofence, geofenceTransition)
                }
            }
        } else {
            Log.e("mystringcheck", "Geofence transition type not recognized: $geofenceTransition inside receiver")
        }

        val serviceIntent = Intent(context, GeofencingService::class.java)
        serviceIntent.putExtras(intent)  // Pass along the received intent
        context.startService(serviceIntent)
        Log.d("mystringcheck", "Started GeofencingService inside receiver")
    }

    private fun handleGeofenceAction(geofence: Geofence, transitionType: Int) {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationHelper.sendHighPriorityNotification(
                    "Geofence Transition Enter",
                    "You have entered a hotspot area, please be on guard",
                    MainActivity::class.java
                )
                // Code to execute when entering the geofence
                Log.d("mystringcheck", "Entered geofence: ${geofence.requestId} - Notification sent for entering inside receiver")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationHelper.sendHighPriorityNotification(
                    "Geofence Transition Exit",
                    "You have exited the hotspot area, you can calm yourself down",
                    MainActivity::class.java
                )
                // Code to execute when exiting the geofence
                Log.d("mystringcheck", "Exited geofence: ${geofence.requestId} - Notification sent for exiting inside receiver")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                notificationHelper.sendHighPriorityNotification(
                    "Geofence Dwell Transition",
                    "You are currently moving inside a hotspot area. Please keep the app open in the background and stay aware.",
                    MainActivity::class.java
                )
                // Code to execute when dwelling inside the geofence
                Log.d("mystringcheck", "Dwelling in geofence: ${geofence.requestId} - Notification sent for dwelling inside receiver")
            }
            else -> {
                Log.e("mystringcheck", "Unknown geofence transition type: $transitionType inside receiver")
            }
        }
    }
}

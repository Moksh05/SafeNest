package com.example.safenest.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.safenest.MainActivity
import com.example.safenest.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent.fromIntent

class GeofencingService : Service() {

    private val TAG = "mystringcheck"
    private lateinit var notificationHelper : NotificationHelper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        handleGeofenceTransition(intent)
        Log.d(TAG,"Inside serivice running")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun handleGeofenceTransition(intent: Intent?) {

        notificationHelper = NotificationHelper(this)
        val geofencingEvent = fromIntent(intent ?: return)
        if (geofencingEvent!!.hasError()) {
            Log.e(TAG, "Error code: ${geofencingEvent.errorCode}")
            return
        }

        // Get the transition type
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Check if the transition is of interest (enter or exit)
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the triggering geofences
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            if (triggeringGeofences != null) {
                for (geofence in triggeringGeofences) {
                    Log.d(TAG, "Geofence triggered: ${geofence.requestId}")
                    // Handle the geofence transition here (e.g., notify the user, start an action, etc.)
                    handleGeofenceAction(geofence, geofenceTransition)
                }
            }
        } else {
            Log.e(TAG, "Geofence transition type not recognized: $geofenceTransition")
        }
    }

    private fun handleGeofenceAction(geofence: Geofence, transitionType: Int) {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationHelper.sendHighPriorityNotification("Geofence Transition Enter","",MainActivity::class.java)
                // Code to execute when entering the geofence
                Log.d(TAG, "Entered geofence: ${geofence.requestId}")
                // You can trigger notifications or update your UI here
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationHelper.sendHighPriorityNotification("Geofence Transition Enter","",MainActivity::class.java)

                // Code to execute when exiting the geofence
                Log.d(TAG, "Exited geofence: ${geofence.requestId}")
                // Handle the exit action
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                notificationHelper.sendHighPriorityNotification("Geofence Transition Enter","",MainActivity::class.java)

                // Code to execute when exiting the geofence
                Log.d(TAG, "Exited geofence: ${geofence.requestId}")
                // Handle the exit action
            }

            else -> {

                Log.e(TAG, "Unknown transition type")
            }
        }
    }
}
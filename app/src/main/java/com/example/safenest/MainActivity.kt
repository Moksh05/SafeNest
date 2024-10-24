package com.example.safenest

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.safenest.Receiver.PowerButtonReceiver
import com.example.safenest.Service.SirenService
import com.example.safenest.databinding.ActivityGeofencingBinding
import com.example.safenest.databinding.ActivityMainBinding
import com.example.safenest.models.GeofenceModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var latitude =31.4804674
    var longitude=76.187401
    val GEOFENCE_ID = "mygeofence"
    private var geofenceList = mutableListOf<GeofenceModel>()
    val fencelatlng = LatLng(31.3515042, 76.1479397)
    private var db: FirebaseFirestore = Firebase.firestore

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencingHelper: GeofencingHelper
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_REQUEST_CODE =123

    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencingHelper = GeofencingHelper(this)
        //createGeofencePendingIntent()
        getcurrentlocation()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        if (Build.VERSION.SDK_INT >= 29) {
            // We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //setupGeofence(LatLng(latitude, longitude),1000f)
                getgeofencesfromdb()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show a dialog and ask for permission
                    showPermissionRationaleDialog()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                }
            }
        } else {
            //setupGeofence(LatLng(latitude, longitude),1000f)
            getgeofencesfromdb()
        }



        val backgroundService = Intent(
            applicationContext,
            SirenService::class.java
        )
        this.startService(backgroundService)
        Log.d(PowerButtonReceiver.SCREEN_TOGGLE_TAG, "Activity onCreate")

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment = HomeFragment()
            when (item.itemId) {

                R.id.nav_education -> selectedFragment = education()
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_contacts -> selectedFragment = Add_Contact()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

            true
        }
    }

    private fun setupGeofence(geofenceid : String ,latLng: LatLng, radius: Float) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            requestPermissions()
            return
        }

        // Create the Geofence
        val geofence: Geofence = geofencingHelper.getGeofence(
            geofenceid,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL
        )

        val geofencingRequest: GeofencingRequest = geofencingHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofencingHelper.getPendingIntent()
        Log.d("mmystringcheck2", "fence added with geoid : $geofenceid")
        // Add the Geofence
        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!).addOnSuccessListener {
            Log.d("mystringcheck2", "Geofence added... $geofenceid")
        }.addOnFailureListener { e ->
            val errorMsg = geofencingHelper.getErrorString(e)
            Log.d("mystringcheck", "Geofence failed $errorMsg")
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Request permissions using the fragment method
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with geofencing setup
                //setupGeofence()
            } else {
                // Permission denied, show a message to the user
                Log.e("Permissions", "Location permission denied.")
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun getcurrentlocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions()
            return
        }


        val marker = LatLng(latitude, longitude)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("mystringcheck","$latitude $longitude")
                }

            }

    }

    private fun getgeofencesfromdb(){
        db.collection("Geofences")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                for (document in result) {
                    val geofenceID = document.getString("geofenceid") ?: ""
                    val latitude = document.getString("latitude") ?: "0.0"
                    val longitude = document.getString("longitude") ?: "0.0"
                    val radius = document.getString("radius") ?: "100f"
                    val severity = document.getString("severity") ?: "5"
                    val geofenceData = GeofenceModel(geofenceID, latitude.toDouble(), longitude.toDouble(),radius.toFloat(),severity.toInt())
                    geofenceList.add(geofenceData)

                }
                Log.d("mystringcheck",geofenceList.toString())

                for (fence in geofenceList){
                    setupGeofence(fence.geofenceID,LatLng(fence.latitude,fence.longitude),fence.radius)
                }


            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun showPermissionRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Background Location Permission")
        builder.setMessage("This app requires background location access to trigger geofences even when the app is not in use.")
        builder.setPositiveButton("OK") { _, _ ->
            // Request the permission after showing the rationale
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Permission denied. Geofences won't work without background location.", Toast.LENGTH_SHORT).show()
        }
        builder.create().show()
    }

}
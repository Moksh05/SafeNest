package com.example.safenest

import android.Manifest
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var latitude =31.4804674
    var longitude=76.187401
    val GEOFENCE_ID = "mygeofence"
    val fencelatlng = LatLng(31.3515042, 76.1479397)


    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencingHelper: GeofencingHelper
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_REQUEST_CODE =123

    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()) // Load HomeFragment initially
                .commit()
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencingHelper = GeofencingHelper(this)
        //createGeofencePendingIntent()
        getcurrentlocation()

        if (Build.VERSION.SDK_INT >= 29) {
            // We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setupGeofence(LatLng(latitude, longitude),1000f)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                }
            }
        } else {
            setupGeofence(LatLng(latitude, longitude),1000f)
        }



        val backgroundService = Intent(
            applicationContext,
            SirenService::class.java
        )
        this.startService(backgroundService)
        Log.d(PowerButtonReceiver.SCREEN_TOGGLE_TAG, "Activity onCreate")
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = HomeFragment()
            when (item.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_education -> selectedFragment = HomeFragment()
                R.id.nav_sos -> selectedFragment = Add_Contact()
                R.id.nav_helpline -> selectedFragment = HomeFragment()
                R.id.nav_location -> selectedFragment = HomeFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }

            true
        }
    }

    private fun setupGeofence(latLng: LatLng, radius: Float) {
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
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL
        )
        val geofencingRequest: GeofencingRequest = geofencingHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofencingHelper.getPendingIntent()

        // Add the Geofence
        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!).addOnSuccessListener {
            Log.d("mystringcheck", "Geofence added...")
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
}
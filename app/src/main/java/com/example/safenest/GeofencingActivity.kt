package com.example.safenest

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.safenest.databinding.ActivityGeofencingBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions

class GeofencingActivity : AppCompatActivity(), OnMapReadyCallback {

    var latitude =31.4804674
    var longitude=76.187401
    val GEOFENCE_ID = "mygeofence"
    val fencelatlng = LatLng(31.3515042, 76.1479397)
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGeofencingBinding
//    private lateinit var geofencingClient: GeofencingClient
    //private lateinit var geofencingHelper: GeofencingHelper
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_REQUEST_CODE =123
    private lateinit var geofencePendingIntent: PendingIntent

    private val geofenceRadiusInMeters = 2000f // 2 km radius
    private val geofenceLat = 12.9716 // Hardcoded Latitude
    private val geofenceLng = 77.5946 // Hardcoded Longitude
    private val geofenceId = "GEOFENCE_ID"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGeofencingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        geofencingClient = LocationServices.getGeofencingClient(this)
//        geofencingHelper = GeofencingHelper(this)
        //createGeofencePendingIntent()
//        setupGeofence(LatLng(latitude, longitude),1000f)
    }


    private fun drawGeofenceCircle(lat: Double, lng: Double, radius: Float) {
        val center = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(center))
        mMap.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius.toDouble())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 255, 0, 0))
                .strokeWidth(5f)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
    }


//    private fun setupGeofence(latLng: LatLng, radius: Float) {
//        // Check for location permissions
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request permissions if not granted
//            requestPermissions()
//            return
//        }
//
//        // Create the Geofence
//        val geofence: Geofence = geofencingHelper.getGeofence(
//            GEOFENCE_ID,
//            latLng,
//            radius,
//            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL
//        )
//        val geofencingRequest: GeofencingRequest = geofencingHelper.getGeofencingRequest(geofence)
//        val pendingIntent: PendingIntent? = geofencingHelper.getPendingIntent()
//
//        // Add the Geofence
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!).addOnSuccessListener {
//            Log.d("mystringcheck", "Geofence added...")
//        }.addOnFailureListener { e ->
//            val errorMsg = geofencingHelper.getErrorString(e)
//            Log.d("mystringcheck", "Geofence failed $errorMsg")
//        }
//    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        mMap.addMarker(MarkerOptions().position(marker).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16f))
        enableUserlocation()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("mystringcheck","$latitude $longitude")
                }

            }


        drawGeofenceCircle(31.4804674, 76.187401, 1000f) // Example coordinates
    }




    private fun enableUserlocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
            return
        }
        mMap.isMyLocationEnabled = true
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
    }
}


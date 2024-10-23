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
import com.example.safenest.models.GeofenceModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class GeofencingActivity : AppCompatActivity(), OnMapReadyCallback {

    var latitude =31.4804674
    var longitude=76.187401
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGeofencingBinding
    private var geofenceList = mutableListOf<GeofenceModel>()
    private var db: FirebaseFirestore = Firebase.firestore
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_REQUEST_CODE =123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGeofencingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getgeofencesfromdb()
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    private fun drawGeofenceCircle(lat: Double, lng: Double, radius: Float,severity :Int) {


        var color = Color.argb(50, 255, 0, 0)
        if(severity == 5){
            color = Color.argb(50, 255, 0, 0)
        }else if(severity == 3){
            color = Color.argb(50, 255, 255, 0)
        }else{
            color = Color.argb(50, 0, 0, 255)
        }


        val center = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(center))
        mMap.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius.toDouble())
                .strokeColor(color)
                .fillColor(color)
                .strokeWidth(5f)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
    }



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

                drawfencescircle();
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun drawfencescircle(){
        Log.d("mystringcheck","drawfences ${geofenceList.toString()}")
        for(fence in geofenceList){
            drawGeofenceCircle(fence.latitude,fence.longitude,fence.radius, fence.severity )
        }
    }
}


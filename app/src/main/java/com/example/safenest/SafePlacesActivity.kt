package com.example.safenest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.safenest.databinding.ActivitySafePlacesBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class SafePlacesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySafePlacesBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySafePlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableUserLocation()

        findNearbyPlaces("hospital")
        findNearbyPlaces("police")
        findNearbyPlaces("doctor")
        findNearbyPlaces("train_station")
        findNearbyPlaces("fire_station")
        findNearbyPlaces("gas_station")

    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun findNearbyPlaces(placeType: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                Toast.makeText(this, "$location", Toast.LENGTH_SHORT).show()

                val placeUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=$latitude,$longitude" +
                        "&radius=2000" +
                        "&type=$placeType" +
                        "&key=AIzaSyBm7hwsMVFAqaMDSWtis1PFaOmm_MZ2I3Q"

                fetchNearbyPlaces(placeUrl)
            } else {
                Toast.makeText(this, "Could not fetch current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchNearbyPlaces(placeUrl: String) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(placeUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Log failure message in case of a failed request
                Log.e("SafePlacesActivity", "Failed to fetch nearby places: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    Log.d("SafePlacesActivity", "Nearby places response: $responseData")

                    val jsonResponse = JSONObject(responseData)
                    val resultsArray = jsonResponse.getJSONArray("results")

                    for (i in 0 until resultsArray.length()) {
                        val place = resultsArray.getJSONObject(i)
                        val geometry = place.getJSONObject("geometry")
                        val location = geometry.getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")
                        val placeName = place.getString("name")
                        val vicinity = place.getString("vicinity")

                        runOnUiThread {
                            val placeLatLng = LatLng(lat, lng)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(placeLatLng)
                                    .title(placeName)
                                    .snippet(vicinity)
                            )
                        }
                    }
                } ?: run {
                    Log.e("SafePlacesActivity", "Response body is null")
                }
            }
        })
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

package com.example.safenest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.viewpager2.widget.ViewPager2
import com.example.safenest.Receiver.PowerButtonReceiver
import com.example.safenest.adapters.Card
import com.example.safenest.adapters.SliderviewAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices



class HomeFragment : Fragment() {
    private val PERMISSION_REQUEST_CODE = 100
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewPager: ViewPager2
    private lateinit var sosbutton: CardView
    private lateinit var cardAdapter: SliderviewAdapter
    private val phoneNumberList = mutableListOf<Pair<String, String>>()
    //private lateinit var handler: Handler
    //private lateinit var fakeCallRunnable: Runnable
    private lateinit var powerButtonReceiver: PowerButtonReceiver

    private lateinit var fakecall : CardView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestPermissions()
        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        // Find the ViewPager2
        Log.d("line44","crashing here")
        viewPager = view.findViewById(R.id.viewPager)
        sosbutton = view.findViewById(R.id.sos_button)
        fakecall = view.findViewById((R.id.fakecall_button))
        // Create the card list
        val cardList = listOf(
            Card(R.drawable.login_img, "Police", "1-0-0"),
            Card(R.drawable.login_img, "Hospital", "1-0-1"),
            Card(R.drawable.login_img, "Police", "1-0-0"),
            Card(R.drawable.login_img, "Hospital", "1-0-1")
        )

        // Set up the adapter
        cardAdapter = SliderviewAdapter(cardList)
        viewPager.adapter = cardAdapter

        Log.d("line60","crashing here")
        sosbutton.setOnClickListener {


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


            sendSos()

        }

        fakecall.setOnClickListener {
            val fakeCallIntent = Intent(requireContext(), callscreenActivity::class.java)
           startActivity(fakeCallIntent)
        }
//        handler = Handler(Looper.getMainLooper())
//
//        // Initialize the Runnable to launch the FakeCallActivity
//        fakeCallRunnable = Runnable {
//            val fakeCallIntent = Intent(requireContext(), fakecall::class.java)
//            startActivity(fakeCallIntent)
//        }
//
//        fakecall.setOnClickListener {
//            handler.postDelayed(fakeCallRunnable, 10000)
//
//        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Request permissions using the fragment method
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    private fun checkPermissions(): Boolean {
        val sendSmsPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.SEND_SMS
        )
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        return sendSmsPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted, proceed with your functionality
                Toast.makeText(requireContext(), "Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, show an appropriate message
                Toast.makeText(requireContext(), "Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun sendSos() {
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions()
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val message =
                            "SOS! I need help. My current location is: https://maps.google.com/?q=$latitude,$longitude"

                        sendSMS("+919821063740", message)
                    }
                }
        } else {
            requestPermissions()
        }
    }
    fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(requireContext(), "SOS Sent!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "SMS failed to send: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getallcontacts(){
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all

        // Iterate through all entries in SharedPreferences
        for ((key, value) in allEntries) {
            if (key.startsWith("saved_phone_number_")) {
                val contactName = key.removePrefix("saved_phone_number_")
                val contactNumber = value.toString()
                // Log the contact name and number
                phoneNumberList.add(Pair(contactName,contactNumber))
                Log.d("StoredContact", "Name: $contactName, Number: $contactNumber")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //handler.removeCallbacks(fakeCallRunnable) // Remove any pending fake call if the fragment view is destroyed
    }

    override fun onResume() {
        super.onResume()

        // Initialize the powerButtonReceiver
        powerButtonReceiver = PowerButtonReceiver()

        // Create the IntentFilter for screen off/on actions
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)

        // Register the receiver
        requireContext().registerReceiver(powerButtonReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        // Unregister the receiver
        requireContext().unregisterReceiver(powerButtonReceiver)
    }

}



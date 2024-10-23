package com.example.safenest.models

data class SosModel(
    val msg : String = "Emergency! I need help. My current location is attached. Please reach out to me immediately or contact the authorities. This is an SOS alert. Stay in touch until I confirm I am safe.",
    val locationlink : String,
    val useremail : String,
    val phonenumber:String
)

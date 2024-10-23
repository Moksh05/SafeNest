package com.example.safenest.models

data class GeofenceModel(
    val geofenceID: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val severity : Int
)

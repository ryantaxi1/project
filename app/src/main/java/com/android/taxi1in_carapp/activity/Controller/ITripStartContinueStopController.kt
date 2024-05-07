package com.android.taxi1in_carapp.activity.Controller


interface ITripStartContinueStopController {
    fun sendTripStartContinueStop(
        tripId: Int,
        carType: String,
        address: String,
        latitude: Double,
        longitude: Double,
        speed: Int,
        tripStatus: String,
        additionalCharge: Double,
        dateTime: String,
        platNumber: String,
        driverId: String,
        isDistance: Boolean
    )

    fun getActiveTrip()
}

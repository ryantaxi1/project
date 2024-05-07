package com.taxi1.activity.Controller


interface ITripStartContinueStopController {
    fun sendTripStartContinueStop(
        tripId: Int,
        carType: String,
        address: String,
        latitude: Double,
        longitude: Double,
        speed: Int,
        tripStatus: String,
        additionalCharge: Int,
        dateTime: String,
        platNumber: String,
        driverId: String
    )
}

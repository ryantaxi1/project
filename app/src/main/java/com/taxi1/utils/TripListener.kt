package com.taxi1.utils

interface TripListener {
    fun onActiveTrip()
    fun onArrivedTrip()
    fun onFreeRide()
}
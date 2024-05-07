package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.Response.TripStartContinueStopResponse

interface ITripStartContinueStopView {
    fun onResponse(tripResponse: TripStartContinueStopResponse)
    fun getActiveTripResponse(tripResponse: TripStartContinueStopResponse)
}
package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.TripStartContinueStopResponse

interface ITripStartContinueStopView {
    fun onResponse(tripResponse: TripStartContinueStopResponse)
}
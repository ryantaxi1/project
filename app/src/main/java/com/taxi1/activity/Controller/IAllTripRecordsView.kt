package com.taxi1.activity.Controller

import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.taxi1.Retrofit.Response.CommanResponse

interface IAllTripRecordsView {
    fun onResponse(allTripRecResponse: AllTripRecordsResponse)
    fun deleteTripOnResponse(deleteTripResponse: CommanResponse)
}
package com.android.taxi1in_carapp.activity.Controller

import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.taxi1.Retrofit.Response.AllTripRecordsResponse

interface IAllTripRecordsView {
    fun onResponse(allTripRecResponse: AllTripRecordsResponse)
    fun deleteTripOnResponse(deleteTripResponse: CommonResponse)
}
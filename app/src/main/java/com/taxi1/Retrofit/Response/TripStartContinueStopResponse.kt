package com.taxi1.Retrofit.Response


import com.google.gson.annotations.SerializedName

data class TripStartContinueStopResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("tripStatus")
    var tripStatus: Int?,
    @SerializedName("tripId")
    var tripId: Int?
)
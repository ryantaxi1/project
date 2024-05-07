package com.android.taxi1in_carapp.Retrofit.Response


import com.google.gson.annotations.SerializedName

data class TripStartContinueStopResponse(
    @SerializedName("finalCharge")
    var finalCharge: String?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("mode")
    var mode: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("tripId")
    var tripId: Int?,
    @SerializedName("tripStatus")
    var tripStatus: Int?
)
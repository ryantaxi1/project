package com.android.taxi1in_carapp.Retrofit.newresponse


import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)
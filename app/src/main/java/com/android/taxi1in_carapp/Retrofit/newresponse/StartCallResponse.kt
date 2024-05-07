package com.android.taxi1in_carapp.Retrofit.newresponse


import com.google.gson.annotations.SerializedName

data class StartCallResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: Result?,
    @SerializedName("status")
    var status: Int?
) {
    data class Result(
        @SerializedName("call_id")
        var callId: Int?
    )
}
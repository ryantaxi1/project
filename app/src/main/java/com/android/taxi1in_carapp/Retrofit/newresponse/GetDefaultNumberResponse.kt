package com.android.taxi1in_carapp.Retrofit.newresponse


import com.google.gson.annotations.SerializedName

data class GetDefaultNumberResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: List<Result?>?,
    @SerializedName("status")
    var status: Int?
) {
    data class Result(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("number")
        var number: String?
    )
}
package com.android.taxi1in_carapp.Retrofit.newresponse


import com.google.gson.annotations.SerializedName

data class SigninResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: Result?,
    @SerializedName("status")
    var status: Int?
) {
    data class Result(
        @SerializedName("driverId")
        var driverId: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("firstName")
        var firstName: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("lastName")
        var lastName: String?,
        @SerializedName("phone")
        var phone: String?,
        @SerializedName("plateNumber")
        var plateNumber: String?,
        @SerializedName("token")
        var token: String?
    )
}
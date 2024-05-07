package com.android.taxi1in_carapp.Retrofit.newresponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetProfileResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: Result?,
    @SerializedName("status")
    var status: Int?
):Serializable {
    data class Result(
        @SerializedName("driverId")
        var driverId: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("firstName")
        var firstName: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("lastName")
        var lastName: String?,
        @SerializedName("phone")
        var phone: String?,
        @SerializedName("plateNumber")
        var plateNumber: String?,
        @SerializedName("token")
        var token: String?,
        @SerializedName("distance")
        var distance: Int?,
        @SerializedName("distanceType")
        var distanceType: String?,
        @SerializedName("vehicalData")
        var vehicalData: List<VehicalData>?
    ):Serializable {
        data class VehicalData(
            @SerializedName("vehicalId")
            var vehicalId: Int?,
            @SerializedName("vehicalNumber")
            var vehicalNumber: String?
        ):Serializable
    }
}
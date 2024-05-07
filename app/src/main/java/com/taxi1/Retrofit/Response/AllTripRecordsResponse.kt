package com.taxi1.Retrofit.Response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllTripRecordsResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: ArrayList<Result>?,
    @SerializedName("status")
    var status: Int?
):Serializable {
    data class Result(
        @SerializedName("additionalCharge")
        var additionalCharge: String?,
        @SerializedName("continueTripArray")
        var continueTripArray: ArrayList<ContinueTripArray>?,
        @SerializedName("finalChage")
        var finalChage: String?,
        @SerializedName("startLocation")
        var startLocation: String?,
        @SerializedName("stopLocation")
        var stopLocation: String?,
        @SerializedName("totalTravelCharge")
        var totalTravelCharge: String?,
        @SerializedName("totalWaitingCharge")
        var totalWaitingCharge: String?,
        @SerializedName("carCharge")
        var carCharge: String?,
        @SerializedName("tripId")
        var tripId: Int?,
        @SerializedName("trip_start_time")
        var tripStartTime: String?,
        @SerializedName("trip_stop_time")
        var tripStopTime: String?
    ):Serializable {
        data class ContinueTripArray(
            @SerializedName("carType")
            var carType: String?,
            @SerializedName("fixCharge")
            var fixCharge: String?,
            @SerializedName("id")
            var id: Int?,
            @SerializedName("travelCharge")
            var travelCharge: String?,
            @SerializedName("tripDateTime")
            var tripDateTime: String?,
            @SerializedName("tripId")
            var tripId: String?,
            @SerializedName("tripStatus")
            var tripStatus: String?,
            @SerializedName("triplocation")
            var triplocation: String?,
            @SerializedName("waitingCharge")
            var waitingCharge: String?
        ):Serializable
    }
}
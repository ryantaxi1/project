package com.taxi1.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "tripData")
data class TripData(
//    @PrimaryKey(autoGenerate = true)val id: Int? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    var userID: Int? = null,

    var tripId: Int? = null,

    var isTripIdHeadingShow: Boolean = false,

    var userName: String? = null,

    var dateTime: String? = null,

    var travelAmount: Int? = null,

    var waitingCharges: Int? = null,

    var title: String? = null,

    var currentLocation: String? = null,

    var startLocation: String? = null,

    var endLocation: String? = null,

    var additionalCharge: Int? = null,

    var carFare: Int? = null

): Serializable
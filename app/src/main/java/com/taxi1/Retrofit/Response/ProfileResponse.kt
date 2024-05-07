package com.taxi1.Retrofit.Response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ProfileResponse {

    @SerializedName("status")
    @Expose
    private var status: Int? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("result")
    @Expose
    private var result: Result? = null

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getResult(): Result? {
        return result
    }

    fun setResult(result: Result?) {
        this.result = result
    }
    class Result {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("token")
        @Expose
        var token: String? = null

        @SerializedName("plate_number")
        @Expose
        var plate_number: String? = null

        @SerializedName("driver_id")
        @Expose
        var driver_id: String? = null
    }
}
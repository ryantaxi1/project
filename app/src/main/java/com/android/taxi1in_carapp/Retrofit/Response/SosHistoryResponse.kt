package com.taxi1.Retrofit.Response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SosHistoryResponse {

    @SerializedName("status")
    @Expose
    private var status: Int? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("result")
    @Expose
    private var result: List<Result?>? = null

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

    fun getResult(): List<Result?>? {
        return result
    }

    fun setResult(result: List<Result?>?) {
        this.result = result
    }
    class Result {
        @SerializedName("number")
        @Expose
        var number: String? = null

        @SerializedName("startDateTime")
        @Expose
        var startDateTime: String? = null

        @SerializedName("endDateTime")
        @Expose
        var endDateTime: String? = null
    }
}
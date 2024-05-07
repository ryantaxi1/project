package com.taxi1.Retrofit.Response

import com.google.gson.annotations.SerializedName

class CommanResponse {
    @SerializedName("message")
    private var mMessage: String? = null

    @SerializedName("status")
    private var mStatus: Int? = null

    fun getMessage(): String? {
        return mMessage
    }

    fun setMessage(message: String?) {
        mMessage = message
    }

    fun getStatus(): Int? {
        return mStatus
    }

    fun setStatus(status: Int?) {
        mStatus = status
    }
}
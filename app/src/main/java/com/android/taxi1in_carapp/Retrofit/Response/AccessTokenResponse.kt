package com.taxi1.Retrofit.Response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class AccessTokenResponse {

    @SerializedName("token")
    @Expose
    private var token: String? = null

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }
}
package com.taxi1.Retrofit.Response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class StaticPageResponse {


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

        @SerializedName("page_header")
        @Expose
        var pageHeader: String? = null

        @SerializedName("bar_title")
        @Expose
        var barTitle: String? = null

        @SerializedName("meta_keyword")
        @Expose
        var metaKeyword: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("content")
        @Expose
        var content: String? = null
    }
}
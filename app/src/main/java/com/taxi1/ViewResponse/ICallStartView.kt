package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.Retrofit.Response.StartCallResponse

interface ICallStartView {
    fun onCallStart(data: StartCallResponse.Result?)
}
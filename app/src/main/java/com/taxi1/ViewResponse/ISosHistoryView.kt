package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.Retrofit.Response.ProfileResponse
import com.taxi1.Retrofit.Response.SosHistoryResponse

interface ISosHistoryView {
    fun onResponse(login: List<SosHistoryResponse.Result?>?)
}
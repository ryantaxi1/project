package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.Retrofit.Response.ProfileResponse

interface IProfileView {
    fun onResponse(login: ProfileResponse.Result?)
}
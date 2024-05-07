package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.AccessTokenResponse
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.Retrofit.Response.ProfileResponse

interface IAccessTokenView {
    fun onResponseToken(data: AccessTokenResponse)
}
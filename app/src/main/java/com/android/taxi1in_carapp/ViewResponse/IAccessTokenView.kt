package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.AccessTokenResponse

interface IAccessTokenView {
    fun onResponseToken(data: AccessTokenResponse)
}
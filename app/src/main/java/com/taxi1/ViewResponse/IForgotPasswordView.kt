package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse

interface IForgotPasswordView {
    fun onForgot(login: CommanResponse?)
}
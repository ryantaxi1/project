package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse

interface ILoginView {
    fun onLogin(login: LoginResponse?)
    fun onFailLogin(comman: CommanResponse?)

}
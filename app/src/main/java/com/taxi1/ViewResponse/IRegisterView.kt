package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.RegisterResponse


interface IRegisterView {
    fun onRegister(register: RegisterResponse)
}
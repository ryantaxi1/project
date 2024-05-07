package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.SignupResponse


interface IRegisterView {
    fun onRegister(register: SignupResponse)
}
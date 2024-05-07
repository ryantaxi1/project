package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.SigninResponse

interface ILoginView {
    fun onLogin(login: SigninResponse?)
    fun onFailLogin(comman: CommonResponse?)

}
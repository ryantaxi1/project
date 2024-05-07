package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.*

interface IDefaultNumberView {
    fun onResponse(login: List<DefaultNumberResponse.Result?>?)
}
package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.GetDefaultNumberResponse
import com.taxi1.Retrofit.Response.*

interface IDefaultNumberView {
    fun onResponseDefaultNumber(response: List<GetDefaultNumberResponse.Result?>?)
}
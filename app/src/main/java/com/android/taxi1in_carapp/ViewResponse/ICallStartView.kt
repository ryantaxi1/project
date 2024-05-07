package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.StartCallResponse

interface ICallStartView {
    fun onCallStart(data: StartCallResponse.Result?)
}
package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.CommanResponse

interface ISendGpsLocationView {
    fun onSendGpsLocation(gpsLocationRes: CommanResponse?)
}
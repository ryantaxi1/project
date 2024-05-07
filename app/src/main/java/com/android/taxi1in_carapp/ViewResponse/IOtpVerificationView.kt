package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse

interface IOtpVerificationView {
    fun onOtpVerifyResponse(response: CommonResponse?)
}
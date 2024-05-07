package com.taxi1.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse

interface IProfileView {
    fun onResponse(response: GetProfileResponse.Result?)
}
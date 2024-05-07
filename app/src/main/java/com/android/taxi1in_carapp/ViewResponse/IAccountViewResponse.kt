package com.android.taxi1in_carapp.ViewResponse

import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse

interface IAccountViewResponse {
    fun onResponse(response: GetProfileResponse.Result?)
    fun onLogout(logout: CommonResponse)
}
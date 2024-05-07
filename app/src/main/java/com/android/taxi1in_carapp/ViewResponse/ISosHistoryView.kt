package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.SosHistoryResponse

interface ISosHistoryView {
    fun onResponse(login: List<SosHistoryResponse.Result?>?)
}
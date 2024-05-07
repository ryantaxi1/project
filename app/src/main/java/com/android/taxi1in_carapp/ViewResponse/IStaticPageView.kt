package com.taxi1.ViewResponse

import com.taxi1.Retrofit.Response.StaticPageResponse


interface IStaticPageView {
    fun onstaticPage(staticpage: StaticPageResponse.Result?)
}
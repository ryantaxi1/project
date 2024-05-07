package com.android.taxi1in_carapp

import android.app.Application
import android.content.pm.ActivityInfo
import com.taxi1.utils.Utils

class Taxi1BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /*if (Utils.isTablet(this)){
            applicationContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
    }
}
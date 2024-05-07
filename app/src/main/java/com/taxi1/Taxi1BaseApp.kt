package com.taxi1

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.taxi1.activity.ui.home.HomeFragment


class Taxi1BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(HomeFragment())
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
package com.taxi1.app

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner


open class MainApplication : Application(), LifecycleObserver {

    companion object {
        var activity: Activity? = Activity()
        var inBackground = true
        private var instance: MainApplication? = MainApplication()
        fun applicationContext(): Context {
            if (instance == null) {
                instance = MainApplication()
            }
            return instance!!.applicationContext
        }

        fun getCurrentActivity(): Activity? {
            if (activity == null) {
                activity = Activity()
            }
            return activity
        }

        fun setCurrentActivity(activity: Activity) {
            this.activity = activity
        }

        fun hideActionBar(actionBar: ActionBar?) {
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.hide()
        }

        fun checkInBackground(): Boolean {
            return inBackground
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        inBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        inBackground = false
    }


}
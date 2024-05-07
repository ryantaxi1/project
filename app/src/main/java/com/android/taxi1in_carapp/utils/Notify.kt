package com.taxi1.utils

import android.app.Activity
import android.content.Context
import com.tapadoo.alerter.Alerter
import android.widget.Toast
import com.android.taxi1in_carapp.R
import com.taxi1.app.MainApplication


object Notify {

    fun Toast(message: String?) {
        Toast.makeText(
            MainApplication.applicationContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun alerterRed(
        activity: Context?,
        message: String?
    ) {
        if (activity != null) Alerter.create(activity as Activity?)
            .setText(message!!)
            .setIcon(R.drawable.warning_sign)
            .setBackgroundColorRes(R.color.red)
            .show()
    }

    fun alerterGreen(activity: Activity?, message: String?) {
        if (activity != null) Alerter.create(activity)
            .setText(message!!)
            .setTextAppearance(R.color.white)
            .setBackgroundColorRes(R.color.theme_color)
            .show()
    }

}
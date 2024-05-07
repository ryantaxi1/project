/*
package com.taxi1.Auth

import android.app.Activity
import android.util.Patterns
import com.android.taxi1in_carapp.R
import com.taxi1.utils.InternetCheck
import com.taxi1.utils.Notify

object ForgotPasswordAuth {

    fun authenticate(
        email: String,
        activity: Activity
    ): Boolean {
        var check = false

        if (email.isEmpty()) {
            if (email.isEmpty()) {
                Notify.alerterRed(
                    activity,
                    activity.resources.getString(R.string.enter_email_err_msg)
                )
                check = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Notify.alerterRed(
                    activity,
                    activity.resources.getString(R.string.email_length_err_msg)
                )
                check = false
            }

        }
        else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(activity)!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(activity)
                check = false
            }
        }
        return check
    }
}*/

package com.android.taxi1in_carapp.Auth

import android.app.Activity
import com.android.taxi1in_carapp.R
import com.taxi1.utils.InternetCheck
import com.taxi1.utils.Notify

object ChangePasswordAuth {

    fun authenticate(
        password: String,
        confirmPassword: String,
        confirmNewPassword: String,
        activity: Activity
    ): Boolean {
        var check = false

        if (password.isEmpty()) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.password_err_msg)
            )
            check = false
        }
        else if(confirmPassword.isEmpty())
        {
            Notify.alerterRed(
            activity,
            activity.resources.getString(R.string.confirm_password_err_msg)
        )
            check = false

        }
        else if (confirmNewPassword.isEmpty()) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.confirm_new_password_err_msg)
            )
            check = false
        } else if (confirmPassword != confirmNewPassword) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.password_match_err_msg)
            )
            check = false
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
}
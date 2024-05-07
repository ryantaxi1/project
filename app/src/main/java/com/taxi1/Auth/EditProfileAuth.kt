package com.taxi1.Auth

import android.app.Activity
import android.util.Patterns
import com.taxi1.R
import com.taxi1.utils.InternetCheck
import com.taxi1.utils.Notify

object EditProfileAuth {

    fun authenticate(
        username:String,
        email: String,
        phoneNumber: String,
        plateNumber: String,
        activity: Activity
    ): Boolean {
        var check = false

        if (username.isEmpty()) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.username_err_msg)
            )
            check = false
        }

        else if (email.isEmpty()) {
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
        else if (phoneNumber.isEmpty()) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_phone_number)
            )
            check = false
        }
        else if (phoneNumber.length < 11) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.phone_number_not_valid)
            )
            check = false
        }
        else if (plateNumber.isEmpty()) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_plate_number)
            )
            check = false
        }else if (plateNumber.length > 5) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_plate_valid_number)
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
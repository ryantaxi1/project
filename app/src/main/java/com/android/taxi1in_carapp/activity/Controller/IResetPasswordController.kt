package com.android.taxi1in_carapp.activity.Controller

interface IResetPasswordController {
    fun resetPassword(email: String, newPassword: String, confirmPassword: String)
}
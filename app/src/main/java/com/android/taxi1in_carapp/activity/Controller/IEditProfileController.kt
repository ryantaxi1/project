package com.android.taxi1in_carapp.activity.Controller

import java.io.File

interface IEditProfileController {
    fun editProfile(firstName: String, lastName: String, phone: String, email: String, image: File?)
}
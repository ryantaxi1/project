package com.android.taxi1in_carapp.activity.Controller

interface IRegisterController {
    fun register(firstname:String, lastName: String, email:String, mobile:String, driverId : String,plateNumber : String, password:String, confirmPassword:String)
}
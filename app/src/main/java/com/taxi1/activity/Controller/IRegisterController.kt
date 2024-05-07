package com.taxi1.activity.Controller

interface IRegisterController {
    fun register(usernmame:String,email:String,mobile:String,driverId : String,plateNumber : String,confirmPassword:String)
}
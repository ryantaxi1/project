package com.taxi1.activity.Controller

interface ISendGPSAddressController {
    fun locationAddress(address:String,lat:String,long:String, speed:String, direction:String)
}
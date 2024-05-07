package com.taxi1.activity.Controller

interface IStoreAddressController {
    fun storeAddress(address:String,lat:String,long:String, callId:String, apiTime: String)
}
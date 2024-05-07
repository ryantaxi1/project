package com.taxi1.utils

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.*

class LocationAddress {
    private val tag = "LocationAddress"
    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double, context: Context, handler: Handler,
    ) {
        val strAddress = Utils.getAddressFromLatLng(context, latitude, longitude)

        val message = Message.obtain()
        message.target = handler
        message.what = 1
        val bundle = Bundle()
        /*   result = ("Latitude: " + latitude + " Longitude: " + longitude +
                   "\n\nAddress:\n" + result)*/
        bundle.putString("address", strAddress)
        message.data = bundle
        message.sendToTarget()
    }

  /*      val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var result: String = null.toString()
                try {
                    val addressList = geoCoder.getFromLocation(latitude, longitude, 1)
                 *//*   val addressList = geoCoder.getFromLocation(
                            22.2851, 70.7482, 1
                    )*//*
                    if ((addressList != null && addressList.size > 0)) {
                        val address = addressList.get(0)
                        val sb = StringBuilder()
                        for (i in 0 until address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        //sb.append(address.thoroughfare).append("\n")
                        //sb.append(address.subThoroughfare).append("\n")
                        //sb.append(address.subLocality).append("\n")
                        //sb.append(address.adminArea).append("\n")
                        //sb.append(address.subAdminArea).append("\n")
                        //sb.append(address.featureName).append("\n")
                        //sb.append(address.locality).append("\n")
                        //sb.append(address.postalCode).append("\n")
                        //sb.append(address.countryName)
                        var sublocality = ""
                        if(address.subLocality != null && address.subLocality.length > 0)
                        {
                           sublocality = address.subLocality + ", "
                        }

                        if(address.subAdminArea != null && address.locality != null)
                        if(address.subAdminArea.equals(address.locality))
                        {
                            //sb.append(address.featureName).append(", ")
                            sb.append(sublocality)
                            sb.append(address.locality).append(", ")
                            sb.append(address.adminArea).append(", ")
                            sb.append(address.postalCode)
                        }
                        else{
                            //sb.append(address.featureName).append(", ")
                            sb.append(sublocality)
                            sb.append(address.locality).append(", ")
                            sb.append(address.subAdminArea).append(", ")
                            sb.append(address.adminArea).append(", ")
                            sb.append(address.postalCode)
                        }
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(tag, "Unable connect to GeoCoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                 *//*   result = ("Latitude: " + latitude + " Longitude: " + longitude +
                            "\n\nAddress:\n" + result)*//*
                    bundle.putString("address", result)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }*/
}
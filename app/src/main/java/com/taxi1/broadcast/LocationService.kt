package com.taxi1.broadcast

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.ViewResponse.ICommanView
import com.taxi1.activity.Controller.IStoreAddressController
import com.taxi1.activity.Controller.StoreAddressController
import com.taxi1.utils.LocationAddress
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


internal class LocationService : Service() {
    val mHandler = Handler()
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    var locationName: String = ""
    var callId: String = ""
    var isCallStart: Boolean = false
    val NOTIFICATION_CHANNEL_ID = "location_channel"
    private var previousLocation: Location? = null
    private var minDistance = 0.05F

    private val binder: IBinder = LocalBinder()

    class LocalBinder : Binder() {
        val service: LocationService
            get() = LocationService()
// Return this instance of YourForegroundService so clients can call public methods
    }

    override fun onCreate() {
        super.onCreate()
        //        compID = SharePref.getCompanyId(getApplicationContext());
//        userID = SharePref.getUserId(getApplicationContext());

        startForeground(NOTIFICATION_ID, createNotification());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ///   UserarrayList.clear();
        /**
         * That is comment due to handle to add location callback
         * when sos stop then not stop to location so it is comment.
         */
        /*mHandler.postDelayed(object : Runnable {
            override fun run() {
                Log.e("Service is Running", "" + System.currentTimeMillis())
                //                DBHelper dbHelper = DBHelper.getInstance(context);
//                UserarrayList = dbHelper.getusers(currentDate1);

                val storedata = StoreUserData(applicationContext)
                isCallStart = storedata.getBoolean("isCallStart")

                Log.e(TAG, "isCallStart=" + isCallStart)


                if (isCallStart) {
                    Log.e(TAG, "isCallStart1=" + isCallStart)

                    Handler().postDelayed(this, 5000)

                    FusedLocationChanged()

                } else {
                    Log.e(TAG, "isCallStart2=" + isCallStart)

                    stopLocationUpdates()
                    stopForegroundService()
                }
            }
        }, 5000)*/


        val storedata = StoreUserData(applicationContext)
        isCallStart = storedata.getBoolean("isCallStart")

        Log.e(TAG, "isCallStart=" + isCallStart)

        if (isCallStart) {
            Log.e(TAG, "isCallStart1=" + isCallStart)

            FusedLocationChanged()

            handlerRunnable()

        } else {
            Log.e(TAG, "isCallStart2=" + isCallStart)

            stopLocationUpdates()
            stopForegroundService()
        }

//        runnable = object : Runnable {
//            override fun run() {
//                // Perform your background task here
//                Log.d(TAG, "Background task executed")
//                FusedLocationChanged()
//
//                // Schedule the next execution
//                mHandler.postDelayed(this, 5000) // Example: Run every 1 second
//            }
//        }


    }

    private fun createNotification(): Notification {
//        val notificationIntent = Intent(this, DashboardActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, notificationIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
            .setInterval(3000) // Update interval in milliseconds
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (fusedLocationClient != null) {
                fusedLocationClient!!.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            }

        }
    }

    fun handlerRunnable() {
        mHandler.postDelayed({

            if(isCallStart){
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("Australia/Sydney")
                val currentDateTime = Date()
                var apiTime = dateFormat.format(currentDateTime)

                Log.e(TAG, "locationResult current date time :: $apiTime")

                if(previousLocation != null){
                    apiCall(previousLocation?.latitude!!, previousLocation?.longitude!!, apiTime)
                    previousLocation = null
                }else{

                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient?.lastLocation
                            ?.addOnSuccessListener { location ->
                                if (location != null) {
                                    apiCall(location.latitude, location.longitude, apiTime)
                                } else {
                                    Log.e(TAG, "No last known location found")
                                }
                            }
                            ?.addOnFailureListener { e ->
                                // Handle failure to get last location
                                Log.e(TAG, "Error getting last location: ${e.message}")
                            }
                    }else{
                        Log.e(TAG, "Error getting last location permission not granted")
                    }
                }

                handlerRunnable()

            }else{
                Log.e(TAG, "Handler call start false")
//                mHandler.removeCallbacksAndMessages(null)
            }

        }, 3000)
    }

    fun apiCall(curlatitude: Double, curlongitude: Double, apiTime: String){
        Toast.makeText(
            applicationContext,
            "Service_Lat::" + curlatitude + "=Service_Long::" + curlongitude,
            Toast.LENGTH_LONG
        ).show()

        val locationAddress = LocationAddress()

        locationAddress.getAddressFromLocation(
            curlatitude,
            curlongitude,
            applicationContext,
            GeoCodeHandler()
        )
        val storedata = StoreUserData(applicationContext)
        callId = storedata.getString("callId")
        Log.e(TAG, "callId=" + callId)

        Log.e(TAG, "Location update $locationName and call id :- $callId")

        val controller: IStoreAddressController =
            StoreAddressController(applicationContext, mycommonview!!)

        controller!!.storeAddress(
            locationName,
            curlatitude.toString(),
            curlongitude.toString(),
            callId ?: "",
            apiTime
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "Service onDestroy");
        stopLocationUpdates()

    }

    fun stopLocationUpdates() {
        try {
            if (mHandler != null)
                mHandler.removeCallbacksAndMessages(null);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (fusedLocationClient != null) {
            fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
            locationCallback = null
            fusedLocationClient = null
        }
        stopForeground(true);
        stopSelf();

    }

    fun stopForegroundService() {

        stopForeground(true)
        stopSelf()
    }

    fun FusedLocationChanged() {


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

//                if (isCallStart) {
//                    val location = locationResult.lastLocation
//
//                    Log.d(TAG, "Location: " + location.latitude + ", " + location.longitude)
//
//                    val locationAddress = LocationAddress()
//
//                    val curlatitude = location!!.latitude
//                    val curlongitude = location!!.longitude
//
//                    Toast.makeText(applicationContext, "Service_Lat::" + curlatitude + "=Service_Long::" + curlongitude, Toast.LENGTH_LONG).show()
//
//                    locationAddress.getAddressFromLocation(
//                        curlatitude,
//                        curlongitude,
//                        applicationContext,
//                        GeoCodeHandler()
//                    )
//                    val controller: IStoreAddressController = StoreAddressController(
//                        applicationContext,
//                        mycommonview!!
//                    )
//                    val storedata = StoreUserData(applicationContext)
//                    callId = storedata.getString("callId")
//                    Log.e(TAG, "callId=" + callId)
//
//                    controller!!.storeAddress(
//                        locationName,
//                        curlatitude.toString(),
//                        curlongitude.toString(),
//                        callId ?: ""
//                    )
//                }
                Log.d(TAG, "location service callback: $isCallStart")
                if (isCallStart) {
                    Log.e(TAG, "locationResult ::" + locationResult.locations)

//                    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
//                    dateFormat.timeZone = TimeZone.getTimeZone("Australia/Sydney")
//                    val currentDateTime = Date()
//                    var apiTime = dateFormat.format(currentDateTime)
//
//                    Log.e(TAG, "locationResult current date time :: $apiTime")

                    locationResult ?: return

                    locationResult.locations.forEach { location ->
                        Log.e(TAG, "location ::" + location)

                        previousLocation?.let {
                            val distance = it.distanceTo(location)
                            if (distance < minDistance) {
                                minDistance = distance
                                // Update UI or do something with the latest location
                            }
                        }
                        previousLocation = location
                        Log.e(TAG, "previousLocation ::" + previousLocation)



//                        if (previousLocation != null) {
//                            val curTime = Time(System.currentTimeMillis())
//                            Log.e(
//                                TAG,
//                                "onLocationChanged invoked : current time ${curTime.minutes} : ${curTime.seconds}"
//                            )
//
//                            val curlatitude = previousLocation!!.latitude
//                            val curlongitude = previousLocation!!.longitude
//
//                            Log.e(TAG, "Lat : $curlatitude  Long : $curlongitude")
//
//                            Toast.makeText(
//                                applicationContext,
//                                "Service_Lat::" + curlatitude + "=Service_Long::" + curlongitude,
//                                Toast.LENGTH_LONG
//                            ).show()
//
//                            val locationAddress = LocationAddress()
//
//                            locationAddress.getAddressFromLocation(
//                                curlatitude,
//                                curlongitude,
//                                applicationContext,
//                                GeoCodeHandler()
//                            )
//                            val storedata = StoreUserData(applicationContext)
//                            callId = storedata.getString("callId")
//                            Log.e(TAG, "callId=" + callId)
//
//                            Log.e(TAG, "Location update $locationName and call id :- $callId")
//
//                            val controller: IStoreAddressController =
//                                StoreAddressController(applicationContext, mycommonview!!)
//
//                            controller!!.storeAddress(
//                                locationName,
//                                curlatitude.toString(),
//                                curlongitude.toString(),
//                                callId ?: "",
//                                apiTime
//                            )
//
//                        }

                    }

                }else{
                    mHandler.removeCallbacksAndMessages(null)
                }

                // Handle location update
            }
        }
        startLocationUpdates()
    }

    companion object {


        private const val TAG = "LocationService"
        private const val NOTIFICATION_ID = 123
    }

    @SuppressLint("HandlerLeak")
    internal inner class GeoCodeHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address").toString()
                }
                /*else -> null.toString()*/
                else -> "Gujarat"
                /*else -> "Searching Location"*/
            }

            Utils.showLog(TAG, "---- address---" + locationAddress)
            locationName = locationAddress
        }
    }

    val mycommonview: ICommanView? = object : ICommanView {
        override fun onComman(comman: CommanResponse?) {
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG, "Service onBind");

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "Service onUnbind");

        return super.onUnbind(intent)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


}

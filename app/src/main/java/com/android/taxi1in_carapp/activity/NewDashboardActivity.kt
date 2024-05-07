package com.android.taxi1in_carapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetDefaultNumberResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.StartCallResponse
import com.android.taxi1in_carapp.activity.Controller.CallStartController
import com.android.taxi1in_carapp.activity.Controller.CallStopController
import com.android.taxi1in_carapp.activity.Controller.GetDefaultNumberController
import com.android.taxi1in_carapp.activity.Controller.ICallStartController
import com.android.taxi1in_carapp.activity.Controller.ICallStopController
import com.android.taxi1in_carapp.activity.Controller.IGetDefaultController
import com.android.taxi1in_carapp.activity.Controller.IStoreAddressController
import com.android.taxi1in_carapp.activity.Controller.StoreAddressController
import com.android.taxi1in_carapp.activity.ui.account.AccountNewFragment
import com.android.taxi1in_carapp.activity.ui.history.NewHistoryFragment
import com.android.taxi1in_carapp.activity.ui.home.HomeNewFragment
import com.android.taxi1in_carapp.activity.ui.meter.NewMeterFragment
import com.android.taxi1in_carapp.databinding.ActivityDashboardNewBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.taxi1.Twillio.SoundPoolManager
import com.taxi1.ViewResponse.ICallStartView
import com.taxi1.ViewResponse.ICommanView
import com.taxi1.ViewResponse.IDefaultNumberView
import com.taxi1.broadcast.LocationProviderChangedReceiver
import com.taxi1.utils.*
import com.twilio.voice.Call
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Time


class NewDashboardActivity : BaseActivity(), IDefaultNumberView, ICallStartView, ICommanView {
    private val TAG = this@NewDashboardActivity.javaClass.simpleName
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityDashboardNewBinding
    lateinit var storedata: StoreUserData
    private var locationManager: LocationManager? = null
    private var myLocationListener: LocationListener? = null
    var defaultNumberController: IGetDefaultController? = null
    var storeAddressController: IStoreAddressController? = null
    private lateinit var tripTimer: Runnable
    private var contactNumber: String? = ""
    private var activeCall: Call? = null
    var isCallStart: Boolean = false
    var callId: String? = null
    var callStartController: ICallStartController? = null
    var callStopController: ICallStopController? = null


    lateinit var mainHandler: Handler
    private var isHandlerRunning = false
    private var locationName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         *         gps location on off listener
         */
        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        mainHandler = Handler(Looper.getMainLooper())

        storedata = StoreUserData(this)
        val meterPermission = storedata.getString(Constants.METER_PERMISSION)
        Log.d("TAG", "meterPermission: ===" + meterPermission)


        storeAddressController = StoreAddressController(this, this)
        defaultNumberController = GetDefaultNumberController(this, this)
        defaultNumberController?.getDefaultNumber()

        setHandler()
        initClick()
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this@NewDashboardActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this@NewDashboardActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@NewDashboardActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2)
        }else{
            checkGPS()
        }
    }

    private fun setHandler() {
        //send current location address
        tripTimer = object : Runnable {
            override fun run() {
                isHandlerRunning = true

                if(isCallStart) {
                    if (true) {
                        var locations: Location?
                        Log.e("location", "5 second handler")
                        if ((ContextCompat.checkSelfPermission(
                                this@NewDashboardActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED)
                        ) {
                            locations = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            Log.e("location", "last location gps ${locations != null}")
                            if (locations == null) {
                                locations = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                Log.e("location", "last location network ${locations != null}")
                            }
                            if (locations != null) {
                                val curTime = Time(System.currentTimeMillis())
                                Log.e(TAG, "onLocationChanged invoked : current time ${curTime.minutes} : ${curTime.seconds}")
//                    Toast.makeText(requireContext(), "onLocationChanged invoked", Toast.LENGTH_SHORT).show()

                                val curlatitude = locations.latitude
                                val curlongitude = locations.longitude

                                Log.e(TAG, "Lat : $curlatitude  Long : $curlongitude")
                                val locationAddress = LocationAddress()
                                locationAddress.getAddressFromLocation(
                                    curlatitude,
                                    curlongitude,
                                    this@NewDashboardActivity,
                                    GeoCodeHandler()
                                )

                                Log.e(TAG, "Location update $locationName and call id :- $callId")
                                //call api to send location details


                                storeAddressController!!.storeAddress(
                                    locationName,
                                    curlatitude.toString(),
                                    curlongitude.toString(),
                                    callId ?: ""
                                )


                            }
                        }
                    }
                }
                mainHandler.postDelayed(this, 5000)
            }
        }
    }

    private fun initClick() {

        binding.includeBottomBar.btnSOSStart?.setOnLongClickListener {
//            binding.includeBottomBar.btnSOSStart?.visibility = GONE
//            binding.includeBottomBar.btnSOSStop?.visibility = VISIBLE
            makeCall()
            true
        }

        binding.includeBottomBar.btnSOSStop?.setOnClickListener {
            SoundPoolManager(this).playDisconnect()
            resetUI()
            disconnect()
//            binding.includeBottomBar.btnSOSStart?.visibility = VISIBLE
//            binding.includeBottomBar.btnSOSStop?.visibility = GONE
        }

        binding.includeBottomBar.rlHome.setOnClickListener {
            val fragmentInstance = supportFragmentManager.findFragmentById(R.id.flContainer)
            if (fragmentInstance !is HomeNewFragment) {
                setFragment(HomeNewFragment(), "HomeNewFragment", Bundle(), false)
            }

        }

        binding.includeBottomBar.rlAccount.setOnClickListener {
            val fragmentInstance = supportFragmentManager.findFragmentById(R.id.flContainer)
            if (fragmentInstance !is AccountNewFragment) {
                setFragment(AccountNewFragment(), "AccountNewFragment", Bundle(), false)
            }

        }

        binding.includeBottomBar.rlMeter.setOnClickListener {
            val fragmentInstance = supportFragmentManager.findFragmentById(R.id.flContainer)
            if (fragmentInstance !is NewMeterFragment) {
                setFragment(NewMeterFragment(), "MeterFragment", Bundle(), false)
            }

        }

        binding.includeBottomBar.rlHistory.setOnClickListener {
            val fragmentInstance = supportFragmentManager.findFragmentById(R.id.flContainer)
            if (fragmentInstance !is NewHistoryFragment) {
                setFragment(NewHistoryFragment(), "NewHistoryFragment", Bundle(), false)
            }
        }

        binding.includeBottomBar.rlHelp.setOnClickListener {

            binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(this, R.color.light_green1))
            binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))

            binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
        }

        binding.includeBottomBar.rlTraining.setOnClickListener {

            binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(this, R.color.light_green1))

            binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(this, R.color.gray_new2))
            binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLocation() {
        Log.e(TAG, "checkLocation called!")
        if (!isGPSEnabled(this)) {
            checkGPSEnabled()
            Log.e(TAG, "GPS provider is not enabled ")
        } else {

            try {
                val serviceString = Context.LOCATION_SERVICE
                locationManager = this.getSystemService(serviceString) as LocationManager?

                var location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if(location == null){
                    location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    Log.e("location", "last location start network ${location != null}")
                }

                Log.e(TAG, "last location" + location)
                if (location != null) {
                    //latitude = location.getLatitude()
                    //longitude = location.getLongitude()
                    val curlatitude = location.getLatitude()
                    val curlongitude = location.getLongitude()
                    Log.e(TAG, "Lat1 : $curlatitude  Long 1: $curlongitude")
                }

                mainHandler.removeCallbacks(tripTimer)
                mainHandler.post(tripTimer)

                Log.d(TAG, "checkLocation current provider name: ${location?.provider} ")

            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    fun isGPSEnabled(mContext: Context?): Boolean {
        val locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)

//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkGPSEnabled() {
        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER).not()) {
            turnOnGPS()
        }
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun turnOnGPS() {
        val request = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(this, 12345)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }.addOnSuccessListener {
            //here GPS is On
            checkLocation()
        }
    }

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

    private fun resetUI() {
        binding.includeBottomBar.btnSOSStart?.visibility = VISIBLE
        binding.includeBottomBar.btnSOSStop?.visibility = GONE
    }

    private fun disconnect() {
        if (activeCall != null) {
            activeCall!!.disconnect()
            activeCall = null
        }

        callStopController = CallStopController(this, this)
        callStopController!!.stopCall(callId.toString())

        mainHandler.removeCallbacks(tripTimer)
        isHandlerRunning = false
        isCallStart = false

        myLocationListener?.let {
            locationManager!!.removeUpdates(myLocationListener!!)
        }
    }

    fun setFragment(
        fragment: Fragment,
        tag: String,
        bundle: Bundle,
        addToBackstack:Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if(addToBackstack){
            transaction.addToBackStack(tag)
        }

        Log.e("setFragment1=", "setFragment")
        Log.e("bundledata1=", bundle.toString())

        transaction.replace(R.id.flContainer, fragment).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun makeCall() {
        takepermissionNew()
        setCallUI()

        // Make sure the Skype for Android client is installed.
        if (!isSkypeClientInstalled(this@NewDashboardActivity)) {
            goToMarket(this@NewDashboardActivity)
            return
        }

        callStartController = CallStartController(this@NewDashboardActivity, this)
        callStartController!!.callStart(contactNumber!!)

        // Create the Intent from our Skype URI.
        val skypeUri = Uri.parse("skype:live:.cid.dd209f734ce4f914")
        val myIntent = Intent(Intent.ACTION_VIEW, skypeUri)

        // Restrict the Intent to being handled by the Skype for Android client only.
        myIntent.component = ComponentName("com.skype.raider", "com.skype4life.MainActivity")
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Initiate the Intent. It should never fail because you've already established the
        // presence of its handler (although there is an extremely minute window where that
        // handler can go away).

        startActivity(myIntent)

    }

    private fun setCallUI() {
        binding.includeBottomBar.btnSOSStart?.visibility = GONE
        binding.includeBottomBar.btnSOSStop?.visibility = VISIBLE
    }

    fun isSkypeClientInstalled(myContext: Context): Boolean {
        val myPackageMgr = myContext.packageManager
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "isSkypeClientInstalled1: ${e.message}")
            return false
        }
        return true
    }

    fun goToMarket(myContext: Context) {
        val marketUri = Uri.parse("market://details?id=com.skype.raider")
        val myIntent = Intent(Intent.ACTION_VIEW, marketUri)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myContext.startActivity(myIntent)
    }

    private fun takepermissionNew() {

        val permissionList = arrayListOf<String>()
        permissionList.add(Manifest.permission.INTERNET)
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        Utils.showLog(TAG, "===take permission====")

        //Dexter.withContext(requireContext()).withPermissions(permissionList).withListener(this).check()

        Dexter.withContext(this)
            .withPermissions(permissionList).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Utils.showLog(TAG, "===permission checked====")
                        //getUserLocation()
                        checkLocation()
                        return
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        Utils.showLog(TAG, "===permission denied====")
                        showSettingsDialog()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken,
                ) {
                    Utils.showLog(TAG, "===permission continue====")

                    //open code if not working
                    /*  permissionToken.continuePermissionRequest()
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                          showSettingsDialog()
                      }*/
                    showSettingsDialog()
                }
            })
            .onSameThread().check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@NewDashboardActivity)
        builder.setTitle("Need Location Permissions")
        builder.setMessage("This app needs location permission to use this application. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }


    private fun checkGPS() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(lm)) {
            Utility.showDialog(
                this,
                "Location",
                "GPS is not enabled in device, it's must be enabled to connect with device.",
                "Enable",
                "",
                object : DialogView.ButtonListener {
                    override fun onNegativeButtonClick(dialog: AlertDialog) {
                        dialog.dismiss()
                    }

                    override fun onPositiveButtonClick(dialog: AlertDialog) {
                        dialog.dismiss()
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                checkGPS()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResponseDefaultNumber(response: List<GetDefaultNumberResponse.Result?>?) {
        Utils.showLog(TAG, "==default number===" + response!![0]?.number)
        contactNumber = response[0]?.number

        setFragment(HomeNewFragment(), "HomeNewFragment", Bundle(), false)
    }

    override fun onCallStart(data: StartCallResponse.Result?) {
        if (data != null)
            callId = data.callId.toString()
        Utils.showLog(TAG, "====call id====" + callId)
        isCallStart = true
    }

    override fun onComman(comman: CommonResponse?) {
        Utils.showLog(TAG, comman?.message)
    }
}
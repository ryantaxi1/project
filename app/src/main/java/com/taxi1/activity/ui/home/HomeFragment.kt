package com.taxi1.activity.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Chronometer
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.taxi1.R
import com.taxi1.Retrofit.Response.AccessTokenResponse
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.DefaultNumberResponse
import com.taxi1.Retrofit.Response.StartCallResponse
import com.taxi1.Twillio.Constants
import com.taxi1.Twillio.Constants.YOUR_TWIML_URL
import com.taxi1.Twillio.IncomingCallNotificationService
import com.taxi1.Twillio.SoundPoolManager
import com.taxi1.ViewResponse.IAccessTokenView
import com.taxi1.ViewResponse.ICallStartView
import com.taxi1.ViewResponse.ICommanView
import com.taxi1.ViewResponse.IDefaultNumberView
import com.taxi1.ViewResponse.ISendGpsLocationView
import com.taxi1.activity.Controller.*
import com.taxi1.broadcast.LocationService
import com.taxi1.databinding.FragmentHomeBinding
import com.taxi1.utils.LocationAddress
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.voice.*
import com.twilio.voice.Call.CallQualityWarning
import java.sql.Time
import java.util.*
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.tan


class HomeFragment : Fragment(), IAccessTokenView, ICallStartView, ICommanView, IDefaultNumberView, ISendGpsLocationView,
    Application.ActivityLifecycleCallbacks {

    private var contactNumber: String? = ""
    private var locationName: String = ""
    val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    private val MIC_PERMISSION_REQUEST_CODE = 1
    private val PERMISSIONS_REQUEST_CODE = 100
    private var foregroundService: LocationService? = null
    private var isServiceBound = false
    lateinit var serviceIntent: Intent

//    var mFusedLocationClient: FusedLocationProviderClient? = null

    var PERMISSION_ID = 44

    //private String accessToken = "PASTE_YOUR_ACCESS_TOKEN_HERE";
    //private val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzhlNzU3M2ZkYmM1NzQxZjhkZDIwZDcxODhlYTFhYzlmLTE2NTI2ODc5NDUiLCJpc3MiOiJTSzhlNzU3M2ZkYmM1NzQxZjhkZDIwZDcxODhlYTFhYzlmIiwic3ViIjoiQUM3NjRjMDUwODY2Njk3YTdmOTAxY2U5ODk3NWQ3MGE3MiIsImV4cCI6MTY1MjY5MTU0NSwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiYWxpY2UiLCJ2b2ljZSI6eyJvdXRnb2luZyI6eyJhcHBsaWNhdGlvbl9zaWQiOiJBUGJjYmUzMDU2YjI1NTRjZjUwNzc4YTE2MzlmZTQ2OWZmIn0sInB1c2hfY3JlZGVudGlhbF9zaWQiOiJDUjc1MzI2OWM5YTZlZDczMzdhMjliZDRhMWQ3MTI0MWQ2In19fQ.HGE21bH6FFODhBOio7XZHJgRv6euWZCthIyk4YFUu3w"

    var accessToken: String? = null
    var callId: String? = null

    var isCallStart: Boolean = false

    /*
     * Audio device management
     */
    private var audioSwitch: AudioSwitch? = null
    private var savedVolumeControlStream = 0
    private var audioDeviceMenuItem: MenuItem? = null

    private var isReceiverRegistered = false
    private var voiceBroadcastReceiver: VoiceBroadcastReceiver? =
        null

    // Empty HashMap, never populated for the Quickstart
    var params = HashMap<String, String>()

    private var relative: RelativeLayout? = null
    private var holdActionFab: FloatingActionButton? = null
    private var muteActionFab: FloatingActionButton? = null
    private var chronometer: Chronometer? = null

    private var notificationManager: NotificationManager? = null
    private var alertDialog: AlertDialog? = null
    private var activeCallInvite: CallInvite? = null
    private var activeCall: Call? = null
    private var activeCallNotificationId = 0

    var registrationListener: RegistrationListener = registrationListener()!!
    var callListener: Call.Listener = callListener()!!

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var accessTokenController: IAccessTokenController? = null
    var callStartController: ICallStartController? = null
    var callStopController: ICallStopController? = null
    var controller: IStoreAddressController? = null
    var gpsLocController: ISendGPSAddressController? = null
    var defaultNumberController: IGetDefaultController? = null

    //    lateinit var mainHandler: Handler
//    private var isHandlerRunning = false
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    var lastLocation: Location? = null
    /**
     * That is for get every 1 minute location
     */
    private var fusedLocProviderClient2: FusedLocationProviderClient? = null
    private var previousLocation: Location? = null
    private lateinit var locationRequest: LocationRequest

    //  private var minDistance = Float.MAX_VALUE
    private var minDistance = 0.1F
    var serviceintent: Intent? = null

//    val tripTimer = object : Runnable {
//        override fun run() {
//            isHandlerRunning = true
//            val storedata = StoreUserData(requireActivity())
//            val isCallStart = storedata.getBoolean("isCallStart")
//            Log.e(TAG, "isCallStart==" + isCallStart.toString())
//
//            if (isCallStart) {
//                mainHandler.postDelayed(this, 5000)
//
//                Log.e("location", "5 second handler")
////                if (ContextCompat.checkSelfPermission(
////                        requireContext(),
////                        Manifest.permission.ACCESS_FINE_LOCATION
////                    ) == PackageManager.PERMISSION_GRANTED
////                )
////                {
////
////                    if (fusedLocationClient != null)
////                        fusedLocationClient!!.requestLocationUpdates(
////                            createLocationRequest(),
////                            locationCallback,
////                            Looper.getMainLooper()
////                        )
////
////                }
//                //                if ((ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)
////                            == PackageManager.PERMISSION_GRANTED))
////                {
//
//
//                //}
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mainHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // startLocationUpdates()

        return root
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = StoreAddressController(requireContext(), this)
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // These flags ensure that the activity can be launched when the screen is locked.

        defaultNumberController = GetDefaultNumberController(requireContext(), this)
        defaultNumberController?.getDefaultNumber()

        gpsLocController = SendGpsAddressController(requireContext(), this)
        fusedLocProviderClient2 = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.create().apply {
            interval = 60000 // 1 minute in milliseconds
            fastestInterval = 60000 // 1 minute in milliseconds (if available sooner)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        every1MinuteSendLocation()

        // These flags ensure that the activity can be launched when the screen is locked.
        val window: Window = requireActivity().getWindow()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        holdActionFab = view.findViewById(R.id.hold_action_fab)
        muteActionFab = view.findViewById(R.id.mute_action_fab)
        chronometer = view.findViewById(R.id.chronometer)
        relative = view.findViewById(R.id.relative)


        binding.relStart.setOnLongClickListener(callActionFabClickListener())
        //binding.relStart.setOnClickListener(callActionFabClickListener())
        binding.relStop.setOnClickListener(hangupActionFabClickListener())
        holdActionFab?.setOnClickListener(holdActionFabClickListener())
        muteActionFab?.setOnClickListener(muteActionFabClickListener())

        notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */voiceBroadcastReceiver = VoiceBroadcastReceiver()
        registerReceiver()

        /*
         * Setup the UI
         */

        /*
         * Setup the UI
         */resetUI()

        /*
         * Displays a call dialog if the intent contains a call invite
         */

        /*
         * Displays a call dialog if the intent contains a call invite
         */handleIncomingCallIntent(requireActivity().getIntent())

        /*
         * Ensure required permissions are enabled
         */

        /*
         * Ensure required permissions are enabled
         */


        if (Build.VERSION.SDK_INT > VERSION_CODES.R) {
            if (!hasPermissions(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissionForMicrophoneAndBluetooth()
            } else {

                /*Utils.showPositiveNegativeAlertDialog(requireContext(), "Permission Dialog", "Grant", "Deny","This app collect the location data to activate sos button. Even when the app is closed or not in use."){

                }*/

                takepermissionNew()
                registerForCallInvites()
            }
        } else {
            if (!hasPermissions(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissionForMicrophone()
            } else {
                takepermissionNew()
                registerForCallInvites()
            }
        }

        /*
         * Setup audio device management and set the volume control stream
         */

        /*
         * Setup audio device management and set the volume control stream
         */audioSwitch = AudioSwitch(requireContext())
        savedVolumeControlStream = requireActivity().getVolumeControlStream()
        requireActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL)
    }

    private fun every1MinuteSendLocation() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            fusedLocProviderClient2?.requestLocationUpdates(locationRequest, getCurrentLocCallback, Looper.getMainLooper())
        }

//        handler.post(locationRunnable)
    }

    private val getCurrentLocCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                var direction: String? = null
                lastLocation?.let {
                    direction = getDirection(getBearing(it, location))
                }
                lastLocation = location

                val locationAddress = LocationAddress()
                locationAddress.getAddressFromLocation(
                    location.latitude,
                    location.longitude,
                    requireActivity(),
                    GeoCodeHandler()
                )
                val kmhSpeed = location.speed * 3.6

                /**
                 * Gps tracker every 1 minute location send
                 * that is different from start and stop call location
                 */
//                gpsLocController?.locationAddress(locationName, location.latitude.toString(), location.longitude.toString(), kmhSpeed.toString(), direction ?: "")
            }
        }
    }

    fun getBearing(startLocation: Location, endLocation: Location): Float {
        val startLat = Math.toRadians(startLocation.latitude)
        val startLong = Math.toRadians(startLocation.longitude)
        val endLat = Math.toRadians(endLocation.latitude)
        val endLong = Math.toRadians(endLocation.longitude)

        val dLong = endLong - startLong

        val dPhi = ln(tan(endLat / 2 + PI / 4) / tan(startLat / 2 + PI / 4))
        val direction = atan2(dLong, dPhi)

        return Math.toDegrees(direction).toFloat()
    }

    fun getDirection(bearing: Float): String {
        val directions = arrayOf("North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest", "North")
        var index = ((bearing + 22.5) / 45).toInt()
        if (index < 0) {
            index += 8 // Ensure positive index by adding 8
        }
        return directions[index % 8]
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

        Dexter.withContext(context)
            .withPermissions(permissionList).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Utils.showLog(TAG, "===permission checked====")
                        //getUserLocation()
                        //checkLocation()
                        startLocationUpdates()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unbindService();
        fusedLocProviderClient2?.removeLocationUpdates(getCurrentLocCallback)

    }

    fun isGPSEnabled(mContext: Context?): Boolean {
        val locationManager =
            mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)

//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private var locationManager: LocationManager? = null
    private var myLocationListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    fun checkLocation() {
        Log.e(TAG, "checkLocation called!")
        if (!isGPSEnabled(requireContext())) {
            checkGPSEnabled()
            Log.e(TAG, "GPS provider is not enabled ")
        } else {

            try {
                val serviceString = Context.LOCATION_SERVICE
                locationManager = context?.getSystemService(serviceString) as LocationManager?

                var location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (location == null) {
                    location =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
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


                /*Log.d(TAG, "check handler running : $isHandlerRunning")

                if (!isHandlerRunning){
                    mainHandler.post(tripTimer)
                }*/

//                mainHandler.removeCallbacks(tripTimer)
//                mainHandler.post(tripTimer)

                FusedLocationChanged()

                //val locationAddress = LocationAddress()
                //locationAddress.getAddressFromLocation(curlatitude, curlongitude, context!!, GeoCodeHandler())

                myLocationListener = object : LocationListener {
                    override fun onLocationChanged(locationListener: Location) {
                    }

                    override fun onProviderDisabled(provider: String) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                }
                //locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0f, myLocationListener!!)

                Log.d(TAG, "checkLocation current provider name: ${location?.provider} ")

                /*locationManager!!.requestLocationUpdates(
                    location?.provider ?: LocationManager.GPS_PROVIDER,
                    5000,
                    0f,
                    myLocationListener!!
                )*/

            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    fun getLastLocationHandler() {


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

    private fun checkGPSEnabled() {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER).not()) {
            turnOnGPS()
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Need Location Permissions")
        builder.setMessage("This app needs location permission to use this application. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel")
        { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context?.getPackageName(), null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun turnOnGPS() {
        val request = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(requireActivity(), 12345)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }.addOnSuccessListener {
            //here GPS is On
            checkLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            101 -> {
                checkLocation()
            }
        }
    }

    private fun registrationListener(): RegistrationListener {
        return object : RegistrationListener {
            override fun onRegistered(accessToken: String, fcmToken: String) {
                Log.d(
                    TAG,
                    "Successfully registered FCM $fcmToken"
                )
            }

            override fun onError(
                error: RegistrationException,
                accessToken: String,
                fcmToken: String,
            ) {
                val message = String.format(
                    Locale.US,
                    "Registration Error: %d, %s",
                    error.errorCode,
                    error.message
                )
                Log.e(TAG, message)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                //Snackbar.make(relative!!, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun callListener(): Call.Listener {
        return object : Call.Listener {
            /*
             * This callback is emitted once before the Call.Listener.onConnected() callback when
             * the callee is being alerted of a Call. The behavior of this callback is determined by
             * the answerOnBridge flag provided in the Dial verb of your TwiML application
             * associated with this client. If the answerOnBridge flag is false, which is the
             * default, the Call.Listener.onConnected() callback will be emitted immediately after
             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
             * call to emit the onConnected callback only after the call is answered.
             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
             * twiML response contains a Say verb, then the call will emit the
             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
             * raised, irrespective of the value of answerOnBridge being set to true or false
             */
            override fun onRinging(call: Call) {
                Log.d(TAG, "Call Ringing")
                /*
                 * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
                 * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
                 * the call is ringing and awaiting to be accepted on the callee's side. The application
                 * can use the `SoundPoolManager` to play custom audio files between the
                 * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
                 */
                /*if (BuildConfig.playCustomRingback) {
                    SoundPoolManager.getInstance(requireActivity()).playRinging()
                }*/
                if (false) {
                    SoundPoolManager(requireContext()).playRinging()
                }
            }

            override fun onConnectFailure(call: Call, error: CallException) {
                audioSwitch!!.deactivate()
                /*  if (BuildConfig.playCustomRingback) {
                      SoundPoolManager.getInstance(this@VoiceActivity).stopRinging()
                  }*/
                if (false) {
                    SoundPoolManager(requireContext()).stopRinging()
                }
                Log.d(TAG, "Call Connect failure")
                val message = String.format(
                    Locale.US,
                    "Call Error: %d, %s",
                    error.errorCode,
                    error.message
                )
                Log.e(TAG, message)
                //Snackbar.make(relative!!, message, Snackbar.LENGTH_LONG).show()
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                resetUI()
            }

            override fun onConnected(call: Call) {
                audioSwitch!!.activate()
                /* if (BuildConfig.playCustomRingback) {
                     SoundPoolManager.getInstance(this@VoiceActivity).stopRinging()
                 }*/
                if (false) {
                    SoundPoolManager(requireContext()).stopRinging()
                }
                Log.d(TAG, "Call Connected")
                activeCall = call

                enableMicrophoneAndSpeaker()
                requestAudioFocus()
            }

            override fun onReconnecting(call: Call, callException: CallException) {
                Log.d(TAG, "Call onReconnecting")
            }

            override fun onReconnected(call: Call) {
                Log.d(TAG, "Call onReconnected")
            }

            override fun onDisconnected(call: Call, error: CallException?) {
                audioSwitch!!.deactivate()

                releaseAudioResources()

                /* if (BuildConfig.playCustomRingback) {
                     SoundPoolManager.getInstance(this@VoiceActivity).stopRinging()
                 }*/
                if (false) {
                    SoundPoolManager(requireContext()).stopRinging()
                }
                Log.d(TAG, "Call Disconnected")
                Toast.makeText(requireContext(), "Call Disconnected", Toast.LENGTH_LONG).show()
                if (error != null) {
                    val message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.errorCode,
                        error.message
                    )
                    Log.e(TAG, message)
                    // Snackbar.make(relative!!, message, Snackbar.LENGTH_LONG).show()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
//                resetUI()
                binding.relStop.performClick()
            }

            /*
             * currentWarnings: existing quality warnings that have not been cleared yet
             * previousWarnings: last set of warnings prior to receiving this callback
             *
             * Example:
             *   - currentWarnings: { A, B }
             *   - previousWarnings: { B, C }
             *
             * Newly raised warnings = currentWarnings - intersection = { A }
             * Newly cleared warnings = previousWarnings - intersection = { C }
             */
            override fun onCallQualityWarningsChanged(
                call: Call,
                currentWarnings: MutableSet<CallQualityWarning>,
                previousWarnings: MutableSet<CallQualityWarning>,
            ) {
//                if (previousWarnings.size > 1) {
//                    val intersection: MutableSet<CallQualityWarning> = HashSet(currentWarnings)
//                    currentWarnings.removeAll(previousWarnings)
//                    intersection.retainAll(previousWarnings)
//                    previousWarnings.removeAll(intersection)
//                }
                val message = String.format(
                    Locale.US,
                    "Newly raised warnings: $currentWarnings Clear warnings $previousWarnings"
                )
                Log.e(TAG, "Call $message")
                // Snackbar.make(relative!!, message, Snackbar.LENGTH_LONG).show()
//                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
//                binding.relStart.visibility = View.VISIBLE
//                binding.relStop.visibility = View.GONE
//                holdActionFab!!.hide()
//                muteActionFab!!.hide()
//                chronometer!!.visibility = View.INVISIBLE
//                chronometer!!.stop()

//                callStopApi()

//                mainHandler.removeCallbacks(tripTimer)
//                isHandlerRunning = false

//                locationManager!!.removeUpdates(myLocationListener!!)
            }
        }
    }

    private fun enableMicrophoneAndSpeaker() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true // Enable speaker
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION // Set audio mode
    }

    private fun requestAudioFocus() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            null,
            AudioManager.STREAM_VOICE_CALL,
            AudioManager.AUDIOFOCUS_GAIN
        )

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Failed to gain audio focus
        }
    }

    private fun releaseAudioResources() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.abandonAudioFocus(null)
    }

    private fun callStopApi() {

        //call api to save end data on server
        callStopController = CallStopController(requireContext(), this)
        callStopController!!.stopCall(callId.toString())

        isCallStart = false

        Log.e(TAG, "callStopApi=" + isCallStart)

    }

    /*
     * The UI state when there is an active call
     */
    private fun setCallUI() {

        binding.relStart.visibility = View.GONE
        binding.relStop.visibility = View.VISIBLE
        Log.d(TAG, "setCallUI:=stop==")

        /*holdActionFab!!.show()
        muteActionFab!!.show()
        chronometer!!.visibility = View.VISIBLE
        chronometer!!.base = SystemClock.elapsedRealtime()
        chronometer!!.start()*/
    }

    /*
     * Reset UI elements
     */
    private fun resetUI() {
        binding.relStart.visibility = View.VISIBLE
        /*muteActionFab!!.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_mic_white_24dp
            )
        )
        holdActionFab!!.hide()
        holdActionFab!!.backgroundTintList = ColorStateList
            .valueOf(ContextCompat.getColor(requireContext(), R.color.theme_color))
        muteActionFab!!.hide()*/
        binding.relStop.visibility = View.GONE
        /*chronometer!!.visibility = View.INVISIBLE
        chronometer!!.stop()*/
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
        startAudioSwitch()
        startLocationUpdates()

        //takepermissionNew()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
        //stopLocationUpdates()

    }

    override fun onDestroy() {
        /*
         * Tear down audio device management and restore previous volume stream
         */
        audioSwitch!!.stop()
        requireActivity().setVolumeControlStream(savedVolumeControlStream)
        //SoundPoolManager.getInstance(this).release()
        SoundPoolManager(requireContext()).release()
        super.onDestroy()
    }

    fun handleIncomingCallIntent(intent: Intent?) {
        if (intent != null && intent.action != null) {
            val action = intent.action
            activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE)
            activeCallNotificationId =
                intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0)
            when (action) {
                Constants.ACTION_INCOMING_CALL -> handleIncomingCall()
                Constants.ACTION_INCOMING_CALL_NOTIFICATION -> showIncomingCallDialog()
                Constants.ACTION_CANCEL_CALL -> handleCancel()
                Constants.ACTION_FCM_TOKEN -> registerForCallInvites()
                Constants.ACTION_ACCEPT -> answer()
                else -> {}
            }
        }
    }

    private fun handleIncomingCall() {
        if (Build.VERSION.SDK_INT < VERSION_CODES.O) {
            showIncomingCallDialog()
        } else {
            if (isAppVisible()) {
                showIncomingCallDialog()
            }
        }
    }

    private fun handleCancel() {
        if (alertDialog != null && alertDialog!!.isShowing()) {
            //SoundPoolManager.getInstance(this).stopRinging()
            SoundPoolManager(requireContext()).stopRinging()
            alertDialog!!.cancel()
        }
    }

    private fun registerReceiver() {
        if (!isReceiverRegistered) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(Constants.ACTION_INCOMING_CALL)
            intentFilter.addAction(Constants.ACTION_CANCEL_CALL)
            intentFilter.addAction(Constants.ACTION_FCM_TOKEN)
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                voiceBroadcastReceiver!!, intentFilter
            )
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(voiceBroadcastReceiver!!)
            isReceiverRegistered = false
        }
    }

    inner class VoiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && (action == Constants.ACTION_INCOMING_CALL || action == Constants.ACTION_CANCEL_CALL)) {
                /*
                 * Handle the incoming or cancelled call invite
                 */
                handleIncomingCallIntent(intent)
            }
        }
    }

    private fun answerCallClickListener(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
            Log.d(TAG, "Clicked accept")
            val acceptIntent =
                Intent(requireContext(), IncomingCallNotificationService::class.java)
            acceptIntent.action = Constants.ACTION_ACCEPT
            acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite)
            acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId)
            Log.d(
                TAG,
                "Clicked accept startService"
            )
            requireContext().startService(acceptIntent)
        }
    }

    private fun callClickListener(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
            // Place a call
            val contact = (dialog as AlertDialog).findViewById<EditText>(R.id.contact)
            params["To"] = "+916356192632"
            params["Url"] = YOUR_TWIML_URL
            val connectOptions = ConnectOptions.Builder(accessToken.toString())
                .params(params)
                .build()
            activeCall = Voice.connect(requireContext(), connectOptions, callListener)
            setCallUI()
            alertDialog!!.dismiss()

            //call api to save start data on server
            callStartController = CallStartController(requireContext(), this)
            callStartController!!.callStart(contact.text.toString())

            takepermissionNew()
        }
    }

    private fun newCancelCallClickListener(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
            if (alertDialog != null && alertDialog!!.isShowing()) {
                alertDialog!!.dismiss()
            }
//            binding.relStop.performClick()

        }
    }

    private fun cancelCallClickListener(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
            //SoundPoolManager.getInstance(this@VoiceActivity).stopRinging()
            SoundPoolManager(requireContext()).stopRinging()
            if (activeCallInvite != null) {
                val intent =
                    Intent(requireContext(), IncomingCallNotificationService::class.java)
                intent.action = Constants.ACTION_REJECT
                intent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite)
                requireContext().startService(intent)
            }
            if (alertDialog != null && alertDialog!!.isShowing()) {
                alertDialog!!.dismiss()
            }

            //call api to save end data on server
            callStopController = CallStopController(requireContext(), this)
            callStopController!!.stopCall(callId.toString())

//            mainHandler.removeCallbacks(tripTimer)
//            isHandlerRunning = false

            locationManager!!.removeUpdates(myLocationListener!!)

        }
    }

    fun createIncomingCallDialog(
        context: Context?,
        callInvite: CallInvite,
        answerCallClickListener: DialogInterface.OnClickListener?,
        cancelClickListener: DialogInterface.OnClickListener?,
    ): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp)
        alertDialogBuilder.setTitle("Incoming Call")
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener)
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener)
        alertDialogBuilder.setMessage(callInvite.from + " is calling with " + callInvite.callerInfo.isVerified + " status")
        return alertDialogBuilder.create()
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     */
    private fun registerForCallInvites() {
        /*  FirebaseInstanceId.getInstance().getInstanceId()
              .addOnSuccessListener(this) { instanceIdResult ->
                  val fcmToken: String = instanceIdResult.getToken()
                  Log.i(com.twilio.voice.quickstart.VoiceActivity.TAG, "Registering with FCM")
                  Voice.register(accessToken,
                      Voice.RegistrationChannel.FCM,
                      fcmToken,
                      registrationListener)
              }*/
    }


    private fun callActionFabClickListener(): View.OnLongClickListener {

        return View.OnLongClickListener { v: View? ->

            //call api to get access token
            accessTokenController = AccessTokenController(requireContext(), this)
            accessTokenController!!.getAccessToken()
            true
        }
    }

    /* private fun callActionFabClickListener(): View.OnClickListener {
         return View.OnClickListener { v: View? ->

             //call api to get access token
             accessTokenController = AccessTokenController(requireContext(), this)
             accessTokenController!!.getAccessToken()
         }
     }*/

    @RequiresApi(VERSION_CODES.O)
    private fun hangupActionFabClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            Log.e(TAG, "hangupActionFabClickListener");
            stopLocationUpdates()

//            stopForegroundService()


            //SoundPoolManager.getInstance(this@VoiceActivity).playDisconnect()
            SoundPoolManager(requireContext()).playDisconnect()
            resetUI()
            disconnect()

//            if (foregroundService!=null&&isServiceBound){
//                foregroundService!!.stopLocationUpdates()
//            }

            try {
                requireContext().stopService(serviceintent)
            }catch (e: Exception){
                e.printStackTrace()
            }

            unbindService()


//            mainHandler.removeCallbacks(tripTimer)
//            isHandlerRunning = false


        }
    }

    private fun holdActionFabClickListener(): View.OnClickListener? {
        return View.OnClickListener { v: View? -> hold() }
    }

    private fun muteActionFabClickListener(): View.OnClickListener? {
        return View.OnClickListener { v: View? -> mute() }
    }

    /*
     * Accept an incoming Call
     */
    private fun answer() {
        //SoundPoolManager.getInstance(this).stopRinging()
        SoundPoolManager(requireContext()).stopRinging()
        activeCallInvite!!.accept(requireContext(), callListener)
        notificationManager!!.cancel(activeCallNotificationId)
        requireActivity().stopService(
            Intent(
                requireContext(),
                IncomingCallNotificationService::class.java
            )
        )
        setCallUI()
        if (alertDialog != null && alertDialog!!.isShowing()) {
            alertDialog!!.dismiss()
        }
    }

    /*
     * Disconnect from Call
     */
    private fun disconnect() {

        if (activeCall != null) {
            activeCall!!.disconnect()
            activeCall = null
        }

        callStopController = CallStopController(requireContext(), this)
        callStopController!!.stopCall(callId.toString())

//        mainHandler.removeCallbacks(tripTimer)
//        isHandlerRunning = false
//
//        if (mainHandler != null) {
//            mainHandler.removeCallbacksAndMessages(null);
//        }

        myLocationListener?.let {
            locationManager!!.removeUpdates(myLocationListener!!)
        }
    }

    private fun hold() {
        if (activeCall != null) {
            val hold: Boolean = !activeCall!!.isOnHold()
            activeCall!!.hold(hold)
            applyFabState(holdActionFab!!, hold)
        }
    }

    private fun mute() {
        if (activeCall != null) {
            val mute: Boolean = !activeCall!!.isMuted()
            activeCall!!.mute(mute)
            applyFabState(muteActionFab!!, mute)
        }
    }

    private fun applyFabState(button: FloatingActionButton, enabled: Boolean) {
        // Set fab as pressed when call is on hold
        val colorStateList = if (enabled) ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.theme_color
            )
        ) else ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.theme_color
            )
        )
        button.backgroundTintList = colorStateList
    }

    private fun requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            //Snackbar.make(relative!!, "Microphone permissions needed. Please allow in your application settings.", Snackbar.LENGTH_LONG).show()

            Toast.makeText(
                requireContext(),
                "Microphone permissions needed. Please allow in your application settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                MIC_PERMISSION_REQUEST_CODE
            )
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    private fun requestPermissionForMicrophoneAndBluetooth() {
        if (!hasPermissions(
                requireContext(), Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            takepermissionNew()
            registerForCallInvites()
        }
    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        /*
         * Check if required permissions are granted
         */

        if (Build.VERSION.SDK_INT >= VERSION_CODES.S) {

            if (!hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)) {
                // Snackbar.make(relative!!, "Microphone permission needed. Please allow in your application settings.", Snackbar.LENGTH_LONG).show()

                Toast.makeText(
                    requireContext(),
                    "Microphone permissions needed. Please allow in your application settings.",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                if (!hasPermissions(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)) {
                    //Snackbar.make(relative!!, "Without bluetooth permission app will fail to use bluetooth.", Snackbar.LENGTH_LONG).show()
                    Toast.makeText(
                        requireContext(),
                        "Without bluetooth permission app will fail to use bluetooth.",
                        Toast.LENGTH_LONG
                    ).show()

                }
                /*
                 * Due to bluetooth permissions being requested at the same time as mic
                 * permissions, AudioSwitch should be started after providing the user the option
                 * to grant the necessary permissions for bluetooth.
                 */startAudioSwitch()
                registerForCallInvites()
            }
        } else {
            if (!hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)) {
                //Snackbar.make(relative!!, "Microphone permissions needed. Please allow in your application settings.", Snackbar.LENGTH_LONG).show()
                Toast.makeText(
                    requireContext(),
                    "Microphone permissions needed. Please allow in your application settings.",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                startAudioSwitch()
                registerForCallInvites()
            }
        }

//        if (!hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
//        {
//            //Snackbar.make(relative!!, "Microphone permissions needed. Please allow in your application settings.", Snackbar.LENGTH_LONG).show()
//            Toast.makeText(
//                requireContext(),
//                "Location permissions needed. Please allow in your application settings.",
//                Toast.LENGTH_LONG
//            ).show()
//
//        }
//        else {
//            takepermissionNew()
//        }

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            every1MinuteSendLocation()
        } else {
            Toast.makeText(requireContext(), "Permission denied location", Toast.LENGTH_SHORT).show()
        }

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = requireActivity().getMenuInflater()
        inflater.inflate(R.menu.menu, menu)
        audioDeviceMenuItem = menu.findItem(R.id.menu_audio_device)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_audio_device) {
            showAudioDevices()
            return true
        }
        return false
    }

    /*
     * Show the current available audio devices.
     */
    private fun showAudioDevices() {
        val selectedDevice = audioSwitch!!.selectedAudioDevice
        val availableAudioDevices = audioSwitch!!.availableAudioDevices
        if (selectedDevice != null) {
            val selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice)
            val audioDeviceNames = ArrayList<String>()
            for (a in availableAudioDevices) {
                audioDeviceNames.add(a.name)
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Select Audio Device")
                .setSingleChoiceItems(
                    audioDeviceNames.toTypedArray<CharSequence>(),
                    selectedDeviceIndex,
                    DialogInterface.OnClickListener { dialog: DialogInterface, index: Int ->
                        dialog.dismiss()
                        val selectedAudioDevice =
                            availableAudioDevices[index]
                        updateAudioDeviceIcon(selectedAudioDevice)
                        audioSwitch!!.selectDevice(selectedAudioDevice)
                    }).create().show()
        }
    }

    /*
     * Update the menu icon based on the currently selected audio device.
     */
    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        var audioDeviceMenuIcon: Int = R.drawable.ic_phonelink_ring_white_24dp
        if (selectedAudioDevice is AudioDevice.BluetoothHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_bluetooth_white_24dp
        } else if (selectedAudioDevice is AudioDevice.WiredHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_headset_mic_white_24dp
        } else if (selectedAudioDevice is AudioDevice.Earpiece) {
            audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp
        } else if (selectedAudioDevice is AudioDevice.Speakerphone) {
            audioDeviceMenuIcon = R.drawable.ic_volume_up_white_24dp
        }
        if (audioDeviceMenuItem != null) {
            audioDeviceMenuItem!!.setIcon(audioDeviceMenuIcon)
        }
    }

    private fun createCallDialog(
        callClickListener: DialogInterface.OnClickListener,
        cancelClickListener: DialogInterface.OnClickListener,
        activity: Activity,
    ): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp)
        alertDialogBuilder.setTitle("Call")
        alertDialogBuilder.setPositiveButton("Call", callClickListener)
        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener)
        alertDialogBuilder.setCancelable(false)
        val li = LayoutInflater.from(activity)
        val dialogView: View = li.inflate(
            R.layout.dialog_call,
            activity.findViewById(android.R.id.content),
            false
        )
        val contact = dialogView.findViewById<EditText>(R.id.contact)
        contact.setHint(R.string.callee)
        //contact.setText(contactNumber)
        contact.setText("+919377122494")
        alertDialogBuilder.setView(dialogView)
        return alertDialogBuilder.create()
    }

    private fun showIncomingCallDialog() {
        //SoundPoolManager.getInstance(this).playRinging()
        SoundPoolManager(requireContext()).playRinging()
        if (activeCallInvite != null) {
            alertDialog = createIncomingCallDialog(
                requireContext(),
                activeCallInvite!!,
                answerCallClickListener(),
                cancelCallClickListener()
            )
            alertDialog!!.show()
        }
    }

    private fun isAppVisible(): Boolean {
        return ProcessLifecycleOwner
            .get()
            .lifecycle
            .currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }

    private fun startAudioSwitch() {
        /*
         * Start the audio device selector after the menu is created and update the icon when the
         * selected audio device changes.
         */
        audioSwitch!!.start { audioDevices: List<AudioDevice?>?, audioDevice: AudioDevice? ->
            Log.d(TAG, "Updating AudioDeviceIcon")
            updateAudioDeviceIcon(audioDevice)
            Unit
        }
    }

    override fun onResponseToken(data: AccessTokenResponse) {
        accessToken = data.getToken()
        Utils.showLog(TAG, "==access token===" + accessToken)

        if (accessToken!!.isNotEmpty() && accessToken!!.length > 0) {
            //open dialog to add number and call

//            alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), requireActivity())
            alertDialog = createCallDialog(callClickListener(), newCancelCallClickListener(), requireActivity())
            alertDialog!!.show()

            makeCall()
        } else {
            Utils.showToast(requireActivity(), "Something went wrong!")
        }
    }

    /*private fun makeCall() {

        takepermissionNew()

        params["to"] = contactNumber.toString()
        val connectOptions = ConnectOptions.Builder(accessToken.toString())
            .params(params)
            .build()
        activeCall = Voice.connect(requireContext(), connectOptions, callListener)
        setCallUI()

        //call api to save start data on server
        callStartController = CallStartController(requireContext(), this)
        callStartController!!.callStart(contactNumber.toString())

    }*/

    private fun makeCall() {

        takepermissionNew()

//        setCallUI()

//        newTwilioInAppCall()

//        /*    val skype_intent = Intent("android.intent.action.VIEW")
//            skype_intent.setClassName("com.skype.raider", "com.skype4life.MainActivity")
//    //        skype_intent.data = Uri.parse("skype:live:.cid.80209069ddc7811c")
//    //        skype_intent.data = Uri.parse("skype:ryan.lin84")
//            skype_intent.data = Uri.parse("skype:live:.cid.dd209f734ce4f914") // duressalarm@taxi1.com.au
//            startActivity(skype_intent)*/
//
//        // Make sure the Skype for Android client is installed.
//        // Make sure the Skype for Android client is installed.
//        if (!isSkypeClientInstalled(requireActivity())) {
//            goToMarket(requireActivity())
//            return
//        }
//
//        callStartController = CallStartController(requireContext(), this)
//        callStartController!!.callStart(contactNumber!!)
//
//        // Create the Intent from our Skype URI.
//
//        // Create the Intent from our Skype URI.
//        val skypeUri = Uri.parse("skype:live:.cid.dd209f734ce4f914")
//        val myIntent = Intent(Intent.ACTION_VIEW, skypeUri)
//
//        // Restrict the Intent to being handled by the Skype for Android client only.
//
//        // Restrict the Intent to being handled by the Skype for Android client only.
//        myIntent.component = ComponentName("com.skype.raider", "com.skype4life.MainActivity")
//        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        // Initiate the Intent. It should never fail because you've already established the
//        // presence of its handler (although there is an extremely minute window where that
//        // handler can go away).
//
//        // Initiate the Intent. It should never fail because you've already established the
//        // presence of its handler (although there is an extremely minute window where that
//        // handler can go away).
//
//        startActivity(myIntent)

    }

    fun newTwilioInAppCall(){
//        params["From"] = YOUR_TWILIO_PHONE_NUMBER
//        params["To"] = contactNumber!!
//        params["To"] = "+919662168132"
        params["To"] = "+916356192632"
        params["Url"] = YOUR_TWIML_URL
        val connectOptions = ConnectOptions.Builder(accessToken.toString())
            .params(params)
            .build()
        activeCall = Voice.connect(requireContext(), connectOptions, callListener)
        setCallUI()
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

    /*fun isSkypeClientInstalled(myContext: Context): Boolean {
        val myPackageMgr = myContext.packageManager
        return try {
            myPackageMgr.getApplicationInfo("com.skype.raider", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }*/

    fun goToMarket(myContext: Context) {
        val marketUri = Uri.parse("market://details?id=com.skype.raider")
        val myIntent = Intent(Intent.ACTION_VIEW, marketUri)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myContext.startActivity(myIntent)
    }

    override fun onCallStart(data: StartCallResponse.Result?) {
        if (data != null)
            callId = data.callId.toString()

        Utils.showLog(TAG, "====call id====" + callId)

        isCallStart = true

        bindService();

        val storedata = StoreUserData(requireContext())
        storedata.setString("callId", callId!!)
        storedata.setBoolean("isCallStart", isCallStart)

        checkLocation()
        //startLocationService();

    }

    override fun onComman(comman: CommanResponse?) {
        Utils.showLog(TAG, comman?.getMessage())
    }

    override fun onResponse(login: List<DefaultNumberResponse.Result?>?) {
        Utils.showLog(TAG, "==default number===" + login!![0]?.number)

        contactNumber = login[0]?.number
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            if (fusedLocationClient != null && locationCallback != null)
                fusedLocationClient!!.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallback!!,
                    Looper.getMainLooper()
                )

        } else {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
            locationCallback = null
            fusedLocationClient = null
        }

    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000 // Update location every 10 seconds
            fastestInterval = 5000 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    //    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//    }
    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }


    override fun onDetach() {
        super.onDetach()
        stopLocationUpdates()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Utils.showLog(TAG, "==Activity===" + "onActivityCreated")

    }

    override fun onActivityStarted(activity: Activity) {
        Utils.showLog(TAG, "==Activity===" + "onActivityStarted")

    }

    override fun onActivityResumed(activity: Activity) {
        Utils.showLog(TAG, "==Activity===" + "onActivityResumed")


    }

    override fun onActivityPaused(activity: Activity) {
        Utils.showLog(TAG, "==Activity===" + "onActivityPaused")

    }

    override fun onActivityStopped(activity: Activity) {
        Utils.showLog(TAG, "==Activity===" + "onActivityStopped")

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Utils.showLog(TAG, "==Activity===" + "onActivitySaveInstanceState")

    }

    override fun onActivityDestroyed(activity: Activity) {
        Utils.showLog(TAG, "==Activity===" + "onActivityDestroyed")
        //serviceIntent = Intent(requireContext(), LocationService::class.java)

        if (isCallStart) {
            stopLocationUpdates()
            stopForegroundService()
        }

    }

    private fun bindService() {
        val context = requireActivity()
        serviceintent = Intent(context, LocationService::class.java)
        context.bindService(serviceintent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        Log.d("unbindService1", isServiceBound.toString())

        if (isServiceBound) {
            val context = requireActivity()
            context.unbindService(serviceConnection)
            isServiceBound = false

            Log.d("unbindService2", isServiceBound.toString())

        }
    }

    // Method to stop the foreground service
    private fun stopForegroundService() {
        if (isServiceBound && foregroundService != null) {
            foregroundService!!.stopForegroundService()
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: LocationService.LocalBinder =
                service as LocationService.LocalBinder
            foregroundService = binder.service
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
        }
    }

    fun FusedLocationChanged() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val storedata1 = StoreUserData(requireActivity())
                val isCallStart1 = storedata1.getBoolean("isCallStart")
                Log.e(TAG, "isCallStart ::" + isCallStart)

                if (isCallStart1) {
                    Log.e(TAG, "locationResult ::" + locationResult.locations)

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

                        if (previousLocation != null) {
                            val curTime = Time(System.currentTimeMillis())
                            Log.e(
                                TAG,
                                "onLocationChanged invoked : current time ${curTime.minutes} : ${curTime.seconds}"
                            )

                            val curlatitude = previousLocation!!.latitude
                            val curlongitude = previousLocation!!.longitude

                            Log.e(TAG, "Lat : $curlatitude  Long : $curlongitude")


                            val locationAddress = LocationAddress()

                            locationAddress.getAddressFromLocation(
                                curlatitude,
                                curlongitude,
                                requireActivity(),
                                GeoCodeHandler()
                            )

                            Log.e(TAG, "Location update $locationName and call id :- $callId")
                            //call api to send location details


                            /**
                             * That is comment because the service in integrated the location api
                             */
                            /*controller!!.storeAddress(
                                locationName,
                                curlatitude.toString(),
                                curlongitude.toString(),
                                callId ?: ""
                            )*/

                        }

                    }

                }
            }
        }

    }

    override fun onSendGpsLocation(gpsLocationRes: CommanResponse?) {
        if(gpsLocationRes?.getStatus() == 1){
//            Utils.showToast(requireContext(), gpsLocationRes.getMessage())
        }else{
//            Utils.showToast(requireContext(), gpsLocationRes?.getMessage())
        }
    }
}


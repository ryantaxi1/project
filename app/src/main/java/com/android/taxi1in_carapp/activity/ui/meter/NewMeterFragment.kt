package com.android.taxi1in_carapp.activity.ui.meter

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.Response.TripStartContinueStopResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.activity.Controller.ITripStartContinueStopController
import com.android.taxi1in_carapp.activity.Controller.TripStartContinueFinishController
import com.android.taxi1in_carapp.activity.NewDashboardActivity
import com.android.taxi1in_carapp.databinding.FragmentNewMeterScreenBinding
import com.android.taxi1in_carapp.utils.onTouchListener
import com.taxi1.ViewResponse.ITripStartContinueStopView
import com.taxi1.utils.Constants
import com.taxi1.utils.Constants.USER_MODEL
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import com.taxi1.utils.Utils.fromJson
import com.taxi1.utils.Utils.getLocationName
import com.taxi1.utils.Utils.showToast


class NewMeterFragment : Fragment(), ITripStartContinueStopView, LocationListener {
    private var TAG = "NewMeterFragment"
    lateinit var binding : FragmentNewMeterScreenBinding
    private var newDashboardActivity: NewDashboardActivity? = null
    var tripStartContinueStopController: ITripStartContinueStopController? = null
    lateinit var locationManager: LocationManager
    lateinit var tripTimer: Runnable
    private var isActiveTrip: Boolean = false
    var isTimerRunning = false // Update the flag when the task is complete
    var tripID: Int? = null
    lateinit var mainHandler: Handler
    private var userData: GetProfileResponse? = null
    var plateNo : String = ""
    var driverId : String = ""
    lateinit var storedata : StoreUserData
    var carType: String = ""
    var additionalCharge: Double? = null
    private var decimalPlaces: Int = 2
    private var isFormatting = false
    private var startPointLocation : Location? = null
    private var endPointLocation : Location? = null
    private var isCertainDistCross : Boolean = false
    var serverDistance: Int = 90



    private var isCursorVisible = false
//    private val cursorColor = R.color.green // Change this to set the cursor color


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDashboardActivity = activity as NewDashboardActivity?

        storedata = StoreUserData(requireActivity())
        plateNo = storedata.getString(Constants.PLATE_NUMBER)
        driverId = storedata.getString(Constants.DRIVER_ID)

        val userJson = storedata.getString(USER_MODEL)
        userData = userJson.fromJson<GetProfileResponse>()


        if(userData?.result?.distanceType == "km"){
            serverDistance = userData?.result?.distance!! * 1000
        }else{
            serverDistance = userData?.result?.distance!!
        }

        // TODO only testing purpose
        carType = Constants.REGULAR_CAR

        tripStartContinueStopController = TripStartContinueFinishController(requireActivity(), this)

//        tripStartContinueStopController?.getActiveTrip()

        locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        mainHandler = Handler(Looper.getMainLooper())
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0f, this)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        // TODO proper manage to maxi to regular switching
        /*if(myToggle!!.isChecked){
            carType = Constants.MAXI_CAR
        }else{
            carType = Constants.REGULAR_CAR
        }*/


        tripTimer = object : Runnable {
            override fun run() {
                Log.e("location", "1 minute handler")
                isTimerRunning = true

                var locations: Location?
                if ((ContextCompat.checkSelfPermission(newDashboardActivity ?: requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    locations = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Log.e("location", "last location gps ${locations != null}")
                    if(locations == null){
                        locations = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        Log.e("location", "last location network ${locations != null}")
                    }
                    if (locations != null) {
                        if(isActiveTrip){
                            val kmh = locations.speed * 3.6

                            val distCalInMeter = calculateDistanceBetweenLocations(startPointLocation!!.latitude, startPointLocation!!.longitude, endPointLocation!!.latitude, endPointLocation!!.longitude)

                            Log.d(TAG, "onLocationChanged distanceCalcMeter is $distCalInMeter and serverDistance is $serverDistance so ${distCalInMeter > serverDistance}")

                            /**
                             * Temporary comment code
                             */
                            /*if(distCalInMeter > serverDistance && endPointLocation!=null){
                                startPointLocation = Location(endPointLocation!!);
                                isCertainDistCross = true
                                Log.d(TAG, "onLocationChanged last location to new location distance $serverDistance meter: ")
                            }else {
                                isCertainDistCross = false
                                Log.d(TAG, "onLocationChanged not distance to $serverDistance meters: ")
                            }*/

                            tripID?.let {
                                tripStartContinueStopController?.sendTripStartContinueStop(
                                    tripID!!,
                                    carType,
                                    getLocationName(requireActivity(), locations),
                                    locations.latitude,
                                    locations.longitude,
                                    kmh.toInt(),
                                    "continue",
                                    0.0,
                                    Utils.getCurrentTime(),
                                    plateNo,
                                    driverId,
                                    isCertainDistCross
                                )
                            }

                        }
                    }
                }
                mainHandler.postDelayed(this, 15000)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewMeterScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initActivityChanges()
//        binding.etAmount.setInputType(InputType.TYPE_NULL)
        binding.etAmount.showSoftInputOnFocus = false


        binding.tvExtraAmount.text = "$${additionalCharge ?: "00.00"}"

        /*val cursorRunnable: Runnable = object : Runnable {
            override fun run() {
                if (isCursorVisible) {
                    setCursorToTextView()
                } else {
                }
                isCursorVisible = !isCursorVisible
                binding.etAmount.postDelayed(this, 500) // Toggle cursor every 500ms
            }
        }*/

        binding.etAmount.setOnClickListener {
            // Request focus for the TextView when clicked
            binding.etAmount.requestFocus()
        }

        binding.root.setOnClickListener {
            binding.etAmount.clearFocus()
        }

        binding.etAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Start the cursor toggle runnable
//                binding.etAmount.post(cursorRunnable)
                binding.keyboard.root.visibility = VISIBLE
            } else {
                binding.keyboard.root.visibility = GONE
//                removeCursorFromTextView()
            }
        }

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                // Remove the previous listener to prevent recursive calls
                binding.etAmount.removeTextChangedListener(this)

                val input = s.toString().replace(".", "") // Remove any existing decimal points

                val formattedValue = when {
                    input.isEmpty() -> "00.00"
                    input.length <= 5 -> {
                        val paddedInput = input.padStart(5, '0') // Ensure a minimum length of 6
                        val decimalPart = paddedInput.takeLast(2)
                        val integerPart = paddedInput.dropLast(2).takeLast(2)
                        "$integerPart.$decimalPart"
                    }
                    else -> {
                        val validInput = input.takeLast(5) // Take the last 6 characters if longer
                        val decimalPart = validInput.takeLast(2)
                        val integerPart = validInput.dropLast(2).takeLast(2)
                        "$integerPart.$decimalPart"
                    }
                }

                binding.etAmount.setText(formattedValue)
                binding.etAmount.setSelection(formattedValue.length) // Move cursor to the end

                // Set the listener again
                binding.etAmount.addTextChangedListener(this)
            }
        })

        initClick()
    }

    /*private fun setCursorToTextView() {
        val text: String = binding.etAmount.getText().toString()
        val spannableString = SpannableString(text)

        // Set cursor at the end of text
        spannableString.setSpan(BackgroundColorSpan(ContextCompat.getColor(requireContext(),R.color.green)), text.length, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.etAmount.text = spannableString
    }*/

    /*private fun removeCursorFromTextView() {
        val text: String = binding.etAmount.text.toString()
        val spannableString = SpannableString(text)

        // Remove cursor by setting background color span to transparent
        spannableString.setSpan(
            BackgroundColorSpan(Color.TRANSPARENT),
            text.length, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.etAmount.text = spannableString
    }*/

    private fun initActivityChanges() {
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_green1))
        newDashboardActivity?.binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))

        newDashboardActivity?.binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        newDashboardActivity?.binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
    }

    private fun initClick() {
        binding.rlMeter.setOnClickListener {
            binding.svMeter.visibility = VISIBLE
            binding.svTarif.visibility = GONE
            binding.llExtra.visibility = GONE

            binding.rlMeter.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_meter_selection_right_side)
            binding.rlTarif.background = null
            binding.rlExtra.background = null

            binding.tvMeter.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.tvTarif.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvExtra.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

//            setInnerFragment(null, "", Bundle(), true)
        }

        binding.rlTarif.setOnClickListener {
            binding.svMeter.visibility = GONE
            binding.svTarif.visibility = VISIBLE
            binding.llExtra.visibility = GONE

            binding.rlMeter.background = null
            binding.rlTarif.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_meter_selection_right_side)
            binding.rlExtra.background = null

            binding.tvMeter.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvTarif.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.tvExtra.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

//            setInnerFragment(TarifFragment(), "TarifFragment", Bundle(), true)
        }

        binding.rlExtra.setOnClickListener {
            binding.svMeter.visibility = GONE
            binding.svTarif.visibility = GONE
            binding.llExtra.visibility = VISIBLE

            binding.rlMeter.background = null
            binding.rlTarif.background = null
            binding.rlExtra.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_meter_selection_right_side)

            binding.tvMeter.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvTarif.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvExtra.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
        }

        binding.tvRegular.setOnClickListener {
            binding.tvRegular.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tarif_selection)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }

            binding.tvMaxi.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

            binding.ivRegularSelection.visibility = VISIBLE
            binding.ivMaxiSelection.visibility = GONE
        }

        binding.tvMaxi.setOnClickListener {
            binding.tvMaxi.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tarif_selection)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }

            binding.tvRegular.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

            binding.ivRegularSelection.visibility = GONE
            binding.ivMaxiSelection.visibility = VISIBLE
        }

        binding.rlStart.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Handle permissions not granted
                var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(location == null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    Log.e("location", "last location start network ${location != null}")
                }
                Log.e(TAG, "onActiveTrip start location: ${location != null} $location")
                if(location != null){
                    val kmh = location.speed * 3.6

                    /**
                     * * TODO remove location insert local db.
                     */

                    tripStartContinueStopController?.sendTripStartContinueStop(
                        0,
                        carType,
                        getLocationName(requireActivity(), location),
                        location.latitude,
                        location.longitude,
                        kmh.toInt(),
                        "start",
                        0.0,
                        Utils.getCurrentTime(),
                        plateNo,
                        driverId,
                        isCertainDistCross
                    )
                }
            }

        }

        binding.rlPause.setOnClickListener {
            if (isActiveTrip) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Handle permissions not granted
                    var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        Log.e("location", "last location network ${location != null}")
                    }
                    Log.e(TAG, "onActiveTrip location: ${location != null} $location")

                    if (location != null) {
                        val kmh = location!!.speed * 3.6

                        /**
                         * * TODO remove location insert local db.
                         */

                        tripStartContinueStopController?.sendTripStartContinueStop(
                            tripID!!,
                            carType,
                            getLocationName(requireActivity(), location!!),
                            location!!.latitude,
                            location!!.longitude,
                            kmh.toInt(),
                            "pause",
                            0.0,
                            Utils.getCurrentTime(),
                            plateNo,
                            driverId,
                            isCertainDistCross
                        )
                    }
                }
            }else{
                showToast(requireContext(), "Trip is not active")
            }
        }

        binding.rlResume.setOnClickListener {
            binding.rlStart.visibility = GONE
            binding.rlResume.visibility = GONE
            binding.rlStop.visibility = VISIBLE
            binding.rlPause.visibility = VISIBLE
            mainHandler.post(tripTimer)
        }



        binding.rlStop.setOnClickListener {

            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.e("location", "last location end network ${location != null}")
            }

            if(location != null) {
                val kmh = location!!.speed * 3.6

                // TODO Manage the additional trip charge
                if(tripID != null){
                    tripStartContinueStopController?.sendTripStartContinueStop(
                        tripID!!,
                        carType,
                        getLocationName(requireActivity(), location!!),
                        location!!.latitude,
                        location!!.longitude,
                        kmh.toInt(),
                        "finish",
                        additionalCharge ?: 0.0,
                        Utils.getCurrentTime(),
                        plateNo,
                        driverId,
                        isCertainDistCross
                    )
                }

                tripID = null
            }
        }

        binding.rlComplete.setOnClickListener {
            clearAllPreviousTripData()
        }

        onTouchListener(binding.keyboard.tvBtn1) {
            binding.etAmount.append("1")
        }
        onTouchListener(binding.keyboard.tvBtn2) {
            binding.etAmount.append("2")
        }
        onTouchListener(binding.keyboard.tvBtn3) {
            binding.etAmount.append("3")
        }
        onTouchListener(binding.keyboard.tvBtn4) {
            binding.etAmount.append("4")
        }
        onTouchListener(binding.keyboard.tvBtn5) {
            binding.etAmount.append("5")
        }
        onTouchListener(binding.keyboard.tvBtn6) {
            binding.etAmount.append("6")
        }
        onTouchListener(binding.keyboard.tvBtn7) {
            binding.etAmount.append("7")
        }
        onTouchListener(binding.keyboard.tvBtn8) {
            binding.etAmount.append("8")
        }
        onTouchListener(binding.keyboard.tvBtn9) {
            binding.etAmount.append("9")
        }
        onTouchListener(binding.keyboard.tvBtn0) {
            binding.etAmount.append("0")
        }
        onTouchListener(binding.keyboard.ivBackSpace) {
//            binding.etAmount.text.substring(0, binding.etAmount.text.length -1)
            if(!binding.etAmount.text.isNullOrEmpty()){
                val newValue: String = binding.etAmount.text.toString().substring(0, binding.etAmount.text.toString().length - 1)
//                val newValue = binding.etAmount.text.toString().replace(binding.etAmount.text.last().toString(), "",false)
                binding.etAmount.setText(newValue)
            }
        }
        onTouchListener(binding.keyboard.btnAddToTrip) {
            if(!binding.etAmount.text.isNullOrBlank()){
                additionalCharge = binding.etAmount.text.toString().toDouble()
                binding.keyboard.root.visibility = GONE
                binding.rlMeter.performClick()
            }else{
                showToast(requireContext(), "Please enter extra amount")
            }
            binding.tvExtraAmount.text = "$${additionalCharge ?: "00.00"}"
        }
    }

    private fun showAmount(amount: String) {

        binding.viewAmountParent.removeAllViews()
        for (char in amount) {
            val dynamicTextview = TextView(requireContext())

            if(!char.equals('.'))
            {
                dynamicTextview.setBackgroundResource(R.drawable.bg_fare_text)
                val paddingHorizontal = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
                val paddingVertical = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
                dynamicTextview.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            }

            dynamicTextview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp)
            }

            dynamicTextview.setTextColor(resources.getColor(R.color.dark_blue))

            dynamicTextview.setTypeface(null, Typeface.BOLD)

            dynamicTextview.setText("$char")

            val textSize = resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._30ssp).toFloat()
            dynamicTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

            binding.viewAmountParent.addView(dynamicTextview)
        }
    }


    override fun onResponse(tripResponse: TripStartContinueStopResponse) {
        if(tripResponse.status == 1) {

            binding.tvTarifMode.text = tripResponse.mode
            when(tripResponse.mode){
                "Day" -> {
                    binding.tvTarifName.text = "Regular day"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_day_tarif))
                }
                "Night" -> {
                    binding.tvTarifName.text = "Night"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_night_tarif))
                }
                "NightOwl" -> {
                    binding.tvTarifName.text = "Night Owl"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_nightowl_tarif))
                }
            }

            if (tripID == null && tripResponse.tripStatus == 1) {
                isActiveTrip = true
                tripID = tripResponse.tripId
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = VISIBLE

                val timer = object: CountDownTimer(15000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Update UI or perform actions on tick (every second)
                    }

                    override fun onFinish() {
                        // Perform actions when the countdown is finished (after 1 minute)
                        mainHandler.post(tripTimer)
                    }
                }
                timer.start()


            } else if (tripResponse.tripStatus == 3) {
                isActiveTrip = false
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = GONE
                binding.rlComplete.visibility = VISIBLE
                mainHandler.removeCallbacks(tripTimer)
                isTimerRunning = false

            }else if (tripResponse.tripStatus == 4) {
                binding.rlResume.visibility = VISIBLE
                binding.rlPause.visibility = GONE
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = GONE
                binding.rlComplete.visibility = GONE

                mainHandler.removeCallbacks(tripTimer)
                isTimerRunning = false
            }
            val total = tripResponse?.finalCharge?.toDouble()
            val amount = String.format("%.2f", total)
            showAmount(amount)
        }
    }

    override fun getActiveTripResponse(tripResponse: TripStartContinueStopResponse) {
        if(tripResponse.status == 1) {

            binding.tvTarifMode.text = tripResponse.mode
            when(tripResponse.mode){
                "Day" -> {
                    binding.tvTarifName.text = "Regular day"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_day_tarif))
                }
                "Night" -> {
                    binding.tvTarifName.text = "Night"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_night_tarif))
                }
                "NightOwl" -> {
                    binding.tvTarifName.text = "Night Owl"
                    binding.ivTarifMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_nightowl_tarif))
                }
            }

            if (tripID == null && tripResponse.tripStatus == 1) {
                isActiveTrip = true
                tripID = tripResponse.tripId
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = VISIBLE
                mainHandler.post(tripTimer)

            }else if (tripResponse.tripStatus == 2){
                isActiveTrip = true
                tripID = tripResponse.tripId
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = VISIBLE
                if(!isTimerRunning){
                    mainHandler.post(tripTimer)
                }

            } /*else if (tripResponse.tripStatus == 3) {
                isActiveTrip = false
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = GONE
                binding.rlComplete.visibility = VISIBLE
                mainHandler.removeCallbacks(tripTimer)
                isTimerRunning = false

            }*/else if (tripResponse.tripStatus == 4) {
                binding.rlResume.visibility = VISIBLE
                binding.rlPause.visibility = GONE
                binding.rlStart.visibility = GONE
                binding.rlStop.visibility = GONE
                binding.rlComplete.visibility = GONE

                mainHandler.removeCallbacks(tripTimer)
                isTimerRunning = false
            }
            val total = tripResponse?.finalCharge?.toDouble()
            val amount = String.format("%.2f", total)
            showAmount(amount)
        }
    }

    private fun clearAllPreviousTripData(){
        isActiveTrip = false
        tripID = null
        additionalCharge = null
        startPointLocation = null
        isCertainDistCross = false
        binding.rlStart.visibility = VISIBLE
        binding.rlPause.visibility = VISIBLE
        binding.rlStop.visibility = GONE
        binding.rlComplete.visibility = GONE
        binding.tvExtraAmount.text = "00.00"

        val total = 00.0.toDouble()
        val amount = String.format("%.2f", total)
        showAmount(amount)
    }

    // Function to calculate distance between two locations in meters
    fun calculateDistanceBetweenLocations(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val locationA = Location("point A")
        locationA.latitude = lat1
        locationA.longitude = lon1

        val locationB = Location("point B")
        locationB.latitude = lat2
        locationB.longitude = lon2

        return locationA.distanceTo(locationB)
    }

    override fun onLocationChanged(location: Location) {
        Log.e("location", "onLocationChanged: ${location.latitude} , ${location.longitude} and speed is ${location.speed * 3.6}kmh" )

        if(isActiveTrip){
            if(startPointLocation == null){
                startPointLocation = location
            }
            endPointLocation = location;
        }else{
            Log.e("location", "onLocationChanged: trip is not running." )
        }

    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called: isTripActive :- $isActiveTrip")

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && isActiveTrip
        ) {

            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.e("location", "last location end network ${location != null}")
            }
            if (location != null) {
                val kmh = location.speed * 3.6

                // TODO Manage the additional trip charge
                tripStartContinueStopController?.sendTripStartContinueStop(
                    tripID ?: 0,
                    carType,
                    getLocationName(requireActivity(),location),
                    location.latitude,
                    location.longitude,
                    kmh.toInt(),
                    "finish",
                    additionalCharge ?: 0.0,
                    Utils.getCurrentTime(),
                    plateNo,
                    driverId,
                    isCertainDistCross
                )
            }

            tripID = null
            super.onDestroy()

        } else {
            super.onDestroy()
        }
    }
}
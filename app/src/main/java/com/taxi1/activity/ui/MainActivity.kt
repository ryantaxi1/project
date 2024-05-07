package com.taxi1.activity.ui


import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
//import com.taxi1.CarAppSyncComponent
import com.taxi1.R
import com.taxi1.Retrofit.Response.TripStartContinueStopResponse
import com.taxi1.ViewResponse.ITripStartContinueStopView
import com.taxi1.activity.Controller.ITripStartContinueStopController
import com.taxi1.activity.Controller.TripStartContinueFinishController
import com.taxi1.broadcast.LocationProviderChangedReceiver
import com.taxi1.databinding.MapboxActivityNavigationViewBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.Constants.MAXI_CAR
import com.taxi1.utils.Constants.MAXI_CAR_CHARGE
import com.taxi1.utils.Constants.REGULAR_CAR
import com.taxi1.utils.Constants.REGULAR_CAR_CHARGE
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.TripListener
import com.taxi1.utils.Utils
import com.taxi1.utils.Utils.getCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


//@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class MainActivity : AppCompatActivity(), LocationListener, TripListener, ITripStartContinueStopView {
    private var TAG = "MainActivity"
    private lateinit var binding: MapboxActivityNavigationViewBinding
    private lateinit var toolBar: View
    private var isActiveTrip: Boolean = false
    lateinit var mainHandler: Handler
    lateinit var storedata : StoreUserData
    var driverId : String = ""
    var plateNo : String = ""
    var carType: String = ""
    var ivBack : ImageView? = null
    var ivTripInfo : ImageView? = null
    var tripID: Int? = null
    var additonalCharge: Int? = null
    val userName = "Jones"
    lateinit var myToggle : ToggleButton
    private var carCharges = mutableMapOf(REGULAR_CAR to REGULAR_CAR_CHARGE, MAXI_CAR to MAXI_CAR_CHARGE)
    //location
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager

    var tripStartContinueStopController: ITripStartContinueStopController? = null


    private val tripTimer = object : Runnable {
        override fun run() {
            Log.e("location", "1 minute handler")
            var locations: Location?
            if ((ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                locations = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                Log.e("location", "last location gps ${locations != null}")
                if(locations == null){
                    locations = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    Log.e("location", "last location network ${locations != null}")
                }
                if (locations != null) {
                    if(isActiveTrip){
                        val kmh = locations.speed * 3.6

                        /**
                         * TODO remove local speed management.
                         */
                        tripID?.let {
                            tripStartContinueStopController?.sendTripStartContinueStop(
                                tripID!!,
                                carType,
                                getLocationName(locations),
                                locations.latitude,
                                locations.longitude,
                                kmh.toInt(),
                                "continue",
                                0,
                                getCurrentTime(),
                                plateNo,
                                driverId
                            )
                        }

                    }
                }
            }
            mainHandler.postDelayed(this, 60000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapboxActivityNavigationViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storedata = StoreUserData(this)
        plateNo = storedata.getString(Constants.PLATE_NUMBER)
        driverId = storedata.getString(Constants.DRIVER_ID)

        //location broadcast receiver
        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        mainHandler = Handler(Looper.getMainLooper())

        tripStartContinueStopController = TripStartContinueFinishController(this, this)

        supportActionBar?.title = "Taxi1"
//        initialToolBar()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0f, this)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        // TODO Not Currently used
//        CarAppSyncComponent.getInstance().setNavigationView(binding.navigationView, this)

        /**
         * TODO Remove initialize the dao, repo, viewModel  because the it is for local db
         */

        // TODO going to expose a public api to share a replay controller
        // This allows to simulate your location
        // binding.navigationView.api.routeReplayEnabled(true)

        if(carType.isEmpty()){
            carSelection()
        }

        /*ivBack?.setOnClickListener {
            finish()
        }
        ivTripInfo?.setOnClickListener {
            val intent = Intent(this@MainActivity, TripDetailsCalculationActivity::class.java)
            startActivity(intent)
        }*/

    }

    // It's default actionbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        val menuItem = menu!!.findItem(R.id.action_car_change)
        myToggle = (menuItem.actionView as ToggleButton?)!!
        myToggle!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if(myToggle!!.isChecked){
                carType = MAXI_CAR
                Toast.makeText(this@MainActivity, "$carType", Toast.LENGTH_SHORT).show()
            }else{
                carType = REGULAR_CAR
                Toast.makeText(this@MainActivity, "$carType", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.action_item_trip_data) {
            val intent = Intent(this@MainActivity, AllTripDataActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_item_change_car_fare) {
            changeCarFareDialog(this@MainActivity)
            return true
        }
        if(id == R.id.action_car_change){
            val toggleButton = findViewById<ToggleButton>(R.id.action_car_change)
            if(toggleButton!!.isChecked){
                carType = REGULAR_CAR
                Toast.makeText(this@MainActivity, "$carType", Toast.LENGTH_SHORT).show()
            }else{
                carType = MAXI_CAR
                Toast.makeText(this@MainActivity, "$carType", Toast.LENGTH_SHORT).show()
            }
        }
         return super.onOptionsItemSelected(item)
    }

    /*fun initialToolBar(){
        val actionBar = supportActionBar
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setDisplayShowCustomEnabled(true)
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar_layout, null)
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        actionBar?.setCustomView(customView, params)
        toolBar = actionBar?.customView!!
        ivBack = toolBar.findViewById(R.id.ivBack)
        ivTripInfo = toolBar.findViewById(R.id.ivTripInfo)
//        supportActionBar?.title = "Chunchi"
    }*/

    override fun onLocationChanged(location: Location) {
        Log.e("location", "onLocationChanged: ${location.latitude} , ${location.longitude} and speed is ${location.speed * 3.6}kmh" )

    }

    override fun onActiveTrip() {
        Toast.makeText(this, "Trip Started", Toast.LENGTH_SHORT).show()

//        viewModel.removePastTripRecord()
//        tripID = (0..10000).random()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    getLocationName(location),
                    location.latitude,
                    location.longitude,
                    kmh.toInt(),
                    "start",
                    0,
                    getCurrentTime(),
                    plateNo,
                    driverId
                )
            }
        }

        mainHandler.post(tripTimer)

    }

    override fun onArrivedTrip() {
        Toast.makeText(this, "Trip Completed", Toast.LENGTH_SHORT).show()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location == null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.e("location", "last location end network ${location != null}")
            }
            if(location != null){
                showAdditionalChargeDialog(this@MainActivity, location)

            }
        }
        mainHandler.removeCallbacks(tripTimer)

    }

    override fun onFreeRide() {
        Toast.makeText(this, "Free Ride Started", Toast.LENGTH_SHORT).show()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && isActiveTrip) {
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location == null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.e("location", "last location end network ${location != null}")
            }
            if(location != null){
                showAdditionalChargeDialog(this@MainActivity, location)

            }
            mainHandler.removeCallbacks(tripTimer)
        }
//        isActiveTrip = false
    }

    fun carSelection(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.car_selection_dialog)
        val cvRegular = dialog.findViewById(R.id.cvRegular) as CardView
        val cvTaxi = dialog.findViewById(R.id.cvTaxi) as CardView
        val btnOK = dialog.findViewById(R.id.ok) as Button

        cvRegular.setOnClickListener {
            carType = REGULAR_CAR
            cvRegular.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            cvTaxi.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            if(::myToggle.isInitialized){
                myToggle.isChecked = false
            }
        }
        cvTaxi.setOnClickListener {
            carType = MAXI_CAR
            cvTaxi.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            cvRegular.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            if(::myToggle.isInitialized){
                myToggle.isChecked = true
            }
        }

        btnOK.setOnClickListener {
            if(carType.isNotEmpty()){
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun getLocationName(location : Location): String {
        val myLocation = Geocoder(this, Locale.getDefault())
        val myList: List<Address>? = myLocation.getFromLocation(location.latitude, location.longitude, 1)
        var address: Address = myList!![0] as Address
        var addressStr = ""

        for (index in 0 until myList.size) {
            address = myList[index]
            addressStr += "${address.getAddressLine(0)}, "
        }

        return addressStr
    }


    fun changeCarFareDialog(context: Context) {
        try {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.add_aditional_charge_layout)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)

            val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
            val txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
            val txtTripId = dialog.findViewById<TextView>(R.id.txtTripId)
            val etAdditionalCharge = dialog.findViewById<EditText>(R.id.etAdditionalCharge)
            val btnAddCharges = dialog.findViewById<Button>(R.id.btnAddCharges)

            txtTitle.text = "Change $carType fare"
            txtTripId.text = tripID.toString()
            etAdditionalCharge.setText(carCharges[carType].toString())

            ivClose.setOnClickListener {
                dialog.dismiss()
            }

            btnAddCharges.setOnClickListener {
                if(!etAdditionalCharge.text.isNullOrBlank()) {
                    if (carType == REGULAR_CAR) {
                        // Regular car price change
                        carCharges[carType] = etAdditionalCharge.text.toString().toInt()
                    } else {
                        // Maxi car price change
                        carCharges[carType] = etAdditionalCharge.text.toString().toInt()
                    }
                    dialog.dismiss()
                }else{
                    Toast.makeText(dialog.context, "Please add car charge..", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(TAG, "showAdditionalChargeDialog: ${e.message}" )
            e.printStackTrace()
        }
    }

    fun showAdditionalChargeDialog(context: Context, location: Location) {
        try {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.add_aditional_charge_layout)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)

            val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
            val txtTripId = dialog.findViewById<TextView>(R.id.txtTripId)
            val etAdditionalCharge = dialog.findViewById<EditText>(R.id.etAdditionalCharge)
            val btnAddCharges = dialog.findViewById<Button>(R.id.btnAddCharges)

            txtTripId.text = tripID.toString()

            ivClose.setOnClickListener {
                dialog.dismiss()
                val kmh = location.speed * 3.6

                /**
                 * TODO remove location insert local db.
                 */
                tripStartContinueStopController?.sendTripStartContinueStop(
                    tripID!!,
                    carType,
                    getLocationName(location),
                    location.latitude,
                    location.longitude,
                    kmh.toInt(),
                    "finish",
                    additonalCharge ?: 0,
                    getCurrentTime(),
                    plateNo,
                    driverId
                )

                tripID = null


                /*CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    val intent = Intent(this@MainActivity, AllTripDataActivity::class.java)
                    startActivity(intent)
                }*/
            }

            btnAddCharges.setOnClickListener {
                if(!etAdditionalCharge.text.isNullOrBlank()){
                    additonalCharge = etAdditionalCharge.text.toString().toInt()
                    val kmh = location.speed * 3.6

                    /**
                     * TODO remove location insert local db.
                     */
                    tripStartContinueStopController?.sendTripStartContinueStop(
                        tripID!!,
                        carType,
                        getLocationName(location),
                        location.latitude,
                        location.longitude,
                        kmh.toInt(),
                        "finish",
                        additonalCharge ?: 0,
                        getCurrentTime(),
                        plateNo,
                        driverId
                    )

                    tripID = null



                    /*CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        val intent = Intent(this@MainActivity, AllTripDataActivity::class.java)
                        startActivity(intent)
                    }*/

                    dialog.dismiss()
                }else{
                    Toast.makeText(dialog.context, "Please add additional charge...", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        } catch (e: Exception) {
            Log.e(TAG, "showAdditionalChargeDialog: ${e.message}" )
            e.printStackTrace()
        }
    }

    override fun onResponse(tripResponse: TripStartContinueStopResponse) {
        if(tripResponse.status == 1){
            if(tripID == null && tripResponse.tripStatus == 1){
                isActiveTrip = true
                tripID = tripResponse.tripId
            }
            else if(tripResponse.tripStatus == 3){
                isActiveTrip = false
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    val intent = Intent(this@MainActivity, AllTripDataActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBackPressed() {
        if(tripID == null){
            super.onBackPressed();
        }else{
            Utils.showToast(this, "You can not back at this stage.")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called: isTripActive :- $isActiveTrip")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && isActiveTrip
        ) {

            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.e("location", "last location end network ${location != null}")
            }
            if (location != null) {
                val kmh = location.speed * 3.6

                tripStartContinueStopController?.sendTripStartContinueStop(
                    tripID ?: 0,
                    carType,
                    getLocationName(location),
                    location.latitude,
                    location.longitude,
                    kmh.toInt(),
                    "finish",
                    additonalCharge ?: 0,
                    getCurrentTime(),
                    plateNo,
                    driverId
                )
            }

            tripID = null
            super.onDestroy()

        } else {
            super.onDestroy()
        }
    }
}
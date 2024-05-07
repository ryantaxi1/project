package com.taxi1.activity.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.taxi1.Bean.TripData
import com.taxi1.R
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.taxi1.databinding.NewCalculationActivityBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.Constants.MAXI_CAR
import com.taxi1.utils.Constants.MAXI_CAR_CHARGE
import com.taxi1.utils.Constants.REGULAR_CAR
import com.taxi1.utils.Constants.REGULAR_CAR_CHARGE
import com.taxi1.utils.Constants.TRIP_LIST
import com.taxi1.utils.StoreUserData

class NewCalculationActivity : AppCompatActivity() {
    private val TAG = NewCalculationActivity::class.java.simpleName
    lateinit var binding: NewCalculationActivityBinding
    private var tripData : AllTripRecordsResponse.Result? = null
    private var waitingChargesList = ArrayList<TripData>()
    lateinit var storedata: StoreUserData
//    private var travelAmounts = 100
    private var waitingCharges = 0

    private var carCharges = mapOf(REGULAR_CAR to REGULAR_CAR_CHARGE, MAXI_CAR to MAXI_CAR_CHARGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storedata = StoreUserData(this)
        binding = NewCalculationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra(TRIP_LIST)) {
            tripData = intent.getSerializableExtra(TRIP_LIST) as AllTripRecordsResponse.Result
        }

        Log.e(TAG, "onCreate get trip list: $tripData")

        supportActionBar?.hide()

        initClick()
        tripCharges()
    }

    private fun showAmount(amount: String) {

        binding.viewAmountParent.removeAllViews()
        for (char in amount) {
            val dynamicTextview = TextView(this)

            if(!char.equals('.'))
            {
                dynamicTextview.setBackgroundResource(R.drawable.bg_fare_text)
                val paddingHorizontal = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)
                val paddingVertical = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp)
                dynamicTextview.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            }

            dynamicTextview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
            }

            dynamicTextview.setTextColor(resources.getColor(R.color.dark_blue))

            dynamicTextview.setTypeface(null, Typeface.BOLD)

            dynamicTextview.setText("$char")




            val textSize = resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._45ssp).toFloat()
            dynamicTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)



            binding.viewAmountParent.addView(dynamicTextview)
        }
    }

    private fun initClick() {
        binding.btnOk.setOnClickListener {
            val intent = Intent(this@NewCalculationActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.llMeter.setOnClickListener {
            bottomCalculationSectionView("meter")
        }

        binding.llExtra.setOnClickListener {
            bottomCalculationSectionView("extra")
        }

        binding.llTarif.setOnClickListener {
            bottomCalculationSectionView("tarif")
        }
    }

    fun bottomCalculationSectionView(title: String){
        if(title == "meter"){
            binding.llMeter.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_white_redius)
            binding.llExtra.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)
            binding.llTarif.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)

            binding.cvCalculationDetails.visibility = View.VISIBLE
        }else if (title == "extra"){
            binding.llMeter.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)
            binding.llExtra.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_white_redius)
            binding.llTarif.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)

            binding.cvCalculationDetails.visibility = View.GONE
        }else{
            binding.llMeter.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)
            binding.llExtra.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_light_green_radius)
            binding.llTarif.background = ContextCompat.getDrawable(this@NewCalculationActivity, R.drawable.bg_white_redius)

            binding.cvCalculationDetails.visibility = View.GONE
        }
    }

    private fun tripCharges() {
        if(tripData==null) return
        binding.tvUserName.text = storedata.getString(Constants.USERNAME)
        binding.tvUserId.text = storedata.getString(Constants.USERID)

        binding.tvCarCharges.text = tripData?.carCharge
        binding.tvWaitingChargeAmount.text = tripData?.totalWaitingCharge
        binding.tvTravelAmounts.text = tripData?.totalTravelCharge
        binding.tvExtraAmount.text = "$${String.format("%.2f", (tripData?.additionalCharge?.toInt() ?: 0).toDouble())}"

        binding.tvStartLocation.text = tripData?.startLocation ?: "No data found"

        binding.tvEndLocation.text = tripData?.stopLocation ?: "No data found"

        binding.tvAdditionalAmounts.text = (tripData?.additionalCharge?.toInt() ?: 0).toString()


        // Calculate the waiting charge < 26Kmh
        /*waitingChargesList = tripData.filter {
            it.waitingCharges != null
        } as ArrayList<TripData>
        Log.e(TAG, "waiting charges list: $waitingChargesList")
        if (waitingChargesList.isNotEmpty()) {
            waitingCharges = waitingChargesList[0].waitingCharges!!
            binding.tvWaitingChargeAmount.text = (waitingChargesList.size * waitingCharges).toString()
        } else {
            binding.tvWaitingChargeAmount.text = waitingCharges.toString()
        }*/

        // Calculate travel charge > 26kmh
        /*travelAmountsList = tripDataLists.filter {
                it.travelAmount != null
            } as ArrayList<TripData>
            Log.e(TAG, "travelAmounts charges list: $travelAmountsList" )
            if(travelAmountsList.isNotEmpty()){
                travelAmounts = travelAmountsList[0].travelAmount!!
                binding.tvTravelAmounts.text = (travelAmountsList.size * travelAmounts).toString()
            }else{
                binding.tvTravelAmounts.text = travelAmounts.toString()
            }*/

        //CAR CHARGES
        /*binding.tvCarCharges.text = (tripData.last().carFare ?: carCharges[tripData.last().title]).toString()
        binding.tvTravelAmounts.text = travelAmounts.toString()*/
//        tvTotalAmounts.text = (tvWaitingChargeAmount.text.toString().toInt() + travelAmounts + tvCarCharges.text.toString().toInt()).toString()
//        val splits = (binding.tvWaitingChargeAmount.text.toString().toInt() + travelAmounts + binding.tvCarCharges.text.toString().toInt()).toString()

//        binding.tvExtraAmount.text = "$${String.format("%.2f", (tripData.last().additionalCharge ?: 0).toDouble())}"
//        val total = binding.tvWaitingChargeAmount.text.toString().toDouble() + binding.tvTravelAmounts.text.toString().toDouble() + binding.tvCarCharges.text.toString().toDouble() + (binding.tvAdditionalAmounts.text.toString().toDouble())


        val total = tripData?.finalChage?.toDouble()
        val amount = String.format("%.2f", total)
        showAmount(amount)

        /*binding.tvStartLocation.text =
            tripData[0].startLocation ?: tripData[0].currentLocation
                    ?: "No data found"

        binding.tvEndLocation.text =
            tripData.last().endLocation ?: tripData.last().currentLocation
                    ?: "No data found"

        binding.tvAdditionalAmounts.text = (tripData.last().additionalCharge ?: 0).toString()*/
    }

    fun addNewViewProgrametically(splits: String, i: Int) {
        val dynamicTextview = TextView(this)
        val dynamicSpaceView = Space(this)

        dynamicTextview.text = splits[i].toString()

        val paddingHorizontal = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)
        val paddingVertical = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp)
        dynamicTextview.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

        dynamicTextview.setBackgroundResource(R.drawable.bg_fare_text)

        dynamicTextview.setTextColor(resources.getColor(R.color.dark_blue))

        dynamicTextview.setTypeface(null, Typeface.BOLD)

        val textSize = resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._45ssp).toFloat()
        dynamicTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)


        dynamicTextview.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dynamicSpaceView.layoutParams = LinearLayout.LayoutParams(
            com.intuit.sdp.R.dimen._5sdp,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.llTotalFare.addView(dynamicTextview)
        binding.llTotalFare.addView(dynamicSpaceView)
    }
}
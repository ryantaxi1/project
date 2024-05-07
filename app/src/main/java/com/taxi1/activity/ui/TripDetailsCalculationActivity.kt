package com.taxi1.activity.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taxi1.Bean.TripData
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.taxi1.adapter.TripDataAdapter
import com.taxi1.databinding.ActivityTripDetailsCalculationBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.Constants.MAXI_CAR
import com.taxi1.utils.Constants.MAXI_CAR_CHARGE
import com.taxi1.utils.Constants.REGULAR_CAR
import com.taxi1.utils.Constants.REGULAR_CAR_CHARGE
import com.taxi1.utils.Constants.TRIP_CONTINUE_DATA
import com.taxi1.utils.Constants.TRIP_LIST
import com.taxi1.utils.Utility.showTripDataDialog

class TripDetailsCalculationActivity : AppCompatActivity(), TripDataAdapter.TripListClickListener {
    val TAG = TripDetailsCalculationActivity::class.java.simpleName
    lateinit var binding: ActivityTripDetailsCalculationBinding
    lateinit var tripAdapter: TripDataAdapter
    private var tripId = 0

    private var tripDataLists = ArrayList<AllTripRecordsResponse.Result.ContinueTripArray>()
    private var travelAmountsList = ArrayList<TripData>()
    private var waitingChargesList = ArrayList<TripData>()
    private var continueTripList : AllTripRecordsResponse.Result? = null

    private var travelAmounts = 100
    private var waitingCharges = 0
    private var carCharges = mapOf(REGULAR_CAR to REGULAR_CAR_CHARGE, MAXI_CAR to MAXI_CAR_CHARGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.getSerializableExtra(Constants.TRIP_CONTINUE_DATA) != null){
            continueTripList = intent.getSerializableExtra(TRIP_CONTINUE_DATA, ) as AllTripRecordsResponse.Result?
        }
        binding.txtTripId.text = "Trip Id ${continueTripList?.tripId ?: 0} Records"


        /**
         * TODO Remove initialize the dao, repo, viewModel  because the it is for local db
         */

        initClick()
        setAdapter()
        initObserver()
    }

    private fun initClick() {
        binding.btnCalculate.setOnClickListener {

            val bundle = Bundle()
            val intent = Intent(Intent(this@TripDetailsCalculationActivity, NewCalculationActivity::class.java))
            bundle.putSerializable(TRIP_LIST, continueTripList)
            intent.putExtras(bundle)
            startActivity(intent)
//            showTripCalculationDialog(this@TripDetailsCalculationActivity)
        }
    }

    private fun setAdapter() {
        tripAdapter = TripDataAdapter(this, tripDataLists, this)
        binding.rvTripData.layoutManager = LinearLayoutManager(this)
        binding.rvTripData.adapter = tripAdapter

    }

    private fun initObserver() {

        /**
         * TODO That used only get data from the local db.
         */


        tripDataLists.clear()
        if (!continueTripList?.continueTripArray.isNullOrEmpty()) {
            tripDataLists.clear()
            tripDataLists.addAll(continueTripList?.continueTripArray!!)
            tripAdapter.notifyDataSetChanged()
        } else {
            binding.rvTripData.visibility = View.GONE
            binding.txtNoTripData.visibility = View.VISIBLE
        }

    }


    override fun tripItemClick(tripData: AllTripRecordsResponse.Result.ContinueTripArray) {
        showTripDataDialog(this, tripData, continueTripList!!)
    }
}
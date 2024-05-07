package com.android.taxi1in_carapp.activity.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.android.taxi1in_carapp.activity.Controller.AllTripRecordsController
import com.android.taxi1in_carapp.activity.Controller.IAllTripRecordsController
import com.android.taxi1in_carapp.activity.Controller.IAllTripRecordsView
import com.taxi1.adapter.AllTripDataAdapter
import com.android.taxi1in_carapp.databinding.ActivityAllTripDataBinding
import com.taxi1.utils.Constants.TRIP_CONTINUE_DATA
import com.taxi1.utils.CustomDialogInterface
import com.taxi1.utils.Utils

class AllTripDataActivity : AppCompatActivity(), AllTripDataAdapter.AllTripListener, IAllTripRecordsView {
    val TAG = AllTripDataActivity::class.java.simpleName
    lateinit var binding: ActivityAllTripDataBinding
    lateinit var allTripDataAdapter: AllTripDataAdapter

    private var allTripList = ArrayList<AllTripRecordsResponse.Result>()
    private var lastTripId : Int? = null
    var allTripRecordsController: IAllTripRecordsController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTripDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allTripRecordsController = AllTripRecordsController(this, this)

        /**
         * TODO Remove initialize the dao, repo, viewModel  because the it is for local db
         */

        setAdapter()
        initObserver()
        swipeToRemove()

        allTripRecordsController?.allTripRecords()

    }

    private fun setAdapter() {
        allTripDataAdapter = AllTripDataAdapter(this, allTripList, this)
        binding.rvTripData.layoutManager = LinearLayoutManager(this)
        binding.rvTripData.adapter = allTripDataAdapter
    }

    private fun initObserver() {

        /**
         * TODO That used only get data from the local db.
         */
        /*viewModel.getAllTripData.observe(this){ it ->
            tripDataLists.clear()
            allTripList.clear()
            if(it != null && it.isNotEmpty()){
                tripDataLists.addAll(it)

                for(index in 0..tripDataLists.lastIndex){
                    if(lastTripId != tripDataLists[index].tripId){
                        lastTripId = tripDataLists[index].tripId
                        tripDataLists[index].isTripIdHeadingShow = true

                        val tripIdList = tripDataLists.filter { filterTripIdList ->
                            lastTripId!! == filterTripIdList.tripId
                        }
                        allTripList.add(
                            AllTripModel(
                                tripId = lastTripId,
                                startLocation = tripIdList[0].startLocation ?: tripIdList[0].currentLocation ?: "No data found",
                                endLocation = tripIdList.last().endLocation ?: tripIdList.last().currentLocation ?: "No data found",
                                startDate = tripIdList[0].dateTime,
                                endDate = tripIdList.last().dateTime,
                            )
                        )
                    }
                }
                Log.e(TAG, "all Trip data: $allTripList" )
                allTripDataAdapter.notifyDataSetChanged()

            }else{
                binding.rvTripData.visibility = View.GONE
                binding.txtNoTripData.visibility = View.VISIBLE
            }
        }*/
    }

    private fun swipeToRemove() {

        // Own Device slide to remove
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedTrip: AllTripRecordsResponse.Result = allTripList[viewHolder.adapterPosition]

                val position = viewHolder.adapterPosition

                allTripList.removeAt(viewHolder.adapterPosition)

                allTripDataAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                Utils.showPositiveNegativeAlertDialog(
                    this@AllTripDataActivity,
                    "Warning",
                    "Yes",
                    "No",
                    "Do you really want to delete?",
                    object : CustomDialogInterface {
                        override fun positiveClick(dialog: DialogInterface) {
                            dialog.dismiss()


                            deletedTrip.tripId?.let {
                                allTripRecordsController?.tripDeleteRecords(deletedTrip.tripId!!.toString())
                            }

                            if(allTripList.isNullOrEmpty()){
                                binding.rvTripData.visibility = View.GONE
                                binding.txtNoTripData.visibility = View.VISIBLE
                            }else{
                                binding.rvTripData.visibility = View.VISIBLE
                                binding.txtNoTripData.visibility = View.GONE
                            }
                        }

                        override fun negativeClick(dialog: DialogInterface) {
                            dialog.dismiss()
                            allTripList.add(position, deletedTrip)
                            allTripDataAdapter.notifyItemInserted(position)

                            if(allTripList.isNullOrEmpty()){
                                binding.rvTripData.visibility = View.GONE
                                binding.txtNoTripData.visibility = View.VISIBLE
                            }else{
                                binding.rvTripData.visibility = View.VISIBLE
                                binding.txtNoTripData.visibility = View.GONE
                            }
                        }
                    },
                )

            }

        }).attachToRecyclerView(binding.rvTripData)
    }

    override fun onResponse(allTripRecResponse: AllTripRecordsResponse) {
        if(!allTripRecResponse.result.isNullOrEmpty()){
            allTripList.addAll(allTripRecResponse.result!!)
            allTripDataAdapter.notifyDataSetChanged()

            if(allTripList.isNullOrEmpty()){
                binding.rvTripData.visibility = View.GONE
                binding.txtNoTripData.visibility = View.VISIBLE
            }else{
                binding.rvTripData.visibility = View.VISIBLE
                binding.txtNoTripData.visibility = View.GONE

                binding.rvTripData.scrollToPosition(allTripList.size - 1)
            }
        }
    }

    override fun deleteTripOnResponse(deleteTripResponse: CommonResponse) {
        binding.rvTripData.scrollToPosition(allTripList.size - 1)
    }

    override fun tripClick(data: AllTripRecordsResponse.Result) {
        val bundle = Bundle()
        val intent = Intent(this@AllTripDataActivity, TripDetailsCalculationActivity::class.java)
        bundle.putSerializable(TRIP_CONTINUE_DATA, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
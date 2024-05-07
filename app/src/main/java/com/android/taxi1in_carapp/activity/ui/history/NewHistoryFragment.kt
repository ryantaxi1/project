package com.android.taxi1in_carapp.activity.ui.history

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.android.taxi1in_carapp.activity.Controller.AllTripRecordsController
import com.android.taxi1in_carapp.activity.Controller.IAllTripRecordsController
import com.android.taxi1in_carapp.activity.Controller.IAllTripRecordsView
import com.android.taxi1in_carapp.activity.NewDashboardActivity
import com.android.taxi1in_carapp.activity.ui.history.adapter.AllTripHistoryAdapter
import com.android.taxi1in_carapp.databinding.FragmentNewHistoryBinding
import com.taxi1.utils.CustomDialogInterface
import com.taxi1.utils.Utils
import retrofit2.http.POST

class NewHistoryFragment : Fragment(), IAllTripRecordsView, AllTripHistoryAdapter.AllTripHistoryListener {
    private val TAG = this@NewHistoryFragment.javaClass.simpleName
    lateinit var binding : FragmentNewHistoryBinding
    private var newDashboardActivity: NewDashboardActivity? = null
    lateinit var allTripHistoryAdapter: AllTripHistoryAdapter
    private var allTripList = ArrayList<AllTripRecordsResponse.Result>()
    var allTripRecordsController: IAllTripRecordsController? = null
    var deleteTripPosition: ArrayList<Int> = arrayListOf()
    var tripSelectedList: ArrayList<Int> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDashboardActivity = activity as NewDashboardActivity?
        allTripRecordsController = AllTripRecordsController(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allTripRecordsController?.allTripRecords()

        initActivityChanges()
        initView()
        setAdapter()

//        allTripRecordsController?.allTripRecords()
    }

    private fun initView() {
        binding.llDeleteAllCheck?.setOnClickListener {
            if(!tripSelectedList.isNullOrEmpty()){

                Utils.showPositiveNegativeAlertDialog(
                    requireContext(),
                    "Alert",
                    "Confirm",
                    "Cancel",
                    "Are you sure you want to delete the trips ?",
                    object : CustomDialogInterface {
                        override fun positiveClick(dialog: DialogInterface) {
                            dialog.dismiss()
                            val deleteList = tripSelectedList.toString().replace("[","").replace("]","").replace(" ","")
                            Log.d(TAG, "delete checked list : $deleteList")
                            allTripRecordsController?.tripDeleteRecords(deleteList)
                        }

                        override fun negativeClick(dialog: DialogInterface) {
                            dialog.dismiss()
                        }
                    })
            }else{
                Toast.makeText(requireContext(), "please select trips", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initActivityChanges() {
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_green1))
        newDashboardActivity?.binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))

        newDashboardActivity?.binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        newDashboardActivity?.binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
    }

    private fun setAdapter() {
        allTripHistoryAdapter = AllTripHistoryAdapter(requireContext(), allTripList, this)
        binding.rvTripList?.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTripList?.adapter = allTripHistoryAdapter
    }

    override fun onResponse(allTripRecResponse: AllTripRecordsResponse) {
        if(allTripRecResponse.result != null){
            allTripList.clear()
            if(allTripRecResponse.result.isNullOrEmpty()){
                binding.rvTripList?.visibility = View.GONE
                binding.txtNoTripData?.visibility = View.VISIBLE
            }else{
                binding.rvTripList?.visibility = View.VISIBLE
                binding.txtNoTripData?.visibility = View.GONE

                allTripList.addAll(allTripRecResponse.result!!)
                allTripHistoryAdapter.notifyDataSetChanged()

                binding.tvMonthTripTotal?.text = allTripList.size.toString()

                binding.rvTripList?.scrollToPosition(allTripList.size - 1)
            }
        }else{
            binding.rvTripList?.visibility = View.GONE
            binding.txtNoTripData?.visibility = View.VISIBLE
            binding.tvMonthTripTotal?.text = "0"
        }
    }

    override fun deleteTripOnResponse(deleteTripResponse: CommonResponse) {
        if (deleteTripResponse.status == 1){
            allTripRecordsController?.allTripRecords()

            /*deleteTripPosition.forEach { position ->
                allTripList.removeAt(position)
                allTripHistoryAdapter.notifyDataSetChanged()
                binding.tvMonthTripTotal?.text = allTripList.size.toString()
            }
            if(allTripList.isNullOrEmpty()){
                binding.rvTripList?.visibility = View.GONE
                binding.txtNoTripData?.visibility = View.VISIBLE
                binding.tvMonthTripTotal?.text = "0"
            }*/
        }
    }

    override fun tripClick(data: AllTripRecordsResponse.Result) {

    }

    override fun deleteTrip(data: AllTripRecordsResponse.Result, position: Int) {

        Utils.showPositiveNegativeAlertDialog(
            requireContext(),
            "Alert",
            "Confirm",
            "Cancel",
            "Are you sure you want to delete the trip?",
            object : CustomDialogInterface {
                override fun positiveClick(dialog: DialogInterface) {
                    dialog.dismiss()
                    allTripRecordsController?.tripDeleteRecords(data.tripId!!.toString())
                    deleteTripPosition.add(position)
                }

                override fun negativeClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })
    }

    override fun checkedTrip(data: AllTripRecordsResponse.Result, position: Int) {
        tripSelectedList.add(data.tripId!!)
    }

    override fun unCheckedTrip(data: AllTripRecordsResponse.Result, position: Int) {
        tripSelectedList.remove(data.tripId!!)
    }
}
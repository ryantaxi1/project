package com.android.taxi1in_carapp.activity.ui.account.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.activity.Controller.IRegisterController
import com.android.taxi1in_carapp.adapter.AllVehicleAdapter
import com.android.taxi1in_carapp.databinding.FragmentOverviewBinding
import com.taxi1.utils.Constants


class OverviewFragment : Fragment() {
    private val TAG = "OverviewFragment"
    private lateinit var binding: FragmentOverviewBinding
    var userData: GetProfileResponse.Result? = null
    private var vehicleList: ArrayList<GetProfileResponse.Result.VehicalData> = arrayListOf()
    lateinit var vehicleAdapter: AllVehicleAdapter
    var controller: IRegisterController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userData = bundle.getSerializable(Constants.CURRENT_USER_DATA) as GetProfileResponse.Result?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvVehicles?.layoutManager = LinearLayoutManager(requireContext())
        vehicleAdapter = AllVehicleAdapter(requireContext(), vehicleList, userData)
        binding.rvVehicles?.adapter = vehicleAdapter

        if(!userData?.vehicalData.isNullOrEmpty()){
            vehicleList.addAll(userData?.vehicalData!!)
            vehicleAdapter.notifyDataSetChanged()
        }

        initView()
    }

    private fun initView() {


        if (!userData?.firstName.isNullOrBlank()) {
//                        val firstCharacter = userData.firstName[0] // Get the first character of the string
            binding.tvProfileShortName?.append("${userData?.firstName!![0]}")
        }
        if (!userData?.lastName.isNullOrBlank()){
            binding.tvProfileShortName?.append("${userData?.lastName!![0]}")
        }

        /*if(nameSplit!!.size >= 2){
            binding.tvProfileShortName?.text = "${nameSplit[0].first()} ${nameSplit[1].first()}"
        }*/

        binding.civProfile?.visibility = GONE
        binding.tvProfileShortName?.visibility = VISIBLE
        binding.tvProfileShortName?.isAllCaps = true

        /*if (!userData?.image.isNullOrBlank()) {
            Glide.with(this)
                .load(userData?.image).placeholder(R.drawable.ic_profile)
                .centerCrop()
                .into(binding.civProfile!!)

            binding.civProfile?.visibility = VISIBLE
            binding.tvProfileShortName?.visibility = GONE
        } else {

//            val nameSplit = userData?.username?.split(" ")


            *//**
             * Todo when new get profile api response then setup it.
             *//*
            if (!userData?.firstName.isNullOrBlank()) {
//                        val firstCharacter = userData.firstName[0] // Get the first character of the string
                binding.tvProfileShortName?.append("${userData?.firstName!![0]}")
            }
            if (!userData?.lastName.isNullOrBlank()){
                binding.tvProfileShortName?.append("${userData?.lastName!![0]}")
            }

            *//*if(nameSplit!!.size >= 2){
                binding.tvProfileShortName?.text = "${nameSplit[0].first()} ${nameSplit[1].first()}"
            }*//*

            binding.civProfile?.visibility = GONE
            binding.tvProfileShortName?.visibility = VISIBLE
            binding.tvProfileShortName?.isAllCaps = true
        }*/

        binding.tvName?.text = userData?.firstName + " " + userData?.lastName
        binding.tvFirstName?.text = userData?.firstName
        binding.tvLastName?.text = userData?.lastName
        binding.tvPhoneNo?.text = userData?.phone
        binding.tvEmail?.text = userData?.email
//                binding.edtPlateNumber?.setText(userData.plate_number)
//                binding.etdriverId?.setText(userData.driver_id)

    }
}
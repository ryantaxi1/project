package com.android.taxi1in_carapp.activity.ui.account.changeVehicle

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.ViewResponse.IAddVehicleView
import com.android.taxi1in_carapp.ViewResponse.IChangeVehicleView
import com.android.taxi1in_carapp.ViewResponse.IDeleteVehicleView
import com.android.taxi1in_carapp.activity.Controller.AddVehicleController
import com.android.taxi1in_carapp.activity.Controller.ChangeVehicleController
import com.android.taxi1in_carapp.activity.Controller.DeleteVehicleController
import com.android.taxi1in_carapp.activity.Controller.IAddVehicleController
import com.android.taxi1in_carapp.activity.Controller.IProfileController
import com.android.taxi1in_carapp.activity.Controller.ProfileController
import com.android.taxi1in_carapp.activity.ui.account.changeVehicle.adapter.ChangeVehicleListAdapter
import com.android.taxi1in_carapp.databinding.FragmentChangeVehicleBinding
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.utils.Constants
import com.taxi1.utils.CustomDialogInterface
import com.taxi1.utils.Utils

class ChangeVehicleFragment : Fragment(), ChangeVehicleListAdapter.VehicleListAdapterListener, IAddVehicleView, IProfileView, IChangeVehicleView, IDeleteVehicleView {
    private val TAG = "ChangeVehicleFragment"
    lateinit var binding: FragmentChangeVehicleBinding
    private var vehicleList: ArrayList<GetProfileResponse.Result.VehicalData> = arrayListOf()
    lateinit var vehicleListAdapter: ChangeVehicleListAdapter
    var userData: GetProfileResponse.Result? = null
    var getProfileController: IProfileController? = null
    var addVehicleController: IAddVehicleController? = null
    var changeVehicleController: ChangeVehicleController? = null
    var deleteVehicleController: DeleteVehicleController? = null


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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangeVehicleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vehicleListAdapter = ChangeVehicleListAdapter(requireContext(), vehicleList, userData, this)
        binding.rvVehicles?.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehicles?.adapter = vehicleListAdapter

        getProfileController = ProfileController(requireContext(), this)
        addVehicleController = AddVehicleController(requireContext(), this)
        changeVehicleController = ChangeVehicleController(requireContext(), this)
        deleteVehicleController = DeleteVehicleController(requireContext(), this)

        bindData()

        initView()
    }

    private fun bindData(){
        if(!userData?.vehicalData.isNullOrEmpty()){
            vehicleList.clear()
            for (data in userData?.vehicalData!!){
                data?.let {
                    vehicleList.add(data)
                }
            }
            vehicleListAdapter.updateListData(vehicleList, userData)
        }
        if(userData != null){

            /*if (userData?.image != null) {
                Glide.with(this).load(userData?.image).placeholder(R.drawable.ic_profile).into(binding.civProfile!!)

                binding.civProfile?.visibility = View.VISIBLE
                binding.tvProfileShortName?.visibility = View.GONE
            }else{
                if (!userData?.firstName.isNullOrBlank()) {
//                        val firstCharacter = userData.firstName[0] // Get the first character of the string
                    binding.tvProfileShortName?.append("${userData?.firstName!![0]}")
                }
                if (!userData?.lastName.isNullOrBlank()){
                    binding.tvProfileShortName?.append("${userData?.lastName!![0]}")
                }

                binding.civProfile?.visibility = View.GONE
                binding.tvProfileShortName?.visibility = View.VISIBLE
                binding.tvProfileShortName?.isAllCaps = true
            }*/

            binding.tvProfileShortName?.text = ""
            if (!userData?.firstName.isNullOrBlank()) {
//                        val firstCharacter = userData.firstName[0] // Get the first character of the string
                binding.tvProfileShortName?.append("${userData?.firstName!![0]}")
            }
            if (!userData?.lastName.isNullOrBlank()){
                binding.tvProfileShortName?.append("${userData?.lastName!![0]}")
            }

            binding.civProfile?.visibility = View.GONE
            binding.tvProfileShortName?.visibility = View.VISIBLE
            binding.tvProfileShortName?.isAllCaps = true

            binding.tvName?.text = userData?.firstName + " " + userData?.lastName
        }
    }

    private fun initView() {
        binding.ivAddVehicle?.setOnClickListener {
            if (binding.etVehicleNo?.text.toString().isEmpty()) {
                Utils.showToast(requireContext(), "Please enter vehicle platNo")
            }else{
                addVehicleController?.addVehicle("T"+binding.etVehicleNo?.text.toString())
            }
        }
    }

    override fun deleteVehicle(data: GetProfileResponse.Result.VehicalData) {

        Utils.showPositiveNegativeAlertDialog(
            requireContext(),
            "Alert",
            "Confirm",
            "Cancel",
            "Are you sure you want to delete the vehicle?",
            object : CustomDialogInterface {
                override fun positiveClick(dialog: DialogInterface) {
                    dialog.dismiss()
                    deleteVehicleController?.deleteVehicle(data.vehicalId.toString())
                }

                override fun negativeClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })
    }

    override fun changeVehicle(data: GetProfileResponse.Result.VehicalData) {
        Utils.showPositiveNegativeAlertDialog(
            requireContext(),
            "Alert",
            "Confirm",
            "Cancel",
            "Are you sure you want to change the current vehicle?",
            object : CustomDialogInterface {
                override fun positiveClick(dialog: DialogInterface) {
                    dialog.dismiss()
                    changeVehicleController?.changeVehicle(data.vehicalId.toString())
                }

                override fun negativeClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })
    }

    override fun onAddVehicle(response: CommonResponse) {
        binding.etVehicleNo?.text?.clear()
        // get new user data
        getProfileController!!.getProfile()
    }

    override fun onResponse(response: GetProfileResponse.Result?) {
        userData = response
        bindData()

    }

    override fun changeVehicleRes(response: CommonResponse) {
        getProfileController!!.getProfile()
    }

    override fun deleteVehicleRes(response: CommonResponse) {
        getProfileController!!.getProfile()

    }
}
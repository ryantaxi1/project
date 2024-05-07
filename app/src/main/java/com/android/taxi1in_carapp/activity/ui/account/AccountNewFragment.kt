package com.android.taxi1in_carapp.activity.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.ViewResponse.IAccountViewResponse
import com.android.taxi1in_carapp.activity.Controller.AccountController
import com.android.taxi1in_carapp.activity.Controller.IAccountController
import com.android.taxi1in_carapp.activity.NewDashboardActivity
import com.android.taxi1in_carapp.activity.NewSignInActivity
import com.android.taxi1in_carapp.activity.ui.account.changePassword.ChangePasswordFragment
import com.android.taxi1in_carapp.activity.ui.account.changeVehicle.ChangeVehicleFragment
import com.android.taxi1in_carapp.activity.ui.account.editAccount.EditAccountFragment
import com.android.taxi1in_carapp.activity.ui.account.overview.OverviewFragment
import com.android.taxi1in_carapp.databinding.FragmentNewAccountBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.Constants.CURRENT_USER_DATA
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.UpdateAccountDetails


class AccountNewFragment : Fragment(), IAccountViewResponse, UpdateAccountDetails {
    private var TAG = "NewAccountFragment"
    lateinit var binding : FragmentNewAccountBinding
    private var newDashboardActivity: NewDashboardActivity? = null
    var accountController: IAccountController? = null
    var isEdit = false
    var currentUserData : GetProfileResponse.Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDashboardActivity = activity as NewDashboardActivity?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountController = AccountController(requireContext(), this)
        accountController!!.getProfile()

        initActivityChanges()
        initClick()


    }

    private fun initActivityChanges() {
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_green1))
        newDashboardActivity?.binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))

        newDashboardActivity?.binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        newDashboardActivity?.binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
    }

    private fun initClick() {

        binding.rlOverview?.setOnClickListener {
            binding.rlOverview?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_selection_right_side)
            binding.rlEditAccountInfo?.background = null
            binding.rlChangeVehicle?.background = null
            binding.rlChangePassword?.background = null

            binding.tvOverview?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.tvEditInfo?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangeVehicle?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangePassword?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

            val bundle = Bundle()
            bundle.putSerializable(CURRENT_USER_DATA, currentUserData)
            setInnerFragment(OverviewFragment(), "OverviewFragment", bundle, false)
        }

        binding.rlEditAccountInfo?.setOnClickListener {

            binding.rlOverview?.background = null
            binding.rlEditAccountInfo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_selection_right_side)
            binding.rlChangeVehicle?.background = null
            binding.rlChangePassword?.background = null

            binding.tvOverview?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvEditInfo?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.tvChangeVehicle?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangePassword?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

            val bundle = Bundle()
            bundle.putSerializable(CURRENT_USER_DATA, currentUserData)
            val editAccountFragment = EditAccountFragment()
            setInnerFragment(editAccountFragment, "EditAccountFragment", bundle, false)
            editAccountFragment.setUpdateAccountListener(this)
            Log.d(TAG, "onResume set update listener: ")
        }

        binding.rlChangeVehicle?.setOnClickListener {
            binding.rlOverview?.background = null
            binding.rlEditAccountInfo?.background = null
            binding.rlChangeVehicle?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_selection_right_side)
            binding.rlChangePassword?.background = null

            binding.tvOverview?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvEditInfo?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangeVehicle?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.tvChangePassword?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }

            val bundle = Bundle()
            bundle.putSerializable(CURRENT_USER_DATA, currentUserData)
            setInnerFragment(ChangeVehicleFragment(), "ChangeVehicleFragment", bundle, false)
        }

        binding.rlChangePassword?.setOnClickListener {
            binding.rlOverview?.background = null
            binding.rlEditAccountInfo?.background = null
            binding.rlChangeVehicle?.background = null
            binding.rlChangePassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_selection_right_side)

            binding.tvOverview?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvEditInfo?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangeVehicle?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.tvChangePassword?.apply {
                setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }

            val bundle = Bundle()
            bundle.putSerializable(CURRENT_USER_DATA, currentUserData)
            setInnerFragment(ChangePasswordFragment(), "ChangePasswordFragment", bundle, false)
        }

        binding.llBtnLogout?.setOnClickListener {
            val storedata = StoreUserData(requireContext())
            storedata.setBoolean(Constants.ISLOGGEDIN,false)
            storedata.clearData(requireContext())
            val intent = Intent(newDashboardActivity, NewSignInActivity::class.java)
            startActivity(intent)
            newDashboardActivity?.finishAffinity()
        }
    }

    fun setInnerFragment(
        fragment: Fragment,
        tag: String,
        bundle: Bundle,
        addToBackstack:Boolean = true
    ) {

        if(newDashboardActivity is NewDashboardActivity){
            val transaction = newDashboardActivity?.supportFragmentManager?.beginTransaction()
            fragment.arguments = bundle
            if(addToBackstack){
                transaction?.addToBackStack(tag)
            }

            Log.e("setAccountInnerFragment1=", "setInnerFragment")
            Log.e("InnerAccount bundledata1=", bundle.toString())

            transaction?.replace(R.id.flAccountContainer, fragment)?.commit()
        }


    }

    override fun onResponse(response: GetProfileResponse.Result?) {

        currentUserData = response

        val bundle = Bundle()
        bundle.putSerializable(CURRENT_USER_DATA, currentUserData)

        if (arguments != null) {
            val isChangeVehicleOpen = requireArguments().getBoolean("isChangeVehicleOpen", false)
            if(isChangeVehicleOpen){
                binding.rlChangeVehicle?.performClick()
            }else{
                setInnerFragment(OverviewFragment(), "OverviewFragment", bundle, false)
            }
        }


//        if (isEdit==true){
//
//        }else {
//
//            try {
//
//                if(profile?.image != null && profile.image.equals(".jpg")){
//                    Glide.with(this)
//                        .load(profile.image).placeholder(R.drawable.ic_profile)
//                        .centerCrop()
//                        .into(binding.civProfile!!)
//
//                    binding.civProfile?.visibility = VISIBLE
//                    binding.tvProfileShortName?.visibility = GONE
//                }
//                else{
//
//                    val nameSplit = profile?.username?.split(" ")
//
//
//                    /**
//                     * Todo when new get profile api response then setup it.
//                     */
//                    /*if (profile?.firstName.isNotEmpty()) {
////                        val firstCharacter = profile?.firstName[0] // Get the first character of the string
//                        binding.tvProfileShortName?.append("${profile?.firstName[0]}")
//                    }
//                    if (profile?.lastName.isNotEmpty()){
//                        binding.tvProfileShortName?.append("${profile?.lastName[0]}")
//                    }*/
//
//                    /*if(nameSplit!!.size >= 2){
//                        binding.tvProfileShortName?.text = "${nameSplit[0].first()} ${nameSplit[1].first()}"
//                    }*/
//
//
//                    binding.civProfile?.visibility = GONE
//                    binding.tvProfileShortName?.visibility = VISIBLE
//                }
//
//
//
//                binding.tvName?.setText(profile!!.username)
//                binding.tvFirstName?.setText(profile!!.username)
//                binding.tvLastName?.setText(profile!!.username)
//                binding.tvPhoneNo?.setText(profile?.phone)
//                binding.tvEmail?.setText(profile?.email)
////                binding.edtPlateNumber?.setText(profile.plate_number)
////                binding.etdriverId?.setText(profile.driver_id)
//
//            } catch (e: Exception) {
//                Log.e(TAG, "onGetProfile: " + e.printStackTrace())
//            }
//        }
    }

    override fun onLogout(logout: CommonResponse) {
        if(logout.status == 1){

        }
    }

    override fun callGetProfileApi(isCallApi: Boolean) {
        Log.d(TAG, "callGetProfileApi is call api $isCallApi")
        if (isCallApi){
            accountController?.getProfile()
        }
    }
}
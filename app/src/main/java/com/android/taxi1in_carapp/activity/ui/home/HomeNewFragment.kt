package com.android.taxi1in_carapp.activity.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.activity.Controller.IProfileController
import com.android.taxi1in_carapp.activity.Controller.ProfileController
import com.android.taxi1in_carapp.activity.NewDashboardActivity
import com.android.taxi1in_carapp.activity.NoticeBoardActivity
import com.android.taxi1in_carapp.activity.ui.account.AccountNewFragment
import com.android.taxi1in_carapp.databinding.FragmentHomeNewBinding
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.utils.StoreUserData
import java.util.Calendar

class HomeNewFragment : Fragment(), IProfileView {

    lateinit var binding : FragmentHomeNewBinding
    private var newDashboardActivity: NewDashboardActivity? = null
    lateinit var storedata : StoreUserData
    var getProfileController: IProfileController? = null
    var profileData: GetProfileResponse.Result? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDashboardActivity = activity as NewDashboardActivity?
        storedata = StoreUserData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeNewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.tvName?.text = storedata.getString(Constants.USERNAME)
//        binding.tvVehicleNo?.text = storedata.getString(Constants.PLATE_NUMBER)

        getProfileController = ProfileController(requireActivity() ?: requireContext(), this)
        getProfileController!!.getProfile()

        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)

        binding.tvGreeting?.text = when (hour) {
            in 0..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            else -> "Good evening,"
        }

        binding.llBtnChangeVehicle?.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isChangeVehicleOpen", true)
            newDashboardActivity?.setFragment(AccountNewFragment(), "AccountNewFragment", bundle, false)
        }

        binding.tvFindMore?.setOnClickListener {
            val intent = Intent(requireContext(), NoticeBoardActivity::class.java)
            startActivity(intent)
        }

        initActivityChanges()
    }

    private fun initActivityChanges() {
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHome?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_green1))
        newDashboardActivity?.binding?.includeBottomBar?.ivAccount?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivMeter?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHistory?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivHelp?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.ivTraining?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_new2))

        newDashboardActivity?.binding?.includeBottomBar?.tvHome?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        newDashboardActivity?.binding?.includeBottomBar?.tvAccount?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvMeter?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvHistory?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvGetHelp?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
        newDashboardActivity?.binding?.includeBottomBar?.tvTraining?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_new2))
    }

    override fun onResponse(response: GetProfileResponse.Result?) {
        profileData = response

        binding.tvName?.text = response?.firstName
        binding.tvVehicleNo?.text = response?.plateNumber
    }
}
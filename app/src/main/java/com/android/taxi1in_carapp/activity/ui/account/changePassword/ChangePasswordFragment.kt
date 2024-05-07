package com.android.taxi1in_carapp.activity.ui.account.changePassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.taxi1.ViewResponse.ICommanView
import com.android.taxi1in_carapp.activity.Controller.ChangePasswordController
import com.android.taxi1in_carapp.activity.Controller.IChangePasswordController
import com.android.taxi1in_carapp.activity.Controller.IProfileController
import com.android.taxi1in_carapp.activity.NewResetPasswordActivity
import com.android.taxi1in_carapp.databinding.FragmentChangePasswordBinding
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.utils.Constants.CURRENT_USER_DATA
import com.taxi1.utils.InternetCheck


class ChangePasswordFragment : Fragment(), IProfileView, ICommanView {
    private val TAG = this@ChangePasswordFragment.javaClass.simpleName
    lateinit var binding: FragmentChangePasswordBinding
    var controller: IChangePasswordController? = null
    var getProfileController: IProfileController? = null
    var userData : GetProfileResponse.Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userData = bundle.getSerializable(CURRENT_USER_DATA) as GetProfileResponse.Result?
        }
        Log.d(TAG, "onCreate user data bundle:- $userData")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** when used the get profile api then uncomment
          */
        /*getProfileController = ProfileController(requireContext(), this)
        getProfileController!!.getProfile()*/

        if(userData != null){
            /*if (userData?.image != null) {
                Glide.with(this)
                    .load(userData?.image).placeholder(R.drawable.ic_profile).into(binding.civProfile!!)

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

        initClick()
    }

    private fun initClick() {
        binding.btnChangePassword?.setOnClickListener {
            updatePassword()
        }

        binding.tvForgotPassword?.setOnClickListener {
            val intent = Intent(requireActivity() ?: requireContext(), NewResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updatePassword() {

        val authSuccessful: Boolean = authenticate(
            binding.etCurrentPassword?.text.toString(),
            binding.etNewPassword?.text.toString(),
            binding.etConfirmNewPassword?.text.toString())
        if (authSuccessful) {
            binding.tvErrorCompleteAllFields?.visibility = View.GONE
            binding.etCurrentPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            controller = ChangePasswordController(requireContext(), this)
            controller!!.changePassword(binding.etCurrentPassword?.text.toString(), binding.etConfirmNewPassword?.text.toString())
        }
    }

    fun authenticate(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): Boolean {
        var check = false

        if (currentPassword.isEmpty()) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etCurrentPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields?.text = "Please enter current password"

            check = false
        } else if (newPassword.isNullOrEmpty() || newPassword.length < 8) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etCurrentPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etConfirmNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields?.text = "Please enter new password at lease 8 character"

            check = false
        } else if (confirmPassword.isEmpty()) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etCurrentPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)

            binding.tvErrorCompleteAllFields?.text = "Please enter confirm password"

            check = false

        } else if (newPassword != confirmPassword) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etCurrentPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etConfirmNewPassword?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)

            binding.tvErrorCompleteAllFields?.text = "Please same new password and confirm password"

            check = false
        }
        else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(requireContext())!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(requireActivity())
                check = false
            }
        }
        return check
    }

    override fun onComman(comman: CommonResponse?) {
        // change password response
        if(comman?.status!! == 1){
            binding.etCurrentPassword?.text?.clear()
            binding.etNewPassword?.text?.clear()
            binding.etConfirmNewPassword?.text?.clear()
        }
    }

    override fun onResponse(response: GetProfileResponse.Result?) {
        // get profile response
    }

}
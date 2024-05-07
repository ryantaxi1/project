package com.android.taxi1in_carapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.ViewResponse.IResetPasswordView
import com.android.taxi1in_carapp.activity.Controller.OtpVerificationController
import com.android.taxi1in_carapp.activity.Controller.ResetPasswordController
import com.android.taxi1in_carapp.databinding.ActivityNewOtpVerifyResetPasswordBinding
import com.taxi1.ViewResponse.IOtpVerificationView
import com.taxi1.utils.Constants.EMAIL
import com.taxi1.utils.InternetCheck

class NewOtpVerifyResetPasswordActivity : BaseActivity(), IOtpVerificationView, IResetPasswordView {
    lateinit var binding: ActivityNewOtpVerifyResetPasswordBinding
    var otpVerifyController: OtpVerificationController? = null
    var resetPasswordController: ResetPasswordController? = null
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOtpVerifyResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EMAIL)){
            email = intent.getStringExtra(EMAIL).toString()
        }

        otpVerifyController = OtpVerificationController(this, this)
        resetPasswordController = ResetPasswordController(this, this)

        initView()
    }

    private fun initView() {
        binding.btnSendOtp?.setOnClickListener {

            if(binding.otpPinView?.text?.length != 4){
                binding.tvErrorCompleteAllFields?.visibility = VISIBLE
                binding.tvErrorCompleteAllFields?.text = "Please enter 4 digit otp"
            }else{
                binding.tvErrorCompleteAllFields?.visibility = GONE
                otpVerifyController?.otpVerification(email, binding.otpPinView?.text.toString())
            }
        }

        binding.btnResetPassword?.setOnClickListener {
            val authSuccessful: Boolean = resetPasswordAuthenticate(
                binding.etPassword?.text.toString(),
                binding.etConfirmPassword?.text.toString(),
            )

            if(authSuccessful) {
                binding.tvErrorCompleteAllFields?.visibility = GONE
                resetPasswordController?.resetPassword(email, binding.etPassword?.text.toString(), binding.etConfirmPassword?.text.toString())
            }
        }
    }

    fun resetPasswordAuthenticate(
        password: String,
        confirmPassword: String,
    ): Boolean {
        var check = false

        if (password.isNullOrEmpty() || password.length < 8) {
            binding.tvErrorCompleteAllFields?.visibility = VISIBLE
            binding.tvErrorCompleteAllFields?.text = "Please enter new password at lease 8 character"

            binding.etPassword?.background = ContextCompat.getDrawable(this@NewOtpVerifyResetPasswordActivity, R.drawable.bg_edittext_field_error)
            binding.etConfirmPassword?.background = ContextCompat.getDrawable(this@NewOtpVerifyResetPasswordActivity, R.drawable.bg_rounded_edit_text_white)

            check = false
        } else if (confirmPassword.isEmpty() || password != confirmPassword) {
            binding.tvErrorCompleteAllFields?.visibility = VISIBLE
            binding.tvErrorCompleteAllFields?.text = "New password and confirm password not match"
            binding.etPassword?.background = ContextCompat.getDrawable(this@NewOtpVerifyResetPasswordActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword?.background = ContextCompat.getDrawable(this@NewOtpVerifyResetPasswordActivity, R.drawable.bg_edittext_field_error)

            check = false
        } else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(this@NewOtpVerifyResetPasswordActivity)!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(this@NewOtpVerifyResetPasswordActivity)
                check = false
            }
        }
        return check
    }

    override fun onOtpVerifyResponse(response: CommonResponse?) {
        if(response?.status == 1){
            binding.llOtpView?.visibility = GONE
            binding.llResetPassword?.visibility = VISIBLE
        }

    }

    override fun onResetPasswordResponse(response: CommonResponse?) {
        if(response?.status == 1){
            val intent = Intent(this, NewSignInActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}
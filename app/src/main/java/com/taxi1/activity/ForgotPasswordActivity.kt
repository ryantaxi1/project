package com.taxi1.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.taxi1.Auth.ForgotPasswordAuth
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.ViewResponse.IForgotPasswordView
import com.taxi1.activity.Controller.ForgotPasswordController
import com.taxi1.activity.Controller.IForgotPasswordController
import com.taxi1.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity(),View.OnClickListener ,IForgotPasswordView{
    private lateinit var binding : ActivityForgotPasswordBinding
    var controller: IForgotPasswordController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setContentView(binding.root)
        binding.toolbar.tvTitle.text = "Forgot Password"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setlistners()
    }

    private fun setlistners() {

        binding.toolbar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnSubmit -> {
               forgotPassword()
            }
        }
    }

    private fun forgotPassword() {

        val authSuccessful: Boolean = ForgotPasswordAuth.authenticate(
            binding.edtEmail.text.toString(), this)
        if (authSuccessful) {

            controller = ForgotPasswordController(this, this)
            controller!!.forgotPassword(binding.edtEmail.text.toString())
        }
    }

    override fun onForgot(login: CommanResponse?) {
        finish()
    }
}
/*
package com.android.taxi1in_carapp.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.android.taxi1in_carapp.Auth.ChangePasswordAuth
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.activity.Controller.ChangePasswordController
import com.android.taxi1in_carapp.activity.Controller.IChangePasswordController
import com.android.taxi1in_carapp.databinding.ActivityChangePasswordBinding
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.ViewResponse.ICommanView

class ChangePasswordActivity : BaseActivity(),View.OnClickListener,ICommanView {

    private lateinit var binding : ActivityChangePasswordBinding
    var controller: IChangePasswordController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Change Password"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setlistners()
    }

    private fun setlistners() {
        binding.toolbar.ivBack.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnUpdate -> {
              UpdatePassword()
            }
        }
    }

    private fun UpdatePassword() {

        val authSuccessful: Boolean = ChangePasswordAuth.authenticate(
            binding.edtpwd.text.toString(),
            binding.edtNewpwd.text.toString(),
            binding.edtConfpwd.text.toString(), this)
        if (authSuccessful) {
            controller = ChangePasswordController(this, this)
            controller!!.changepassword(binding.edtpwd.text.toString(), binding.edtConfpwd.text.toString())
        }
    }

    override fun onComman(comman: CommanResponse?) {
        finish()
    }
}*/

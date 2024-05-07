package com.taxi1.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.taxi1.Auth.ChangePasswordAuth
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.ViewResponse.ICommanView
import com.taxi1.activity.Controller.ChangePasswordController
import com.taxi1.activity.Controller.IChangePasswordController
import com.taxi1.activity.Controller.IRegisterController
import com.taxi1.databinding.ActivityChangePasswordBinding
import com.taxi1.databinding.ActivitySoshistoryBinding

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
}
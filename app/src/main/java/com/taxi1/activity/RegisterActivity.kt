package com.taxi1.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.taxi1.Auth.SignUpAuth
import com.taxi1.R
import com.taxi1.Retrofit.Response.RegisterResponse
import com.taxi1.ViewResponse.IRegisterView
import com.taxi1.activity.Controller.IRegisterController
import com.taxi1.activity.Controller.RegisterController
import com.taxi1.databinding.ActivityRegisterBinding
import com.taxi1.utils.Utils

class RegisterActivity : BaseActivity() ,View.OnClickListener, IRegisterView {
    var controller: IRegisterController? = null
    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setlistners()
    }

    private fun setlistners() {

        binding.btnRegister.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnRegister -> {
                signupUser()
            }
            R.id.btnLogin -> {
                finish()
            }
        }
    }

    private fun signupUser() {

        val authSuccessful: Boolean = SignUpAuth.authenticate(
            binding.edtUserName.text.toString(),
            binding.edtEmail.text.toString(),
            binding.edtPhone.text.toString(),
            binding.edtpassword.text.toString(),
            binding.etdriverId?.text.toString(),
            binding.edtPlateNumber?.text.toString(),
            binding.edtConfirmpassword.text.toString(), this)
        if (authSuccessful) {

            controller = RegisterController(this, this)
            controller!!.register(
                binding.edtUserName.text.toString(),
                binding.edtEmail.text.toString(),
                binding.edtPhone.text.toString(),
                binding.etdriverId?.text.toString(),
                binding.edtPlateNumber?.text.toString(),
                binding.edtConfirmpassword.text.toString())
        }
    }

    override fun onRegister(register: RegisterResponse) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
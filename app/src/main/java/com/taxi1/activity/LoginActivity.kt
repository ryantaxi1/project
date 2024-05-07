package com.taxi1.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.taxi1.Auth.LoginAuth
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.ViewResponse.ILoginView
import com.taxi1.activity.Controller.ILoginController
import com.taxi1.activity.Controller.LoginController
import com.taxi1.databinding.ActivityLoginBinding
import com.taxi1.utils.Utils

class LoginActivity : BaseActivity(),View.OnClickListener,ILoginView {
    private lateinit var binding : ActivityLoginBinding
    var controller: ILoginController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListners()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    }

    private fun setListners() {
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvForgotPassword ->
            {
                startActivity(Intent(this,ForgotPasswordActivity::class.java))
            }
            R.id.btnRegister ->
            {
                startActivity(Intent(this,RegisterActivity::class.java))
            }
            R.id.btnLogin ->
            {
                loginUser()
                //startActivity(Intent(this,DashboardActivity::class.java))
            }
        }
    }

    private fun loginUser() {

        val authSuccessful: Boolean = LoginAuth.authenticate(
            binding.edtemail.text.toString(),
            binding.edtpwd.text.toString(), this)
        if (authSuccessful) {

            controller = LoginController(this, this)
            controller!!.login(binding.edtemail.text.toString(),
                binding.edtpwd.text.toString())
        }
    }

    override fun onLogin(login: LoginResponse?) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onFailLogin(comman: CommanResponse?) {

    }
}
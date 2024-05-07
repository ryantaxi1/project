package com.android.taxi1in_carapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.activity.Controller.ForgotPasswordController
import com.android.taxi1in_carapp.activity.Controller.IForgotPasswordController
import com.android.taxi1in_carapp.databinding.ActivityNewResetPasswordBinding
import com.taxi1.ViewResponse.IForgotPasswordView
import com.taxi1.utils.Constants.EMAIL
import com.taxi1.utils.InternetCheck

class NewResetPasswordActivity : BaseActivity(), IForgotPasswordView {

    lateinit var binding: ActivityNewResetPasswordBinding
    var controller: IForgotPasswordController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = ForgotPasswordController(this, this)

        initView()
    }

    private fun initView() {
        binding.llBtnSendCode?.setOnClickListener {
            forgotPassword()
        }

        binding.tvFindMore?.setOnClickListener {
            val intent = Intent(this, NoticeBoardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun forgotPassword() {

        val authSuccessful: Boolean = newAuthenticate(binding.etEmail?.text.toString())
        if (authSuccessful) {
            binding.tvErrorCompleteAllFields?.visibility = View.GONE
            binding.etEmail?.background = ContextCompat.getDrawable(this@NewResetPasswordActivity, R.drawable.bg_rounded_edit_text_white)

            controller?.forgotPassword(binding.etEmail?.text.toString())
        }
    }

    fun newAuthenticate(
        email: String,
    ): Boolean {
        var check = false

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.tvErrorCompleteAllFields?.text = "Please enter valid email"

            binding.etEmail?.background = ContextCompat.getDrawable(this@NewResetPasswordActivity, R.drawable.bg_edittext_field_error)

            check = false
        }
        else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(this@NewResetPasswordActivity)!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(this@NewResetPasswordActivity)
                check = false
            }
        }
        return check
    }

    override fun onForgot(login: CommonResponse?) {
        val bundle = Bundle()
        bundle.putString(EMAIL, binding.etEmail?.text.toString())
        val intent = Intent(this, NewOtpVerifyResetPasswordActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
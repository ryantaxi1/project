package com.android.taxi1in_carapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.CommonResponse
import com.android.taxi1in_carapp.Retrofit.newresponse.SigninResponse
import com.taxi1.ViewResponse.ILoginView
import com.android.taxi1in_carapp.activity.Controller.ILoginController
import com.android.taxi1in_carapp.activity.Controller.LoginController
import com.android.taxi1in_carapp.databinding.ActivitySigninNewBinding
import com.taxi1.utils.InternetCheck
import com.taxi1.utils.StoreUserData

class NewSignInActivity : BaseActivity(), ILoginView {
    lateinit var binding: ActivitySigninNewBinding

    //    private var newDashboardActivity: NewDashboardActivity? = null
    lateinit var storedata: StoreUserData
    var controller: ILoginController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        newDashboardActivity = activity as NewDashboardActivity?
//        storedata = StoreUserData(requireContext())

        binding = ActivitySigninNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storedata = StoreUserData(this)

        initClick()
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSigninNewBinding.inflate(layoutInflater)
        return binding.root
    }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = LoginController(requireContext(), this)

        initClick()
    }*/

    private fun initClick() {

        binding.tvFindMore?.setOnClickListener {
            val intent = Intent(this, NoticeBoardActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
//            val authSuccessful: Boolean = LoginAuth.authenticate(binding.etEmail.text.toString(), binding.etPassword.text.toString(), this)

            val authSuccessful: Boolean = newAuthenticateSignIn(binding.etPhoneNo?.text.toString(), binding.etPassword.text.toString())

            if (authSuccessful) {
                binding.tvErrorCompleteAllFields?.visibility = View.GONE
                binding.etPhoneNo?.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etPassword.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_rounded_edit_text_white)

                controller = LoginController(this, this)
                controller!!.login(binding.etPhoneNo?.text.toString(), binding.etPassword.text.toString())
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, NewResetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.llSignup?.setOnClickListener {
            val intent = Intent(this, NewSignUpActivity::class.java)
            startActivity(intent)
        }

    }

    fun newAuthenticateSignIn(
        phoneNumber: String,
        password: String,
    ): Boolean {
        var check = false

        if (phoneNumber.isEmpty() || phoneNumber.length != 10) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etPhoneNo?.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_edittext_field_error)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields?.text = "Please enter valid phone no"

            check = false
        }
        else if (password.isEmpty()) {
            binding.tvErrorCompleteAllFields?.visibility = View.VISIBLE
            binding.etPhoneNo?.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignInActivity, R.drawable.bg_edittext_field_error)

            binding.tvErrorCompleteAllFields?.text = "Please enter your password"

            check = false
        }
        else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(this@NewSignInActivity)!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(this@NewSignInActivity)
                check = false
            }
        }
        return check
    }

    override fun onLogin(login: SigninResponse?) {

        val intent = Intent(this, NewDashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onFailLogin(comman: CommonResponse?) {

    }
}
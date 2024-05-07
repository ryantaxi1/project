package com.android.taxi1in_carapp.activity

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.SignupResponse
import com.taxi1.ViewResponse.IRegisterView
import com.android.taxi1in_carapp.activity.Controller.IRegisterController
import com.android.taxi1in_carapp.activity.Controller.RegisterController
import com.android.taxi1in_carapp.databinding.ActivityNewSignupBinding
import com.taxi1.utils.InternetCheck

class NewSignUpActivity : BaseActivity(), IRegisterView {

    lateinit var binding: ActivityNewSignupBinding
    var controller: IRegisterController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        newDashboardActivity = activity as NewDashboardActivity?
//        storedata = StoreUserData(requireContext())

        controller = RegisterController(this, this)

        binding = ActivityNewSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Load the custom font using the AndroidX library
        val customTypeface = TypefaceCompat.createFromResourcesFontFile(
            this,
            this.resources,
            resources.obtainTypedArray(R.array.fonts).getIndex(0),
            "font/euclid_square_semi_bold.ttf",
            0,
            0
        )


        val wordToSpan: Spannable = SpannableString(binding.tvTerms.text)
        wordToSpan.setSpan(
            customTypeface,
            15,
            43,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTerms.text = wordToSpan

        initClick()
    }

    private fun initClick() {

        binding.tvFindMore.setOnClickListener {
            val intent = Intent(this, NoticeBoardActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, NewSignInActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.btnSignup.setOnClickListener {
//            signupUser()
            val authSuccessful: Boolean = newAuthenticate(
                binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etMobileNo.text.toString(),
                binding.etdriverId?.text.toString(),
                binding.etPlateNo?.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirmPassword.text.toString(),
                binding.cbTerms.isChecked
            )

            if(authSuccessful){
                binding.tvErrorCompleteAllFields.visibility = GONE
                binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
                binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

                controller = RegisterController(this, this)
                controller!!.register(
                    binding.etFirstName.text.toString(),
                    binding.etLastName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etMobileNo.text.toString(),
                    binding.etdriverId.text.toString(),
                    "T"+binding.etPlateNo.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etConfirmPassword.text.toString()
                )

//                Toast.makeText(this, "Register Successfully test", Toast.LENGTH_SHORT).show()
            }else{
//                Toast.makeText(this, "Register not Successfully test", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun newAuthenticate(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        driverId: String,
        plateNumber: String,
        password: String,
        confirmPassword: String,
        privacyPolicy: Boolean
    ): Boolean {
        var check = false

        if (firstName.isEmpty()) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter first name"

            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.firstname_err_msg)
            )*/
            check = false
        }else if (lastName.isEmpty()) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter last name"

            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.lastname_err_msg)
            )*/
            check = false
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter valid email"

            /*if (email.isEmpty()) {
                Notify.alerterRed(
                    activity,
                    activity.resources.getString(R.string.enter_email_err_msg)
                )
                check = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Notify.alerterRed(
                    activity,
                    activity.resources.getString(R.string.email_length_err_msg)
                )
                check = false
            }*/

            check = false
        } else if (phoneNumber.isEmpty() || phoneNumber.length != 10) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter valid phone no"

            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_phone_number)
            )*/
            check = false
        }

        /*   else if (phoneNumber.length < 11) {
           Notify.alerterRed(
               activity, activity.resources.getString(R.string.phone_number_not_valid)
           )
           check = false
       }*/
        else if (driverId.isEmpty()) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)


            binding.tvErrorCompleteAllFields.text = "Please enter driverId"
            /* Notify.alerterRed(
                 activity,
                 activity.resources.getString(R.string.enter_driver)
             )*/
            check = false
        } else if (plateNumber.isNullOrEmpty()) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter 4 digit platNo"
            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_plate_number)
            )*/
            check = false
        }

        /*else if (plateNumber.length > 5) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.enter_plate_valid_number)
            )
            check = false
        }*/
        else if (password.isNullOrEmpty() || password.length < 8) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)

            binding.tvErrorCompleteAllFields.text = "Please enter password at lease 8 character"
            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.password_err_msg)
            )*/
            check = false
        } else if (confirmPassword.isEmpty() || password != confirmPassword) {
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.etFirstName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etLastName.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etEmail.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etMobileNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etdriverId.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPlateNo.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_rounded_edit_text_white)
            binding.etConfirmPassword.background = ContextCompat.getDrawable(this@NewSignUpActivity, R.drawable.bg_edittext_field_error)

            binding.tvErrorCompleteAllFields.text = "Please enter same password and confirm password"

            /*Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.confirm_password_err_msg)
            )*/
            check = false
        } /*else if (password != confirmPassword) {
            Notify.alerterRed(
                activity,
                activity.resources.getString(R.string.password_match_err_msg)
            )
            check = false
        }*/
        else if (!privacyPolicy){
            binding.tvErrorCompleteAllFields.visibility = VISIBLE
            binding.tvErrorCompleteAllFields.text = "Please agree terms and privacy"

            check = false
        }
        else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(this@NewSignUpActivity)!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(this@NewSignUpActivity)
                check = false
            }
        }
        return check
    }


    override fun onRegister(register: SignupResponse) {
        binding.llSignupForm.visibility = GONE
        binding.llSignupComplete.visibility = VISIBLE
    }

}
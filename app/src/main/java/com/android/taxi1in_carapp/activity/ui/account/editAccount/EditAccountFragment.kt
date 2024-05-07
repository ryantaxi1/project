package com.android.taxi1in_carapp.activity.ui.account.editAccount

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.activity.Controller.EditProfileController
import com.android.taxi1in_carapp.activity.Controller.IEditProfileController
import com.android.taxi1in_carapp.databinding.FragmentEditAccountBinding
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.utils.Constants
import com.taxi1.utils.InternetCheck
import com.taxi1.utils.UpdateAccountDetails
import com.taxi1.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class EditAccountFragment : Fragment(), IProfileView {
    private val TAG = "EditAccountFragment"
    lateinit var binding: FragmentEditAccountBinding
    var userData: GetProfileResponse.Result? = null
    var editprofileController: IEditProfileController? = null
    private val permissionCode = 10
    private val actionRequestGallery = 200
    private var selectedImageUri: Uri? = null
    private var pickImageLogo: File? = null
    private var updateAccountDetailsListener: UpdateAccountDetails? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userData = bundle.getSerializable(Constants.CURRENT_USER_DATA) as GetProfileResponse.Result?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editprofileController = EditProfileController(requireContext(), this)

        if (userData != null) {

            /*if (userData?.image != null) {
                Glide.with(this)
                    .load(userData?.image).placeholder(R.drawable.ic_profile)
                    .into(binding.civProfile!!)

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
            if (!userData?.lastName.isNullOrBlank()) {
                binding.tvProfileShortName?.append("${userData?.lastName!![0]}")
            }

            binding.civProfile?.visibility = View.GONE
            binding.tvProfileShortName?.visibility = View.VISIBLE
            binding.tvProfileShortName?.isAllCaps = true

            binding.tvName?.text = userData?.firstName + " " + userData?.lastName
            binding.etFirstName?.setText(userData?.firstName)
            binding.etLastName?.setText(userData?.lastName)
            binding.etPhoneNo?.setText(userData?.phone)
            binding.etEmail?.setText(userData?.email)

            binding.tvName?.text = userData?.firstName + " " + userData?.lastName
            binding.etFirstName?.setText(userData?.firstName)
            binding.etLastName?.setText(userData?.lastName)
            binding.etPhoneNo?.setText(userData?.phone)
            binding.etEmail?.setText(userData?.email)
        }

        initView()
    }

    private fun initView() {

        /*binding.rlProfileImageView?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ), permissionCode
                )
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ), permissionCode
                )
            }
        }*/

        binding.btnSave?.setOnClickListener {

            val authSuccessful: Boolean = authenticate(
                binding.etFirstName?.text.toString(),
                binding.etLastName?.text.toString(),
                binding.etEmail?.text.toString(),
                binding.etPhoneNo?.text.toString(),
            )

            if (authSuccessful) {
                binding.etFirstName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                binding.etLastName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                binding.etEmail?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                binding.etPhoneNo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)

                editprofileController?.editProfile(
                    binding.etFirstName?.text.toString(),
                    binding.etLastName?.text.toString(),
                    binding.etPhoneNo?.text.toString(),
                    binding.etEmail?.text.toString(),
                    pickImageLogo
                )

            }
        }
    }

    fun authenticate(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
    ): Boolean {
        var check = false

        if (firstName.isEmpty()) {
            binding.etFirstName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etLastName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etEmail?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etPhoneNo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)

            Utils.showToast(requireContext(), "Please enter firstname")

            check = false
        } else if (lastName.isEmpty()) {
            binding.etFirstName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etLastName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etEmail?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etPhoneNo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)

            Utils.showToast(requireContext(), "Please enter lastName")

            check = false
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etFirstName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etLastName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etEmail?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)
            binding.etPhoneNo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            Utils.showToast(requireContext(), "Please enter valid email ")

            check = false
        } else if (phoneNumber.isEmpty() || phoneNumber.length != 10) {
            binding.etFirstName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etLastName?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etEmail?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
            binding.etPhoneNo?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_field_error)

            Utils.showToast(requireContext(), "Please enter valid phone number")

            check = false
        } else {
            if (InternetCheck.getInstance()?.isNetworkAvailable(requireContext())!!) {
                check = true
            } else {
                InternetCheck.getInstance()?.alertDialog(requireActivity())
                check = false
            }
        }
        return check
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startActivityForResult(chooser, actionRequestGallery)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {

                // get image from storage
                getImageFromGallery()
            } else {
                Utils.showToast(requireContext(), "Permission not granted!")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            actionRequestGallery -> if (resultCode === AppCompatActivity.RESULT_OK) {

                selectedImageUri = data?.data

                selectedImageUri?.let {
                    pickImageLogo = uriToFile(requireContext(), selectedImageUri!!)
                }

                binding.tvProfileShortName?.visibility = GONE
                binding.civProfile?.visibility = VISIBLE

                binding.civProfile?.setImageURI(selectedImageUri)

                Log.d(TAG, "onActivityResult Selected image gallery: $pickImageLogo")
            }
        }
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        var inputStream: InputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var file: File? = null

        try {
            val contentResolver: ContentResolver = context.contentResolver
            val displayName = getDisplayName(context, uri)

            inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                file = File(context.cacheDir, displayName)
                fileOutputStream = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    fileOutputStream.write(buffer, 0, read)
                }

                return file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    fun getDisplayName(context: Context, uri: Uri): String {
        val displayName: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    it.getString(columnIndex)
                } else {
                    null
                }
            }
        } else {
            uri.lastPathSegment
        }

        return displayName ?: "temp_file"
    }

    fun setUpdateAccountListener(listener: UpdateAccountDetails) {
        updateAccountDetailsListener = listener
    }

    override fun onResponse(response: GetProfileResponse.Result?) {
        Log.d(TAG, "onResponse send call get profile event: ")
        updateAccountDetailsListener?.callGetProfileApi(true)
    }

}
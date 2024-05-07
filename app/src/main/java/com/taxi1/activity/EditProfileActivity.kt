package com.taxi1.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.taxi1.Auth.EditProfileAuth
import com.taxi1.Auth.SignUpAuth
import com.taxi1.R
import com.taxi1.Retrofit.Response.ProfileResponse
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.activity.Controller.EditProfileController
import com.taxi1.activity.Controller.IEditProfileController
import com.taxi1.activity.Controller.IProfileController
import com.taxi1.activity.Controller.ProfileController
import com.taxi1.databinding.ActivityEditProfileBinding
import com.taxi1.utils.ImageFilePath
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : BaseActivity(), View.OnClickListener,IProfileView {
    private lateinit var binding :ActivityEditProfileBinding
    val TAG = "EditProfileActivity"
    var getProfileController: IProfileController? = null
    var editprofileController: IEditProfileController? = null
    private var mImageBitmap: Bitmap? = null
    private var mCurrentPhotoPath: String? = null
    private val GALLERY = 1
    private var REQUEST_CAMERA: Int = 0
    private var imageUri: Uri? = null
    var isEdit=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Edit Profile"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setlistners()

        getProfileController = ProfileController(this, this)
        getProfileController!!.getProfile()

    }

    private fun setlistners() {

        binding.toolbar.ivBack.setOnClickListener(this)
        binding.civProfile.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                finish()
            }

            R.id.civProfile -> {
                requestPermissions()
            }

            R.id.btnSave -> {

                UpdateProfile()
            }
        }
    }

    private fun UpdateProfile() {

        val authSuccessful: Boolean = EditProfileAuth.authenticate(
            binding.edtUserName.text.toString(),
            binding.edtEmail.text.toString(),
            binding.edtPhone.text.toString(),
            binding.edtPlateNumber?.text.toString(),this)
        if (authSuccessful) {
            if (imageUri!=null) {
                val image: String = ImageFilePath.getPath(this, imageUri!!)!!
                editprofileController = EditProfileController(this, this)
                editprofileController!!.editProfile(
                   binding.edtUserName.text.toString(),binding.edtEmail.text.toString(),
                    binding.edtPhone.text.toString(),binding.edtPlateNumber?.text.toString(),
                    image
                )
                isEdit = true
            }else{
                val image: String = ""
                editprofileController = EditProfileController(this, this)
                editprofileController!!.editProfile(
                    binding.edtUserName.text.toString(),binding.edtEmail.text.toString(),
                    binding.edtPhone.text.toString(),binding.edtPlateNumber?.text.toString(),
                    image
                )
                isEdit = true
            }
        }
    }

    private fun requestPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.d(TAG, "All permissions are granted")
                        selectImage()

                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) { // show alert dialog navigating to Settings
//openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { Log.d(TAG, "Permission Error!") }
            .onSameThread()
            .check()
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            getString(R.string.option_camera),
            getString(R.string.option_gallery),
            getString(R.string.cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { dialog, item ->
            if (options[item] == getString(R.string.option_camera)) {
                takePhotoFromCamera()
            } else if (options[item] == getString(R.string.option_gallery)) {
                choosePhotoFromGallary()
            } else if (options[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.i(TAG, "IOException")
            }
            if (photoFile != null) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                } else {
                    val file = File(Uri.fromFile(photoFile).path)
                    val photoUri = FileProvider.getUriForFile(this, packageName + ".provider", file)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        Uri.parse(mCurrentPhotoPath)
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (mImageBitmap != null) {
                    imageUri = getImageUri(mImageBitmap!!)
                    startCropImageActivity(getImageUri(mImageBitmap!!))
                }
            } else if (requestCode == GALLERY) {
                if (data != null) {
                    val selectedImage = data.data
                    imageUri = selectedImage
                    startCropImageActivity(selectedImage)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                result.uri
                Glide.with(binding.civProfile)
                    .load(result.uri)
                    .into(binding.civProfile)
            }
        } else {
            Log.d(TAG, "onActivityResult: ==== error ===")
        }
    }

    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "" + System.currentTimeMillis(), null)
        return Uri.parse(path)
    }

    private fun startCropImageActivity(imageUri: Uri?) {
        val intent: Intent = CropImage.activity(imageUri!!)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setOutputCompressQuality(50)
            .getIntent(this)
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onResponse(profile: ProfileResponse.Result?) {
        if (isEdit==true){
            finish()
        }else {
            try {
                Glide.with(this)
                    .load(profile!!.image).placeholder(R.drawable.ic_profile)
                    .centerCrop()
                    .into(binding.civProfile)
                //Log.e(TAG, "imagelink: "+ImageList.get(position))
            } catch (e: Exception) {
                Log.e("TAG", "onBindViewHolder: ")
            }
            try {
                binding.edtUserName.setText(profile!!.username)
                binding.edtEmail.setText(profile.email)
                binding.edtPhone.setText(profile.phone)
                binding.edtPlateNumber?.setText(profile.plate_number)
                binding.etdriverId?.setText(profile.driver_id)

            } catch (e: Exception) {
                Log.e(TAG, "ongetProfile: " + e)
            }
        }
    }
}
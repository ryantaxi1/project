package com.taxi1.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.activity.NewSignInActivity
import com.google.gson.Gson
//import com.android.taxi1in_carapp.activity.LoginActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    var progressDialog: ProgressDialog? = null
    fun isTablet(context: Context): Boolean {
        val xlarge =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
        val large =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    fun resizeBitmapImageFn(
        bmpSource: Bitmap, maxResolution: Int,
    ): Bitmap? {
        val iWidth = bmpSource.width
        val iHeight = bmpSource.height
        var newWidth = iWidth
        var newHeight = iHeight
        var rate = 0.0f
        if (iWidth > iHeight) {
            if (maxResolution < iWidth) {
                rate = maxResolution / iWidth.toFloat()
                newHeight = (iHeight * rate).toInt()
                newWidth = maxResolution
            }
        } else {
            if (maxResolution < iHeight) {
                rate = maxResolution / iHeight.toFloat()
                newWidth = (iWidth * rate).toInt()
                newHeight = maxResolution
            }
        }
        return Bitmap.createScaledBitmap(
            bmpSource, newWidth, newHeight, true)
    }

    fun showProgress(context: Context) {

        if (!isProgressShowing()) {
            progressDialog = ProgressDialog(context)
            try {
                progressDialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressDialog?.setCancelable(false)
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog?.setContentView(R.layout.progressdailog)
        }
    }

    fun isProgressShowing(): Boolean {
        if (progressDialog == null)
            return false
        else {
            return progressDialog!!.isShowing
        }
    }

    fun dismissProgress() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss() else Log.i(
                "Dialog",
                "already dismissed"
            )
        } catch (e: Exception) {
        }
    }

    fun showToast(activity: Context, message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showLog(msg: String?, value: String?) {
        Log.e(msg, value.toString())
    }

    @SuppressLint("Range")
    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                null
            }
        }
    }

    fun cleardata(context: Context) {
        val storedata = StoreUserData(context)
        storedata.setBoolean(Constants.ISLOGGEDIN,false)
        storedata.clearData(context)
    }

    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        Log.d("TAG",
            "getAddressFromLatLng: $latitude long $longitude")
        try {
            val geocoder = Geocoder(context!!, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.size > 0) {
                val address = addresses[0]
                if (address != null) {
                    if (address.getAddressLine(0) != null) {
                        return address.getAddressLine(0)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("tag", e.message!!)
        }
        return ""
    }

    fun showPositiveAlertDialog(context: Context, title: String, message: String): AlertDialog {
        val builder = AlertDialog.Builder(context)

        // Set the dialog title and message
        builder.setTitle(title)
        builder.setMessage(message)

        // Set a positive button and its click listener
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the dialog when the button is clicked
            context.startActivity(Intent(context, NewSignInActivity::class.java))
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    fun showPositiveNegativeAlertDialog(context: Context, title: String, positiveText: String, negativeString: String, message: String, callback: CustomDialogInterface): AlertDialog {
        val builder = AlertDialog.Builder(context)

        // Set the dialog title and message
        builder.setTitle(title)
        builder.setMessage(message)

        // Set a positive button and its click listener
        builder.setPositiveButton(positiveText) { dialog, _ ->
            callback.positiveClick(dialog)
        }

        // Set a negative button and its click listener
        builder.setNegativeButton(negativeString) { dialog, _ ->
            callback.negativeClick(dialog)
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    fun getLocationName(context: Context, location : Location): String {
        var address: Address
        var addressStr = ""
        try {
            val myLocation = Geocoder(context, Locale.getDefault())
            val myList: List<Address>? = myLocation.getFromLocation(location.latitude, location.longitude, 1)
            address = myList!![0] as Address
            addressStr = ""

            for (index in 0 until myList.size) {
                address = myList[index]
                addressStr += "${address.getAddressLine(0)}, "
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        return addressStr
    }

    // dataclass to JSON
    fun Any.toJson(): String {
        return Gson().toJson(this)
    }

    // Convert JSON string to object
    inline fun <reified T> String.fromJson(): T {
        return Gson().fromJson(this, T::class.java)
    }
}
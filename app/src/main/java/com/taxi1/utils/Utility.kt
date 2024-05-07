package com.taxi1.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.taxi1.R
import com.taxi1.Retrofit.Response.AllTripRecordsResponse

object Utility {

    fun showDialog(
        context: Context,
        title: String = "METStech",
        message: String = "Empty!",
        positiveButton: String,
        negativeButton: String,
        listener: DialogView.ButtonListener):DialogView {
        var dialogView = DialogView(context)
        dialogView.setTitle(title)
        dialogView.setMessage(message)
        dialogView.setListener(object : DialogView.ButtonListener {
            override fun onNegativeButtonClick(dialog: AlertDialog) {
                listener.onNegativeButtonClick(dialog)
            }

            override fun onPositiveButtonClick(dialog: AlertDialog) {
                listener.onPositiveButtonClick(dialog)
            }
        })
        dialogView.setPositiveButtonText(positiveButton)
        dialogView.setNegativeButtonText(negativeButton)
        dialogView.show()
        return  dialogView
    }

    fun showTripDataDialog(
        activity: Activity,
        tripData: AllTripRecordsResponse.Result.ContinueTripArray,
        continueTripList: AllTripRecordsResponse.Result
    ) {
        try {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.trip_details_view)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)

            val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
            val txtUserName = dialog.findViewById<TextView>(R.id.txtUserName)
            val txtUserId = dialog.findViewById<TextView>(R.id.txtUserId)
            val txtDateTime = dialog.findViewById<TextView>(R.id.txtDateTime)
            val txtTripId = dialog.findViewById<TextView>(R.id.txtTripId)
            val txtTravelAmount = dialog.findViewById<TextView>(R.id.txtTravelAmount)
            val txtWaitingCharge = dialog.findViewById<TextView>(R.id.txtWaitingCharge)
            val txtCarType = dialog.findViewById<TextView>(R.id.txtCarType)
            val txtCurrentLocation = dialog.findViewById<TextView>(R.id.txtCurrentLocation)
            val txtStartLocation = dialog.findViewById<TextView>(R.id.txtStartLocation)
            val txtEndLocation = dialog.findViewById<TextView>(R.id.txtEndLocation)


//            txtUserName.text = tripData.userName.toString()
//            txtUserId.text = tripData.userID.toString()
            txtDateTime.text = tripData.tripDateTime.toString()
            txtTripId.text = tripData.tripId.toString()
            txtTravelAmount.text = tripData.travelCharge.toString()
            txtWaitingCharge.text = tripData.waitingCharge.toString()
            txtCarType.text = tripData.carType.toString()
            txtCurrentLocation.text = tripData.triplocation.toString()
            txtStartLocation.text = continueTripList.startLocation.toString()
            txtEndLocation.text = continueTripList.stopLocation.toString()

            ivClose.setOnClickListener {
                dialog.dismiss()
            }

//            val imgClose = dialog.findViewById<ImageView>(R.id.imgClose)
//            val llLogout = dialog.findViewById<LinearLayout>(R.id.llLogout)
//            val llDeactivateAcc = dialog.findViewById<LinearLayout>(R.id.llDeactivateAcc)
//            val llNotificationStatus = dialog.findViewById<LinearLayout>(R.id.llNotificationStatus)
//            val imgNotification = dialog.findViewById<ImageView>(R.id.imgNotification)
//
//            if (SharedPrefUserData(activity).getUserData().status == "0") {
//                txtAccountStatus.text = "Activate Account"
//            }
//
            /*imgClose.setOnClickListener {
                dialog.dismiss()
            }
            imgBack.setOnClickListener {
                dialog.dismiss()
            }*/
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
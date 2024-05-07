package com.taxi1.broadcast

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.taxi1.utils.DialogView
import com.taxi1.utils.Utility

class LocationProviderChangedReceiver : BroadcastReceiver() {

    internal var isGpsEnabled: Boolean = false
    internal var isNetworkEnabled: Boolean = false
    internal var dialogView: DialogView? = null

    companion object {
        private val TAG = "LocationProviderChanged"
    }

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let { act ->
            if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                Log.e(TAG, "Location Providers changed, is GPS Enabled: " + isGpsEnabled)

                //Start your Activity if location was enabled:
                if (!isGpsEnabled) {
                    Log.e(TAG, "location dialog is showing: ${dialogView?.isShowing()}")
                    if (dialogView == null || !dialogView?.isShowing()!!) {
                        locationDialog(context)
                    }
                }
            }
        }
    }

    fun locationDialog(context:Context){
        dialogView = Utility.showDialog(
            context,
            "Location",
            "GPS is not enabled in device, it's must be enabled to connect with device.",
            "Enable",
            "",
            object : DialogView.ButtonListener {
                override fun onNegativeButtonClick(dialog: AlertDialog) {
                    dialog.dismiss()
                }

                override fun onPositiveButtonClick(dialog: AlertDialog) {
                    dialog.dismiss()
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
        )
    }
}
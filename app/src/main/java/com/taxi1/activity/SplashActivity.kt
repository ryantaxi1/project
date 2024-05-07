package com.taxi1.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.taxi1.databinding.ActivitySplashBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.CustomDialogInterface
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils

class SplashActivity : BaseActivity() {
    lateinit var storedata : StoreUserData

    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        storedata = StoreUserData(this)


        if (ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Utils.showPositiveNegativeAlertDialog(
                this,
                "Permission Dialog",
                "Grant",
                "Deny",
                "This app collects location data when the SOS button is long pressed. The location is shared with our server to track the user's location. During a Skype SOS call, the app will share location data in the background with the SOS call receiver, allowing them to track and locate the app user. And set the allow all the time.",
                object : CustomDialogInterface {
                    override fun positiveClick(dialog: DialogInterface) {
                        dialog.dismiss()
                        ActivityCompat.requestPermissions(
                            this@SplashActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                            2
                        )
                    }

                    override fun negativeClick(dialog: DialogInterface) {
                        Utils.showToast(this@SplashActivity, "Permission not granted.")
                        dialog.dismiss()
                        Handler().postDelayed({
                            finish()
                        }, 1000)
                    }
                })

        }else{
            if(storedata.getBoolean(Constants.ISLOGGEDIN))
            {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            else{

                Handler().postDelayed({

                    var intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 3000)
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if(storedata.getBoolean(Constants.ISLOGGEDIN))
                {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else{

                    Handler().postDelayed({

                        var intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }, 3000)
                }
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    finish()
                }, 1000)
            }
        }
    }
}
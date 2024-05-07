package com.android.taxi1in_carapp.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.taxi1in_carapp.databinding.ActivityNewSplashBinding
import com.taxi1.utils.Constants
import com.taxi1.utils.CustomDialogInterface
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils

class SplashActivity : BaseActivity() {
    private val TAG = this@SplashActivity.javaClass.simpleName
    lateinit var storedata : StoreUserData

//    lateinit var binding: ActivitySplashBinding
    lateinit var binding: ActivityNewSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
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
                            finishAffinity()
                        }, 1000)
                    }
                })

        }else{

            /*Handler().postDelayed({

                var intent = Intent(this, NewDashboardActivity::class.java)
                startActivity(intent)
                finish()

            }, 3000)*/

            if(storedata.getBoolean(Constants.ISLOGGEDIN))
            {
//                val intent = Intent(this, DashboardActivity::class.java)
                val intent = Intent(this, NewDashboardActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            else{

                Handler().postDelayed({
                    Log.d(TAG, "Have Permission newSignActivity  start: ")
                    var intent = Intent(this, NewSignInActivity::class.java)
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

                /*Handler().postDelayed({

                    var intent = Intent(this, NewDashboardActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 3000)*/

                if(storedata.getBoolean(Constants.ISLOGGEDIN))
                {
//                    val intent = Intent(this, DashboardActivity::class.java)
                    val intent = Intent(this, NewDashboardActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else{

                    Handler().postDelayed({
                        Log.d(TAG, "Start From Permission Result Handler newSignActivity  start: ")
                        var intent = Intent(this, NewSignInActivity::class.java)
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
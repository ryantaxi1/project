/*
package com.android.taxi1in_carapp.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.activity.ChangePasswordActivity
import com.android.taxi1in_carapp.activity.ui.home.HomeFragment
import com.android.taxi1in_carapp.databinding.ActivityDashboardBinding
import com.taxi1.utils.*


class DashboardActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setSupportActionBar(binding.appBarDashboard.toolbar)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
       */
/* val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)*//*

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

       */
/* val icon = BitmapFactory.decodeResource(resources, R.drawable.menu_new) //Converting drawable into bitmap

        val new_icon: Bitmap = Utils.resizeBitmapImageFn(icon, 20)!! //resizing the bitmap

        val d: Drawable = BitmapDrawable(resources, new_icon) //Converting bitmap into drawable

        drawerToggleDelegate!!.setActionBarUpIndicator(d,
            R.string.app_name)*//*




        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            val view: View? = this@DashboardActivity.currentFocus
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.menu_new)

        val storedata = StoreUserData(this)
        val meterPermission = storedata.getString(Constants.METER_PERMISSION)
        Log.d("TAG", "meterPermission: ===" + meterPermission)


        */
/**
         * TODO When upload new version then simple comment the condition that is taximeter option.
         *//*

        if(meterPermission.equals("1"))
        {
            binding.linTaxiMeter?.visibility = View.VISIBLE
        }
        else{
            binding.linTaxiMeter?.visibility = View.GONE
        }

        binding.linTaxiMeter?.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@DashboardActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2)
            }else{
                checkGPS()
            }
        }

        binding.linHome.setOnClickListener {
            //startActivity(Intent(this,DashboardActivity::class.java))

            val fragmentselected = HomeFragment()
            supportFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment_content_dashboard,
                fragmentselected,
                fragmentselected.javaClass.simpleName
            ).addToBackStack(null)
                .commit()
            binding.drawerLayout.close()
        }

        binding.linEditProfile.setOnClickListener {
            startActivity(Intent(this,EditProfileActivity::class.java))
        }

        binding.linsoshistory.setOnClickListener {
            startActivity(Intent(this, SOSHistoryActivity::class.java))
        }

        binding.linAboutUs.setOnClickListener {
            val intent = Intent(this,StaticPageActivity::class.java)
            intent.putExtra(AppConstants.PAGE,AppConstants.PAGE_ABOUT_US)
            startActivity(intent)
        }
        binding.linPrivacyPolicy.setOnClickListener {
            val intent = Intent(this,StaticPageActivity::class.java)
            intent.putExtra(AppConstants.PAGE,AppConstants.PAGE_PRIVACY_POLICY)
            startActivity(intent)
        }
        binding.linTnC.setOnClickListener {
            val intent = Intent(this,StaticPageActivity::class.java)
            intent.putExtra(AppConstants.PAGE,AppConstants.PAGE_TERMS_CONDITIONS)
            startActivity(intent)
        }

        binding.linChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.linLogout.setOnClickListener {
            binding.drawerLayout.close()

            val storedata = StoreUserData(this)
            storedata.setBoolean(Constants.ISLOGGEDIN,false)
            storedata.clearData(this)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.ivClose.setOnClickListener {
            binding.drawerLayout.close()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkGPS() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(lm)) {
            Utility.showDialog(
                this,
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
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            )
        }else{
            */
/*val mainActivity = Intent(this@DashboardActivity, MainActivity::class.java)
            startActivity(mainActivity)
            finish()*//*


            */
/*CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val mainActivity = Intent(this@DashboardActivity, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }*//*

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                checkGPS()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}*/

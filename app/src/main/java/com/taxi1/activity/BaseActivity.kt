package com.taxi1.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager.BadTokenException
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.taxi1.R
import com.taxi1.utils.Utils

open class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    fun startActivity(cls: Class<*>?) {
        startActivity(Intent(baseContext, cls))
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        /*if (Utils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)*/
    }

    @IdRes
    protected fun getFragmentContainer(): Int {
        return 0
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
//        val sharedPreference = SharedPreference(this)
        super.onPause()
    }


    fun showLoader() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setCancelable(false)
            progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        if (!isFinishing) try {
            progressDialog!!.show()
            progressDialog!!.setContentView(R.layout.progressdialog)
        } catch (e: BadTokenException) {
            e.printStackTrace()
        }
    }

    fun hideLoader() {
        try {
            if (!isFinishing && progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
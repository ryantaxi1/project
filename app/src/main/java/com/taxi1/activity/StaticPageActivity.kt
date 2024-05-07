package com.taxi1.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.taxi1.Retrofit.Response.StaticPageResponse
import com.taxi1.ViewResponse.IStaticPageView
import com.taxi1.activity.Controller.IStaticPageController
import com.taxi1.activity.Controller.StaticPageController
import com.taxi1.databinding.ActivityStaticPageBinding
import com.taxi1.utils.AppConstants
import com.taxi1.utils.Utils

class StaticPageActivity : BaseActivity(),IStaticPageView {
    private lateinit var binding : ActivityStaticPageBinding
    val TAG ="StaticPageActivity"
    var controller: IStaticPageController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStaticPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        var title = ""
        if (intent.hasExtra(AppConstants.PAGE)){
            title = intent.getStringExtra(AppConstants.PAGE).toString()
        }
        Utils.showLog(TAG,"==== page name === "+ title)

        binding.toolbar.tvTitle.text = title

        controller= StaticPageController(this,this)
        if(title.equals("Privacy Policy"))
        {
            controller!!.staticpage("1")
        }
        else if(title.equals("About Us"))
        {
            controller!!.staticpage("2")
        }
        else{
            controller!!.staticpage("3")
        }
    }

    override fun onstaticPage(staticpage: StaticPageResponse.Result?) {

        binding.tvDescription.setText(staticpage?.metaKeyword)
    }
}
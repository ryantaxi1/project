package com.taxi1.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.taxi1.Bean.SOSHistoryBean
import com.taxi1.Retrofit.Response.SosHistoryResponse
import com.taxi1.ViewResponse.ISosHistoryView
import com.taxi1.activity.Controller.ISosHistoryController
import com.taxi1.activity.Controller.SosHistoryController
import com.taxi1.adapter.SOSHistoryAdapter
import com.taxi1.databinding.ActivitySoshistoryBinding

class SOSHistoryActivity : BaseActivity() ,SOSHistoryAdapter.ItemClickListener,ISosHistoryView{
    private lateinit var binding : ActivitySoshistoryBinding
    var sosHistoryController: ISosHistoryController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoshistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "SOS History"

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        binding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        sosHistoryController = SosHistoryController(this, this)
        sosHistoryController!!.getSOSHistory()
    }

    private fun bindRecyclerview(login: List<SosHistoryResponse.Result?>) {

        val recyclerLayoutManager = LinearLayoutManager(this)
        binding.rvsos.layoutManager = recyclerLayoutManager
        binding.rvsos.setHasFixedSize(true)
        val adapter =  SOSHistoryAdapter(this, login)
        binding.rvsos.adapter = adapter
        adapter.setClicklistner(this)
        adapter.notifyDataSetChanged()
    }

    override fun itemclick(bean: SOSHistoryBean, position: Int, type: String) {

    }

    override fun onResponse(login: List<SosHistoryResponse.Result?>?) {

        if(login!!.isNotEmpty() && login.size > 0)
        {
           bindRecyclerview(login)
        }
    }
}
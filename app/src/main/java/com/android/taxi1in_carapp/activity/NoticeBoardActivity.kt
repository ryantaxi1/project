package com.android.taxi1in_carapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.databinding.ActivityNoticeBoardBinding

class NoticeBoardActivity : BaseActivity() {
    private val TAG = this@NoticeBoardActivity.javaClass.simpleName
    lateinit var binding: ActivityNoticeBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.tvBtnBack.setOnClickListener {
            finish()
        }
    }
}
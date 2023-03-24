package com.cool.testopencv

import android.os.Bundle
import com.cool.testopencv.databinding.ActivityTestImageBinding
import com.cool.yfc.base.BaseActivity

class TestImageActivity : BaseActivity() {
    private lateinit var binding: ActivityTestImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
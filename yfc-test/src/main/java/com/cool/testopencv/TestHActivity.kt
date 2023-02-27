package com.cool.testopencv

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.cool.testopencv.databinding.ActivityTestHBinding
import com.cool.yfc.base.BaseActivity
import com.cool.yfc.tools.mediaselect.ResultCallback
import com.cool.yfc.tools.mediaselect.getMediaPath
import com.cool.yfc.tools.mediaselect.selectImages
import com.luck.picture.lib.entity.LocalMedia
import com.qw.soul.permission.bean.Permission

class TestHActivity : BaseActivity() {
    private lateinit var binding: ActivityTestHBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestHBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            selectImages(this, true, 1, null, object : ResultCallback {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (!result.isNullOrEmpty()) {
                        val path = getMediaPath(result.first())
                        Glide.with(this@TestHActivity).load(path).into(binding.ivImage)

                        binding.button.visibility = View.GONE
                    }
                }

                override fun onCancel() {
                }

                override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                }
            })
        }
    }
}
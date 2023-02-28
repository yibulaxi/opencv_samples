package com.cool.testopencv

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.bumptech.glide.Glide
import com.cool.testopencv.databinding.ActivityTestHBinding
import com.cool.yfc.base.BaseActivity
import com.cool.yfc.ext.logE
import com.cool.yfc.ext.showLongToast
import com.cool.yfc.tools.mediaselect.ResultCallback
import com.cool.yfc.tools.mediaselect.getMediaPath
import com.cool.yfc.tools.mediaselect.selectImages
import com.cool.yfc.tools.mediaselect.selectImagesWithCrop
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.luck.picture.lib.entity.LocalMedia
import com.qw.soul.permission.bean.Permission
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

class TestHoughCirclesActivity : BaseActivity() {
    private lateinit var binding: ActivityTestHBinding
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestHBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelect.setOnClickListener {
            selectImages(
                this,
                true,
                1,
                null,
                object : ResultCallback {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (!result.isNullOrEmpty()) {
                            filePath = getMediaPath(result.first())

                            handleFilePath {
                                loadImage(filePath)
                                changeVisibility()

                                test(minDist, param1, param2, minRadius, maxRadius)
                            }
                        }
                    }

                    override fun onCancel() {
                    }

                    override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                    }
                }
            )
        }

        binding.btnSelectCrop.setOnClickListener {
            selectImagesWithCrop(
                this,
                true,
                1,
                null,
                1080F,
                1920F,
                object : ResultCallback {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (!result.isNullOrEmpty()) {
                            filePath = getMediaPath(result.first())

                            handleFilePath {
                                loadImage(filePath)
                                changeVisibility()

                                test(minDist, param1, param2, minRadius, maxRadius)
                            }
                        }
                    }

                    override fun onCancel() {
                    }

                    override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                    }
                }
            )
        }

        binding.ivImage.setOnClickListener {
            changeVisibility()
        }

        binding.btnReset.setOnClickListener {
            minDist = 8.0
            param1 = 90.0
            param2 = 30.0
            minRadius = 1
            maxRadius = 25

            setProgress()
            test(minDist, param1, param2, minRadius, maxRadius)
        }

        val listener = object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                if (view != null) {
                    when (view) {
                        binding.rsb1 -> {
                            "minDist，两个圆心之间的最小距离。若两圆心距离 < minDist，则认为是同一个圆。".showLongToast()
                        }
                        binding.rsb2 -> {
                            "param1，Canny 边缘检测的高阈值，低阈值被自动置为高阈值的一半，默认为 100。".showLongToast()
                        }
                        binding.rsb3 -> {
                            "param2，累加平面某点是否是圆心的判定阈值。它越大，能通过检测的圆就更接近完美的圆形，默认为 100。".showLongToast()
                        }
                        binding.rsb4 -> {
                            "minRadius，圆半径的最小值。默认为 0。".showLongToast()
                        }
                        binding.rsb5 -> {
                            "maxRadius，圆半径的最大值，默认为 0。".showLongToast()
                        }
                    }
                }
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                if (view != null) {
                    stopTrackingTouch(view)
                }
            }
        }

        setProgress()

        binding.rsb1.setOnRangeChangedListener(listener)
        binding.rsb2.setOnRangeChangedListener(listener)
        binding.rsb3.setOnRangeChangedListener(listener)
        binding.rsb4.setOnRangeChangedListener(listener)
        binding.rsb5.setOnRangeChangedListener(listener)

        "点击空白处可隐藏按钮".showLongToast()
    }

    private fun handleFilePath(listener: () -> Unit) {
        if (!filePath.isNullOrEmpty()) {
            Luban.with(this)
                .load(filePath)
                .ignoreBy(1024)
                .setTargetDir(cacheDir.absolutePath)
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {
                        "正在压缩图片".showLongToast()
                    }

                    override fun onSuccess(source: String?, compressFile: File?) {
                        "图片压缩成功".showLongToast()
                        filePath = compressFile?.absolutePath
                        listener.invoke()
                    }

                    override fun onError(source: String?, e: Throwable?) {
                        "图片压缩失败".showLongToast()
                        listener.invoke()
                    }
                })
                .launch()
        }
    }

    private fun changeVisibility() {
        binding.btnSelect.visibility = if (binding.btnSelect.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.btnSelectCrop.visibility = binding.btnSelect.visibility
        binding.btnReset.visibility = binding.btnSelect.visibility
    }

    private fun setProgress() {
        binding.rsb1.setProgress(minDist.toFloat())
        binding.rsb2.setProgress(param1.toFloat())
        binding.rsb3.setProgress(param2.toFloat())
        binding.rsb4.setProgress(minRadius.toFloat())
        binding.rsb5.setProgress(maxRadius.toFloat())
    }

    private fun loadImage(filePath: String?) {
        Glide.with(this@TestHoughCirclesActivity).load(filePath).into(binding.ivImage)
    }

    private fun stopTrackingTouch(view: RangeSeekBar) {
        val value = view.rangeSeekBarState[0].value
        when (view) {
            binding.rsb1 -> {
                test(value.toDouble(), param1, param2, minRadius, maxRadius)
            }
            binding.rsb2 -> {
                test(minDist, value.toDouble(), param2, minRadius, maxRadius)
            }
            binding.rsb3 -> {
                test(minDist, param1, value.toDouble(), minRadius, maxRadius)
            }
            binding.rsb4 -> {
                test(minDist, param1, param2, value.toInt(), maxRadius)
            }
            binding.rsb5 -> {
                test(minDist, param1, param2, minRadius, value.toInt())
            }
        }
    }

    private var minDist: Double = 8.0
    private var param1: Double = 100.0
    private var param2: Double = 35.0
    private var minRadius: Int = 3
    private var maxRadius: Int = 24

    private fun test(minDist: Double, param1: Double, param2: Double, minRadius: Int, maxRadius: Int) {
        this.minDist = minDist
        this.param1 = param1
        this.param2 = param2
        this.minRadius = minRadius
        this.maxRadius = maxRadius

        if (filePath.isNullOrEmpty()) return

        ThreadUtils.executeByIo(object : SimpleTask<String>() {
            override fun doInBackground(): String {
                val rgb = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_COLOR)
                val gray = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE)

                val circles = Mat()

                Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, minDist, param1, param2, minRadius, maxRadius)
                val cols = circles.cols()
                if (cols > 0) {
                    var x = 0
                    while (x < cols) {
                        val circleVec = circles[0, x]
                        if (circleVec != null && circleVec.size >= 3) {
                            val center = Point(circleVec[0].toInt().toDouble(), circleVec[1].toInt().toDouble())
                            val radius = circleVec[2].toInt()
                            Imgproc.circle(rgb, center, 3, Scalar(255.0, 255.0, 255.0), 5)
                            Imgproc.circle(rgb, center, radius, Scalar(255.0, 255.0, 255.0), 2)
                        }
                        x++
                    }
                    Imgproc.putText(
                        rgb,
                        cols.toString() + "circle",
                        Point(20.0, 20.0),
                        Imgproc.FONT_HERSHEY_SIMPLEX,
                        1.0,
                        Scalar(0.0, 255.0, 0.0),
                        3
                    )
                }
                circles.release()

                val file = File.createTempFile("rgb", ".png")
                Imgcodecs.imwrite(file.absolutePath, rgb)

                return file.absolutePath
            }

            override fun onSuccess(result: String?) {
                if (isFinishing || isDestroyed) return

                loadImage(result.toString())
            }
        })
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            if (status == SUCCESS) {
                logE("OpenCV loaded successfully")

                // Load native library after(!) OpenCV initialization
                System.loadLibrary("mixed_sample")
            } else {
                super.onManagerConnected(status)
            }
        }
    }

    public override fun onResume() {
        super.onResume()

        if (!OpenCVLoader.initDebug()) {
            logE("Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            logE("OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }
}
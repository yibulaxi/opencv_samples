package com.cool.testopencv

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.cool.testopencv.databinding.ActivityTutorial2Binding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.features2d.FastFeatureDetector
import org.opencv.imgproc.Imgproc

class Tutorial2Activity : CameraActivity() {
    companion object {
        private const val TAG = "OCVSample::Activity"
        private const val VIEW_MODE_RGBA = 0
        private const val VIEW_MODE_GRAY = 1
        private const val VIEW_MODE_CANNY = 2
        private const val VIEW_MODE_FEATURES = 5
        private const val VIEW_MODE_HOUGH_CIRCLES = 10
    }

    private var mViewMode = 0
    private var mRgba: Mat? = null
    private var mIntermediateMat: Mat? = null
    private var mGray: Mat? = null
    private var mItemPreviewRGBA: MenuItem? = null
    private var mItemPreviewGray: MenuItem? = null
    private var mItemPreviewCanny: MenuItem? = null
    private var mItemPreviewFeaturesNative: MenuItem? = null
    private var mItemPreviewFeaturesJava: MenuItem? = null
    private var mItemPreviewHoughCircles: MenuItem? = null
    private var isNativeFeatures = true

    private lateinit var binding: ActivityTutorial2Binding

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            if (status == SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully")

                // Load native library after(!) OpenCV initialization
                System.loadLibrary("mixed_sample")

                binding.jcv.enableView()
            } else {
                super.onManagerConnected(status)
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorial2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.jcv.apply {
            visibility = CameraBridgeViewBase.VISIBLE

            setCvCameraViewListener(object : CvCameraViewListener2 {
                override fun onCameraViewStarted(width: Int, height: Int) {
                    mRgba = Mat(height, width, CvType.CV_8UC4)
                    mGray = Mat(height, width, CvType.CV_8UC1)
                    mIntermediateMat = Mat(height, width, CvType.CV_8UC4)
                }

                override fun onCameraViewStopped() {
                    mRgba!!.release()
                    mGray!!.release()
                    mIntermediateMat!!.release()
                }

                override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat = myCameraFrame(inputFrame)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.i(TAG, "called onCreateOptionsMenu")
        mItemPreviewRGBA = menu.add("Preview RGBA")
        mItemPreviewGray = menu.add("Preview GRAY")
        mItemPreviewCanny = menu.add("Canny")
        mItemPreviewFeaturesNative = menu.add("Find Features Native")
        mItemPreviewFeaturesJava = menu.add("Find Features Java")
        mItemPreviewHoughCircles = menu.add("Hough Circles")
        return true
    }

    public override fun onPause() {
        binding.jcv.disableView()

        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun getCameraViewList(): List<CameraBridgeViewBase?> {
        return listOf(binding.jcv)
    }

    public override fun onDestroy() {
        binding.jcv.disableView()

        super.onDestroy()
    }

    fun myCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        when (mViewMode) {
            VIEW_MODE_GRAY ->                 // input frame has gray scale format
                Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4)
            VIEW_MODE_RGBA ->                 // input frame has RBGA format
                mRgba = inputFrame.rgba()
            VIEW_MODE_CANNY -> {
                // input frame has gray scale format
                mRgba = inputFrame.rgba()
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80.0, 100.0)
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4)
            }
            VIEW_MODE_FEATURES -> {
                // input frame has RGBA format
                mRgba = inputFrame.rgba()
                mGray = inputFrame.gray()
                if (isNativeFeatures) {
                    FindFeatures(mGray!!.nativeObjAddr, mRgba!!.nativeObjAddr)
                } else {
                    val detector = FastFeatureDetector.create()
                    val matOfKeyPoint = MatOfKeyPoint()
                    detector.detect(mGray, matOfKeyPoint)
                    val keyPoints = matOfKeyPoint.toList()
                    for (kp in keyPoints) {
                        Imgproc.circle(mRgba, Point(kp.pt.x, kp.pt.y), 10, Scalar(255.0, 0.0, 0.0, 255.0))
                    }
                }
            }
            VIEW_MODE_HOUGH_CIRCLES -> {
                mRgba = inputFrame.rgba()
                mGray = inputFrame.gray()
                val circles = Mat()
                //                Imgproc.blur(mGray, mGray, new Size(7, 7), new Point(2, 2));
                Imgproc.HoughCircles(mGray, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 8.0, 90.0, 30.0, 1, 25)
                val cols = circles.cols()
                if (cols > 0) {
                    var x = 0
                    while (x < cols) {
                        val circleVec = circles[0, x]
                        if (circleVec != null && circleVec.size >= 3) {
                            val center = Point(circleVec[0].toInt().toDouble(), circleVec[1].toInt().toDouble())
                            val radius = circleVec[2].toInt()
                            Imgproc.circle(mRgba, center, 3, Scalar(255.0, 255.0, 255.0), 5)
                            Imgproc.circle(mRgba, center, radius, Scalar(255.0, 255.0, 255.0), 2)
                        }
                        x++
                    }
                    Imgproc.putText(
                        mRgba,
                        cols.toString() + "circle",
                        Point(20.0, 20.0),
                        Imgproc.FONT_HERSHEY_SIMPLEX,
                        1.0,
                        Scalar(0.0, 255.0, 0.0),
                        3
                    )
                }
                circles.release()
            }
        }
        return mRgba!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, "called onOptionsItemSelected; selected item: $item")
        if (item === mItemPreviewRGBA) {
            mViewMode = VIEW_MODE_RGBA
        } else if (item === mItemPreviewGray) {
            mViewMode = VIEW_MODE_GRAY
        } else if (item === mItemPreviewCanny) {
            mViewMode = VIEW_MODE_CANNY
        } else if (item === mItemPreviewFeaturesNative) {
            mViewMode = VIEW_MODE_FEATURES
            isNativeFeatures = true
        } else if (item === mItemPreviewFeaturesJava) {
            mViewMode = VIEW_MODE_FEATURES
            isNativeFeatures = false
        } else if (item === mItemPreviewHoughCircles) {
            mViewMode = VIEW_MODE_HOUGH_CIRCLES
        }
        return true
    }

    external fun FindFeatures(matAddrGr: Long, matAddrRgba: Long)
}
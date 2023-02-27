package com.cool.testopencv

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.blankj.utilcode.util.SDCardUtils
import com.cool.testopencv.databinding.ActivityMainBinding
import com.cool.yfc.ext.BASE_TAG
import com.cool.yfc.ext.logE
import com.cool.yfc.ext.showLongToast
import com.google.android.material.snackbar.Snackbar
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            test()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!OpenCVLoader.initDebug()) {
            Log.d(BASE_TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d(BASE_TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            if (status == SUCCESS) {
                Log.i(BASE_TAG, "OpenCV loaded successfully")

                "OpenCV loaded successfully".showLongToast()
            } else {
                super.onManagerConnected(status)
            }
        }
    }

    private fun test() {
        kotlin.runCatching {
            val filePath = SDCardUtils.getSDCardPathByEnvironment() + "/111111111111111111111/测试文件/image/640.jpg"
            val filePathOut = SDCardUtils.getSDCardPathByEnvironment() + "/111111111111111111111/测试文件/image/out_640.jpg"
            val image = Imgcodecs.imread(filePath)
            val se = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0), Point(-1.0, -1.0))
            val test = Mat()
            Imgproc.morphologyEx(image, test, Imgproc.MORPH_GRADIENT, se)
            Imgcodecs.imwrite(filePathOut, test)

            val gray = Mat()
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

//        circles= cv.HoughCircles(gray, cv.HOUGH_GRADIENT, d, min_dist, param1=hgrad, param2=lgrad, minRadius= min, maxRadius=max)
//        for c in circles[0]:
//        print(c)
//        x, y, r = c
//        b = np.random.randint(0, 256)
//        g = np.random.randint(0, 256)
//        r = np.random.randint(0, 256)
//        cv.circle(src, (x, y), 30, (255, g, r), -1, 8, 0)
//        cv.imwrite("D:/hough_det.png", src)
        }.onFailure {
            logE(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
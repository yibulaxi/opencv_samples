package com.cool.testopencv

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.CameraBridgeViewBase

abstract class CameraActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 200
    }

    open fun getCameraViewList(): List<CameraBridgeViewBase?>? = arrayListOf()

    private fun onCameraPermissionGranted() {
        val cameraViews = getCameraViewList() ?: return
        for (cameraBridgeViewBase in cameraViews) {
            cameraBridgeViewBase?.setCameraPermissionGranted()
        }
    }

    override fun onStart() {
        super.onStart()
        var havePermission = true
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            havePermission = false
        }
        if (havePermission) {
            onCameraPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
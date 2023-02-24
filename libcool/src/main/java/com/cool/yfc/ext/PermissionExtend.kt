@file:JvmName(PERMISSION_EXTEND)

package com.cool.yfc.ext

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.blankj.utilcode.util.ActivityUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.bean.Special
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

fun checkPermissionsStorageOK(): Boolean = checkPermissionsOK(storagePermission())
fun checkPermissionsOK(permissions: List<String>): Boolean = checkPermissionsOK(*permissions.toTypedArray())
fun checkPermissionsOK(vararg permissions: String): Boolean {
    val arr = checkPermissions(*permissions)
    arr.forEach {
        if (!it.isGranted) {
            return false
        }
    }
    return true
}

fun checkSinglePermission(permission: String): Permission = SoulPermission.getInstance().checkSinglePermission(permission)
fun checkPermissions(vararg permissions: String): Array<Permission> = SoulPermission.getInstance().checkPermissions(*permissions)
fun checkSpecialPermission(special: Special): Boolean = SoulPermission.getInstance().checkSpecialPermission(special)

fun requestPermissionStorage(autoHandleDenied: Boolean = true, listener: ((success: Boolean, permissions: Array<Permission>?) -> Unit)? = null) =
    requestPermission(storagePermission(), autoHandleDenied, listener)

fun requestPermission(permission: String, autoHandleDenied: Boolean = true, listener: ((success: Boolean, permissions: Array<Permission>?) -> Unit)? = null) =
    requestPermission(listOf(permission), autoHandleDenied, listener)

fun requestPermission(permissions: List<String>, autoHandleDenied: Boolean = true, listener: ((success: Boolean, permissions: Array<Permission>?) -> Unit)? = null) {
    runCatching {
        if (includeStorage(permissions) && !checkedStorage()) {
            return
        }

        SoulPermission.getInstance().checkAndRequestPermissions(
            Permissions.build(*permissions.toTypedArray()),
            object : CheckRequestPermissionsListener {
                override fun onAllPermissionOk(allPermissions: Array<Permission>?) {
                    listener?.invoke(true, allPermissions)
                }

                override fun onPermissionDenied(refusedPermissions: Array<Permission>?) {
                    if (autoHandleDenied) {
                        showPermissionDenied()
                    } else {
                        listener?.invoke(false, refusedPermissions)
                    }
                }
            })
    }.onFailure {
        logE(it)
    }
}

fun storagePermission(): MutableList<String> = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
fun storagePermissionWith(vararg permissions: String): MutableList<String> {
    return storagePermission().apply { addAll(permissions) }
}

private fun includeStorage(permissions: List<String>): Boolean {
    val storagePermission = storagePermission()
    storagePermission.forEach {
        if (permissions.contains(it)) {
            return true
        }
    }
    return false
}

private var checkedStorageDisposable: Disposable? = null
var needStorageToast: String? = ""

private fun checkedStorage(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
        if (needStorageToast.isNullOrEmpty()) {
            openStorageSetting()
        } else {
            needStorageToast.showLongToast()

            checkedStorageDisposable.closeSafe()
            checkedStorageDisposable = Observable.timer(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe { openStorageSetting() }
        }
        false
    } else {
        true
    }
}

fun openStorageSetting() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ActivityUtils.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}




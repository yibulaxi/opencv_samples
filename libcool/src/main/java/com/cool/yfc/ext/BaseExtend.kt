@file:JvmName(BASE_EXTEND)

package com.cool.yfc.ext

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ThrowableUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @author yfc
 * @version V1.0
 * @since 2022/05/26 13:40
 */

/**
 * 检查花费时间
 *
 * @param runnable runnable
 */
fun checkSpendTime(runnable: Runnable) {
    checkSpendTime("checkSpendTime", runnable)
}

/**
 * 检查花费时间
 *
 * @param msg  msg
 * @param runnable runnable
 */
fun checkSpendTime(msg: String?, runnable: Runnable) {
    var message = msg
    val time = System.currentTimeMillis()
    runnable.run()
    if (ObjectUtils.isNotEmpty(message)) {
        message += "\n"
    }
    logE(message + (System.currentTimeMillis() - time) + "ms", BASE_EXTEND)
}

fun saveVideoToSystemAlbum(context: Context, videoFile: String): Boolean {
    return try {
        val resolver = context.contentResolver
        val contentValues = getVideoContentValues(File(videoFile), System.currentTimeMillis())
        val localUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val out = context.contentResolver.openOutputStream(localUri!!)
                copyFile(videoFile, out)
            } catch (e: IOException) {
                logE(e)
            }
        }
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri))
        //将该文件扫描到相册
        //MediaScannerConnection.scanFile(context, new String[] { videoFile }, null, null);
        true
    } catch (e: Exception) {
        logE(e)
        false
    }
}

private fun getVideoContentValues(paramFile: File, paramLong: Long): ContentValues {
    val contentValues = ContentValues()
    contentValues.put("title", paramFile.name)
    contentValues.put("_display_name", paramFile.name)
    contentValues.put("mime_type", "video/*")
    contentValues.put("datetaken", paramLong)
    contentValues.put("date_modified", paramLong)
    contentValues.put("date_added", paramLong)
    contentValues.put("_data", paramFile.absolutePath)
    contentValues.put("_size", paramFile.length())
    return contentValues
}

private fun copyFile(oldPath: String, out: OutputStream?): Boolean {
    try {
        var byteRead: Int
        val oldFile = File(oldPath)
        if (oldFile.exists()) {
            val inStream = FileInputStream(oldPath)
            val buffer = ByteArray(1024)
            while (inStream.read(buffer).also { byteRead = it } != -1) {
                out!!.write(buffer, 0, byteRead)
            }
            inStream.close()
            out!!.close()
            return true
        } else {
            logE(String.format("文件(%s)不存在。", oldPath), BASE_EXTEND)
        }
    } catch (e: Exception) {
        logE(e)
    }
    return false
}

/**
 * 检测辅助功能是否开启
 */
fun <T> isAccessibilitySettingsOn(context: Context, clazz: Class<T>): Boolean {
    val service: String = context.packageName + "/" + clazz.canonicalName
    var accessibilityEnabled = 0

    runCatching {
        accessibilityEnabled = Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        logV("accessibilityEnabled = $accessibilityEnabled")
    }.onFailure {
        logE("Error finding setting, default accessibility to not found: " + ThrowableUtils.getFullStackTrace(it))
    }

    val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        logV("***ACCESSIBILITY IS ENABLED*** -----------------")
        val settingValue: String? = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService: String = mStringColonSplitter.next()
                logV("-------------- > accessibilityService :: $accessibilityService $service")
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    logV("We've found the correct setting - accessibility is switched on!")
                    return true
                }
            }
        }
    } else {
        logV("***ACCESSIBILITY IS DISABLED***")
    }
    return false
}

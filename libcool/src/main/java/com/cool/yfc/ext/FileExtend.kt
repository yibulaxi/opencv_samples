@file:JvmName(FILE_EXTEND)

package com.cool.yfc.ext

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import java.io.File

/**
 *
 * @author yfc
 * @since 2023/02/07 16:28
 * @version V1.0
 */
fun String?.toFile(): File? = getFileByPath(this)
fun getFileByPath(path: String?): File? = if (!path.isNullOrEmpty()) FileUtils.getFileByPath(path) else null

fun String?.readFileBytes(): ByteArray? = readFileBytesByPath(this)
fun readFileBytesByPath(filePath: String?): ByteArray? = readFileBytes(filePath.toFile())

fun File?.readBytes(): ByteArray? = readFileBytes(this)
fun readFileBytes(file: File?): ByteArray? {
    runCatching {
        return if (file != null && file.exists() && file.isFile) return FileIOUtils.readFile2BytesByStream(file) else null
    }.onFailure {
        logE(it)
    }
    return null
}
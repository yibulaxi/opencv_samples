@file:JvmName(ENCRYPT_EXTEND)

package com.cool.yfc.ext

import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileIOUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author yfc
 * @since 2023/02/07 15:37
 * @version V1.0
 */
enum class EncryptType(open val encryptName: String) {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA1"),
    SHA224("SHA224"),
    SHA256("SHA256"),
    SHA384("SHA384"),
    SHA512("SHA512");
}

fun String?.paresEncryptType(): EncryptType? = paresEncryptTypeByString(this)
fun paresEncryptTypeByString(encryptName: String?): EncryptType? {
    if (!encryptName.isNullOrEmpty()) {
        when (encryptName.replace("-", "").replace("_", "").trim().uppercase()) {
            EncryptType.MD2.encryptName -> return EncryptType.MD2
            EncryptType.MD5.encryptName -> return EncryptType.MD5
            EncryptType.SHA1.encryptName -> return EncryptType.SHA1
            EncryptType.SHA224.encryptName -> return EncryptType.SHA224
            EncryptType.SHA256.encryptName -> return EncryptType.SHA256
            EncryptType.SHA384.encryptName -> return EncryptType.SHA384
            EncryptType.SHA512.encryptName -> return EncryptType.SHA512
        }
    }
    return null
}

fun File?.encryptLowercase(type: EncryptType?, locale: Locale = Locale.ROOT): String? = encryptFileLowercase(type, this, locale)
fun File?.encryptUppercase(type: EncryptType?, locale: Locale = Locale.ROOT): String? = encryptFileUppercase(type, this, locale)
fun File?.encrypt(type: EncryptType?): String? = encryptFile(type, this)

fun encryptFileLowercase(type: EncryptType?, file: File?, locale: Locale = Locale.ROOT): String? = encryptFile(type, file)?.lowercase(locale)
fun encryptFileUppercase(type: EncryptType?, file: File?, locale: Locale = Locale.ROOT): String? = encryptFile(type, file)?.uppercase(locale)
fun encryptFile(type: EncryptType?, file: File?): String? {
    runCatching {
        return if (file != null && file.exists() && file.isFile) {
            val bytes = FileIOUtils.readFile2BytesByStream(file)
            encryptBytes(type, bytes)
        } else {
            null
        }
    }.onFailure {
        logE(it)
    }
    return null
}

fun String?.encryptLowercase(
    type: EncryptType?,
    charset: String? = StandardCharsets.UTF_8.name(),
    locale: Locale = Locale.ROOT,
): String? = encryptStringLowercase(type, this, charset, locale)

fun String?.encryptUppercase(
    type: EncryptType?,
    charset: String? = StandardCharsets.UTF_8.name(),
    locale: Locale = Locale.ROOT,
): String? = encryptStringUppercase(type, this, charset, locale)

fun String?.encrypt(
    type: EncryptType?,
    charset: String? = StandardCharsets.UTF_8.name(),
): String? = encryptString(type, this, charset)

fun encryptStringLowercase(
    type: EncryptType?,
    content: String?,
    charset: String? = StandardCharsets.UTF_8.name(),
    locale: Locale = Locale.ROOT,
): String? = encryptString(type, content, charset)?.lowercase(locale)

fun encryptStringUppercase(
    type: EncryptType?,
    content: String?,
    charset: String? = StandardCharsets.UTF_8.name(),
    locale: Locale = Locale.ROOT,
): String? = encryptString(type, content, charset)?.uppercase(locale)

fun encryptString(
    type: EncryptType?,
    content: String?,
    charset: String? = StandardCharsets.UTF_8.name(),
): String? {
    runCatching {
        return encryptBytes(type, content?.toByteArray(if (!charset.isNullOrEmpty()) charset(charset) else StandardCharsets.UTF_8))
    }.onFailure {
        logE(it)
    }
    return null
}

fun ByteArray?.encryptLowercase(type: EncryptType?, locale: Locale = Locale.ROOT): String? = encryptBytesLowercase(type, this, locale)
fun ByteArray?.encryptUppercase(type: EncryptType?, locale: Locale = Locale.ROOT): String? = encryptBytesUppercase(type, this, locale)
fun ByteArray?.encrypt(type: EncryptType?): String? = encryptBytes(type, this)

fun encryptBytesLowercase(type: EncryptType?, bytes: ByteArray?, locale: Locale = Locale.ROOT): String? = encryptBytes(type, bytes)?.lowercase(locale)
fun encryptBytesUppercase(type: EncryptType?, bytes: ByteArray?, locale: Locale = Locale.ROOT): String? = encryptBytes(type, bytes)?.uppercase(locale)
fun encryptBytes(type: EncryptType?, bytes: ByteArray?): String? {
    runCatching {
        return if (type == null || bytes == null) {
            null
        } else {
            val encryptBytes = when (type) {
                EncryptType.MD2 -> EncryptUtils.encryptMD2(bytes)
                EncryptType.MD5 -> EncryptUtils.encryptMD5(bytes)
                EncryptType.SHA1 -> EncryptUtils.encryptSHA1(bytes)
                EncryptType.SHA224 -> EncryptUtils.encryptSHA224(bytes)
                EncryptType.SHA256 -> EncryptUtils.encryptSHA256(bytes)
                EncryptType.SHA384 -> EncryptUtils.encryptSHA384(bytes)
                EncryptType.SHA512 -> EncryptUtils.encryptSHA512(bytes)
            }
            ConvertUtils.bytes2HexString(encryptBytes)
        }
    }.onFailure {
        logE(it)
    }
    return null
}





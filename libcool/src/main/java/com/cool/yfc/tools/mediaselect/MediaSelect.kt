package com.cool.yfc.tools.mediaselect

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cool.yfc.ext.requestPermission
import com.cool.yfc.ext.storagePermissionWith
import com.cool.yfc.tools.mediaselect.engine.image.ImageEngine
import com.cool.yfc.tools.mediaselect.engine.video.VideoEngine
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureSelectionConfig.selectorStyle
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.picture.lib.utils.StyleUtils
import com.qw.soul.permission.bean.Permission
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine

fun getMediaPath(media: LocalMedia?): String? {
    var path: String? = null
    if (media != null) {
        path = if (media.isCut && !media.isCompressed) { // 裁剪过
            media.cutPath
        } else if (media.isCompressed || media.isCut && media.isCompressed) { // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            media.compressPath
        } else { // 原图
            if (TextUtils.isEmpty(media.realPath)) media.path else media.realPath
        }
    }
    return path
}

////////////////////////////////////////////////////////////////////image////////////////////////////////////////////////////////////////////
fun selectImages(
    context: Context?,
    isDisplayCamera: Boolean = true,
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>? = null,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        selectMedia(
            context = context,
            isDisplayCamera = isDisplayCamera,
            maxSelectNum = maxSelectNum,
            selectedList = selectedList,
            callback = callback
        )
    }
}

fun selectImagesWithCrop(
    context: Context?,
    isDisplayCamera: Boolean = true,
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>? = null,
    cropX: Float = 0F,
    cropY: Float = 0F,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        selectMedia(
            context = context,
            isDisplayCamera = isDisplayCamera,
            maxSelectNum = maxSelectNum,
            selectedList = selectedList,
            isCrop = true,
            cropX = cropX,
            cropY = cropY,
            callback = callback
        )
    }
}

fun takeImage(context: Context?, callback: ResultCallback? = null) {
    context?.createSelector()?.apply {
        takeMedia(
            context = context,
            callback = callback
        )
    }
}

fun takeImageWithCrop(
    context: Context?,
    cropX: Float = 0F,
    cropY: Float = 0F,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        takeMedia(
            context = context,
            isCrop = true,
            cropX = cropX,
            cropY = cropY,
            callback = callback
        )
    }
}

fun selectWallpaperImage(
    context: Context?,
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>?,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        val cropX = ScreenUtils.getScreenWidth().toFloat()
        val cropY = ScreenUtils.getScreenHeight().toFloat()

        selectMedia(
            context = context,
            isDisplayCamera = true,
            maxSelectNum = maxSelectNum,
            selectedList = selectedList,
            isCrop = true,
            cropX = cropX,
            cropY = cropY,
            callback = callback
        )
    }
}
////////////////////////////////////////////////////////////////////image////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////video////////////////////////////////////////////////////////////////////
fun takeVideo(
    context: Context?,
    minSecond: Int = 1,
    maxSecond: Int = 60,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        takeMedia(
            context = context,
            chooseMode = SelectMimeType.ofVideo(),
            minSecond = minSecond,
            maxSecond = maxSecond,
            callback = callback
        )
    }
}

fun selectVideos(
    context: Context?,
    isDisplayCamera: Boolean = true,
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>? = null,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        selectMedia(
            context = context,
            chooseMode = SelectMimeType.ofVideo(),
            isDisplayCamera = isDisplayCamera,
            maxSelectNum = maxSelectNum,
            selectedList = selectedList,
            callback = callback
        )
    }
}
////////////////////////////////////////////////////////////////////video////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////audio////////////////////////////////////////////////////////////////////
fun takeAudio(
    context: Context?,
    minSecond: Int = 1,
    maxSecond: Int = 60,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        takeMedia(
            context = context,
            chooseMode = SelectMimeType.ofAudio(),
            minSecond = minSecond,
            maxSecond = maxSecond,
            callback = callback
        )
    }
}

fun selectAudios(
    context: Context?,
    isDisplayCamera: Boolean = true,
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>? = null,
    callback: ResultCallback? = null,
) {
    context?.createSelector()?.apply {
        selectMedia(
            context = context,
            chooseMode = SelectMimeType.ofAudio(),
            isDisplayCamera = isDisplayCamera,
            maxSelectNum = maxSelectNum,
            selectedList = selectedList,
            callback = callback
        )
    }
}
////////////////////////////////////////////////////////////////////audio////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun Activity.createSelector(): PictureSelector {
    return PictureSelector.create(this)
}

private fun Fragment.createSelector(): PictureSelector {
    return PictureSelector.create(this)
}

private fun View.createSelector(): PictureSelector {
    return PictureSelector.create(this.context)
}

private fun Context.createSelector(): PictureSelector {
    return PictureSelector.create(this)
}

/**
 * 直接进行拍照或者摄像或者录音
 */
private fun PictureSelector.takeMedia(
    context: Context,
    chooseMode: Int = SelectMimeType.ofImage(),
    minSecond: Int = 1,
    maxSecond: Int = 60,
    isCrop: Boolean = false,
    cropX: Float = 0F,
    cropY: Float = 0F,
    callback: ResultCallback? = null,
) {
    requestPermission(storagePermissionWith(Manifest.permission.CAMERA), false) { success, permissions ->
        if (success) {
            var cameraModel = this@takeMedia.openCamera(chooseMode).setSelectedData(null)

            if (chooseMode == SelectMimeType.ofVideo() || chooseMode == SelectMimeType.ofAudio()) {
                cameraModel = cameraModel.setRecordVideoMinSecond(minSecond).setRecordVideoMaxSecond(maxSecond)
            }

            if (isCrop) {
                cameraModel = cameraModel.setCropEngine(ImageFileCropEngine(context, cropX, cropY))
            }

            cameraModel.forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    callback?.onResult(result)
                }

                override fun onCancel() {
                    callback?.onCancel()
                }
            })
        } else {
            callback?.onPermissionDenied(permissions)
        }
    }
}

/**
 * 选择媒体
 */
private fun PictureSelector.selectMedia(
    context: Context,
    chooseMode: Int = SelectMimeType.ofImage(),
    maxSelectNum: Int = 1,
    selectedList: List<LocalMedia>? = null,
    isDisplayCamera: Boolean = true,
    isGif: Boolean = false,
    isPreview: Boolean = true,
    isCrop: Boolean = false,
    cropX: Float = 0F,
    cropY: Float = 0F,
    callback: ResultCallback? = null,
) {
    requestPermission(storagePermissionWith(Manifest.permission.CAMERA), false) { success, permissions ->
        if (success) {
            var selectionModel: PictureSelectionModel = this@selectMedia.openGallery(chooseMode)
                .setImageEngine(ImageEngine.glide)
                .setVideoPlayerEngine(VideoEngine.exoPlayerEngine)
                .setSelectionMode(if (maxSelectNum > 1) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE)
                .isPreviewZoomEffect(isPreview)
                .isPreviewImage(isPreview)
                .isPreviewVideo(isPreview)
                .isPreviewAudio(isPreview)
                .setMaxSelectNum(maxSelectNum)
                .setMaxVideoSelectNum(maxSelectNum)
                .isDisplayCamera(isDisplayCamera)
                .isGif(isGif)
                .setSelectedData(selectedList)

            if (isCrop) {
                selectionModel = selectionModel.setCropEngine(ImageFileCropEngine(context, cropX, cropY))
            }

            selectionModel.forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    callback?.onResult(result)
                }

                override fun onCancel() {
                    callback?.onCancel()
                }
            })
        } else {
            callback?.onPermissionDenied(permissions)
        }
    }
}

/**
 * 自定义裁剪
 */
private class ImageFileCropEngine(val context: Context, val x: Float, val y: Float) : CropFileEngine {
    override fun onStartCrop(fragment: Fragment, srcUri: Uri, destinationUri: Uri, dataSource: java.util.ArrayList<String>, requestCode: Int) {
        val options: UCrop.Options = buildOptions(context, x, y)
        val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                if (!ImageLoaderUtils.assertValidRequest(context)) {
                    return
                }
                Glide.with(context).load(url).override(180, 180).into(imageView)
            }

            override fun loadImage(context: Context, url: Uri, maxWidth: Int, maxHeight: Int, call: UCropImageEngine.OnCallbackListener<Bitmap>) {
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        call.onCall(null)
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        call.onCall(resource)
                    }
                })
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }
}

/**
 * 配制UCrop，可根据需求自我扩展
 *
 * @return UCrop.Options
 */
private fun buildOptions(context: Context, x: Float, y: Float): UCrop.Options {
    val options = UCrop.Options()
    options.setHideBottomControls(false)
    options.setFreeStyleCropEnabled(false)
    options.setShowCropFrame(true)
    options.setShowCropGrid(true)
    options.setCircleDimmedLayer(false)
    options.withAspectRatio(x, y)
    options.isCropDragSmoothToCenter(false)
    options.setSkipCropMimeType(null)
    options.isForbidCropGifWebp(false)
    options.isForbidSkipMultipleCrop(false)
    options.setMaxScaleMultiplier(100f)
    options.isDragCropImages(true)
    options.setCompressionQuality(100)

    if (selectorStyle != null && selectorStyle.selectMainStyle.statusBarColor != 0) {
        val mainStyle: SelectMainStyle = selectorStyle.selectMainStyle
        val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
        val statusBarColor = mainStyle.statusBarColor
        options.isDarkStatusBarBlack(isDarkStatusBarBlack)
        if (StyleUtils.checkStyleValidity(statusBarColor)) {
            options.setStatusBarColor(statusBarColor)
            options.setToolbarColor(statusBarColor)
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_grey))
            options.setToolbarColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_grey))
        }
        val titleBarStyle: TitleBarStyle = selectorStyle.titleBarStyle
        if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
            options.setToolbarWidgetColor(titleBarStyle.titleTextColor)
        } else {
            options.setToolbarWidgetColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_white))
        }
    } else {
        options.setStatusBarColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_grey))
        options.setToolbarColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_grey))
        options.setToolbarWidgetColor(ContextCompat.getColor(context, com.luck.picture.lib.R.color.ps_color_white))
    }
    return options
}

interface ResultCallback {
    fun onResult(result: ArrayList<LocalMedia>?)
    fun onCancel()
    fun onPermissionDenied(refusedPermissions: Array<out Permission>?)
}
package com.cool.yfc.banner

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.ReflectUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cool.yfc.R
import com.cool.yfc.ext.getDefaultImageError
import com.cool.yfc.ext.getDefaultImageFallback
import com.cool.yfc.ext.getDefaultImagePlaceholder
import com.cool.yfc.ext.logE
import com.makeramen.roundedimageview.RoundedImageView
import com.youth.banner.adapter.BannerAdapter
import java.io.Serializable

/**
 *
 * @author yfc
 * @since 2022/07/28 09:23
 * @version V1.0
 */
open class BaseBannerAdapter<T : BaseBannerInterface>(
    data: List<T>? = null,
    var onCreateHolderFunction: ((View, RoundedImageView, LottieAnimationView) -> Unit)? = null,
    var extra: Serializable? = null,
) : BannerAdapter<T, BaseBannerAdapter.BaseBannerHolder>(data) {
    /**
     * 创建ViewHolder
     *
     * @return XViewHolder
     */
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BaseBannerHolder {
        val context = parent!!.context
        val roundedView = RoundedImageView(context)
            .apply {
                layoutParams = getMatchParent()
                scaleType = ImageView.ScaleType.FIT_CENTER
                cornerRadius = parent.context.resources.getDimension(R.dimen.dp_10)
            }
        val lottieView = LottieAnimationView(context).apply {
            layoutParams = getMatchParent()
            ReflectUtils.reflect(this).field("wasAnimatingWhenDetached", true)
            ReflectUtils.reflect(this).field("autoPlay", true)
        }
        val frameLayout = FrameLayout(context).apply {
            layoutParams = getMatchParent()
            addView(roundedView)
            addView(lottieView)
            if (extra != null) {
                addView(AppCompatTextView(context).apply {
                    layoutParams = getMatchParent()
                    gravity = Gravity.CENTER
                    text = extra.toString()
                })
            }
        }

        onCreateHolderFunction?.invoke(frameLayout, roundedView, lottieView)

        return BaseBannerHolder(frameLayout)
    }

    /**
     * 绑定布局数据
     *
     * @param holder   XViewHolder
     * @param data     数据实体
     * @param position 当前位置
     * @param size     总数
     */
    override fun onBindView(holder: BaseBannerHolder?, data: T, position: Int, size: Int) {
        holder?.apply {
            if (data is BaseBannerBean) {
                if (data.type == BannerType.IMAGE) {
                    changeShowType(holder, BannerType.IMAGE)
                    roundedView.cornerRadius = data.cornerRadius
                    loadImage(roundedView, data)
                } else {
                    changeShowType(holder, BannerType.LOTTIE)
                    loadLottie(holder, data)
                }
            } else {
                changeShowType(holder, BannerType.IMAGE)
                loadImage(roundedView, data)
            }
        }
    }

    private fun getMatchParent(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun loadImage(roundedView: RoundedImageView, data: T) {
        loadImage(roundedView, data.getData())
    }

    private fun loadImage(roundedView: RoundedImageView, any: Any?) {
        Glide.with(roundedView)
            .load(any)
            .apply(getRequestOptions())
            .into(roundedView)
    }

    private fun loadLottie(holder: BaseBannerHolder?, data: BaseBannerBean) {
        holder?.apply {
            lottieView.setFailureListener {
                if (data.type == BannerType.LOTTIE) {
                    logE(it)
                    changeShowType(holder, BannerType.IMAGE)
                    loadImage(roundedView, "")
                }
            }

            val any = data.getData()
            if (any is String) {
                lottieView.setAnimationFromUrl(any)
                if (!lottieView.isAnimating) {
                    lottieView.playAnimation()
                }
            } else {
                lottieView.setAnimationFromUrl("")
            }
        }
    }

    private fun changeShowType(holder: BaseBannerHolder, type: BannerType = BannerType.IMAGE) {
        holder.roundedView.visibility = if (type == BannerType.IMAGE) View.VISIBLE else View.GONE
        holder.lottieView.visibility = if (type != BannerType.IMAGE) View.VISIBLE else View.GONE
    }

    private fun getRequestOptions(): RequestOptions {
        return RequestOptions.placeholderOf(getDefaultImagePlaceholder())
            .error(getDefaultImageError())
            .fallback(getDefaultImageFallback())
            .centerCrop()
    }

    open class BaseBannerHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var lottieView: LottieAnimationView
        lateinit var roundedView: RoundedImageView

        init {
            if (view is ViewGroup) {
                val childCount = view.childCount
                for (index in 0 until childCount) {
                    val child = view.getChildAt(index)
                    if (child is LottieAnimationView) {
                        lottieView = child
                    } else if (child is RoundedImageView) {
                        roundedView = child
                    }
                }
            }
        }
    }
}
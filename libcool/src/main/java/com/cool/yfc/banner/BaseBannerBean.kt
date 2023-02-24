package com.cool.yfc.banner

import com.cool.yfc.ext.SBean
import java.io.Serializable

open class BaseBannerBean(
    var any: Any? = null,
    var cornerRadius: Float = 0F,
    var type: BannerType = BannerType.IMAGE,
    var autoCheckBannerType: Boolean = true,
    var extra: Serializable? = null,
) : SBean(), BaseBannerInterface {
    companion object {
        private const val serialVersionUID: Long = 7808012312488588408L
    }

    init {
        if (autoCheckBannerType) {
            if (any is String) {
                val anyData = any as String
                if (anyData.endsWith(".json", true)) {
                    type = BannerType.LOTTIE
                }
            }
        }
    }

    override fun getData(): Any? = any
}

enum class BannerType {
    IMAGE,
    LOTTIE,
}

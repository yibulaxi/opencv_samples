@file:JvmName(RECYCLER_VIEW_EXTEND)

package com.cool.yfc.ext

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cool.yfc.R
import com.cool.yfc.databinding.ItemBaseMenuBinding
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author yfc
 * @since 2022/07/29 09:13
 * @version V1.0
 */
fun <T> RecyclerView.simplyAdapterBVH(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
    convert: (holder: BaseViewHolder, item: T) -> Unit,
): BaseQuickAdapter<T, BaseViewHolder> {
    return simplyAdapter(layoutResId, data, layoutManager, convert)
}

fun <T, BD : ViewDataBinding> RecyclerView.simplyAdapterBDBH(
    @LayoutRes layoutResId: Int,
    data: MutableList<T>? = null,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
    convert: (holder: BaseDataBindingHolder<BD>, item: T) -> Unit,
): BaseQuickAdapter<T, BaseDataBindingHolder<BD>> {
    return simplyAdapter(layoutResId, data, layoutManager, convert)
}

private inline fun <T, reified VH : BaseViewHolder> RecyclerView.simplyAdapter(
    @LayoutRes layoutResId: Int = 0,
    data: MutableList<T>? = null,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
    crossinline convert: (holder: VH, item: T) -> Unit,
): BaseQuickAdapter<T, VH> {
    this.layoutManager = layoutManager
    val adapter = object : BaseQuickAdapter<T, VH>(layoutResId, data) {
        override fun convert(holder: VH, item: T) {
            convert.invoke(holder, item)
        }
    }
    this.adapter = adapter
    return adapter
}

/**
 * 关闭默认局部刷新动画
 */
fun RecyclerView.closeDefaultAnimator() {
    kotlin.runCatching {
        val itemAnimator = this.itemAnimator
        if (itemAnimator != null) {
            itemAnimator.addDuration = 0
            itemAnimator.changeDuration = 0
            itemAnimator.moveDuration = 0
            itemAnimator.removeDuration = 0

            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }.onFailure {
        logE(it)
    }
}

////////////////////////////////////////////////////////////////////menu////////////////////////////////////////////////////////////////////////////
fun <T : BaseMenuItem> RecyclerView.simplyMenuItem(
    data: MutableList<T>?,
    listener: OnMenuItemListener<T>,
): MenuItemHelper<T> = simplyMenuItem(data, 1, listener)

fun <T : BaseMenuItem> RecyclerView.simplyMenuItem(
    data: MutableList<T>?,
    spanCount: Int,
    listener: OnMenuItemListener<T>,
): MenuItemHelper<T> {
    this.closeDefaultAnimator()

    val mSpanCount = min(max(spanCount, 1), 5)
    val layoutManager = if (mSpanCount > 1) GridLayoutManager(this.context, mSpanCount) else LinearLayoutManager(this.context)

    val adapter = this.simplyAdapterBDBH<T, ItemBaseMenuBinding>(
        R.layout.item_base_menu,
        data,
        layoutManager
    ) { holder, item ->
        val binding = holder.dataBinding!!

        binding.tvMenu.text = item.title
        binding.root.setOnClickListener {
            if (!item.isStatusHide()) {
                binding.switchStatus.isChecked = !item.isStatusOn()
            } else {
                listener.onClick(holder.bindingAdapterPosition, item)
            }
        }

        setSwitchStatus(holder, item, listener)
    }

    return MenuItemHelper(this, adapter)
}

fun <T : BaseMenuItem> setSwitchStatus(holder: BaseDataBindingHolder<ItemBaseMenuBinding>, item: T, listener: OnMenuItemListener<T>) {
    val binding = holder.dataBinding!!

    binding.switchStatus.setOnCheckedChangeListener(null)

    binding.switchStatus.textOn = item.statusTextOn
    binding.switchStatus.textOff = item.statusTextOff

    when (item.status) {
        BaseMenuItem.STATUS_ON -> {
            binding.switchStatus.visibility = View.VISIBLE
            binding.switchStatus.isChecked = true
        }
        BaseMenuItem.STATUS_OFF -> {
            binding.switchStatus.visibility = View.VISIBLE
            binding.switchStatus.isChecked = false
        }
        BaseMenuItem.STATUS_HIDE -> {
            binding.switchStatus.visibility = View.GONE
        }
        else -> {
            binding.switchStatus.visibility = View.GONE
        }
    }

    binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
        item.status = if (isChecked) BaseMenuItem.STATUS_ON else BaseMenuItem.STATUS_OFF

        val position = holder.bindingAdapterPosition
        holder.bindingAdapter?.notifyItemChanged(position, null)

        listener.onStatusChanged(position, item, isChecked)
    }
}

class MenuItemHelper<T : BaseMenuItem>(
    rv: RecyclerView,
    adapter: BaseQuickAdapter<T, BaseDataBindingHolder<ItemBaseMenuBinding>>,
)

interface OnMenuItemListener<T : BaseMenuItem> {
    fun onStatusChanged(position: Int, item: T, isChecked: Boolean)
    fun onClick(position: Int, item: T)
}

open class BaseMenuItem(
    var type: Int,
    var title: String,
    var status: Int,
    var statusTextOn: CharSequence?,
    var statusTextOff: CharSequence?,
) {
    companion object BASE {
        const val STATUS_HIDE = 0
        const val STATUS_ON = 1
        const val STATUS_OFF = 2
    }

    fun isStatusHide(): Boolean = status == STATUS_HIDE
    fun isStatusOn(): Boolean = status == STATUS_ON
    fun isStatusOff(): Boolean = status == STATUS_OFF
}
////////////////////////////////////////////////////////////////////menu////////////////////////////////////////////////////////////////////////////
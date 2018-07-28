package com.mobile.view.productdetail.viewtypes.variation.size

import android.view.animation.AnimationUtils
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.utils.OnItemClickListener
import com.mobile.view.R
import com.mobile.view.productdetail.model.SimpleProduct

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_size, holder = VariationsSizeHolder::class)
class VariationsSizeItem(var size: SimpleProduct, private var onItemClickListener: OnItemClickListener?) {
    var holder: VariationsSizeHolder? = null
    @Binder
    public fun binder(holder: VariationsSizeHolder) {
        this.holder = holder
        holder.title.text = size.title
        holder.itemView.setOnClickListener { onItemClicked() }
        if (size.isSelected) {
            enableView()
        } else {
            disableView()
        }

        setHasOrNotStock(holder)
    }

    private fun setHasOrNotStock(holder: VariationsSizeHolder) {
        if (!size.has_stock) {
            holder.rootLayout.setBackgroundResource(R.drawable.round_size_background_disable_gray_2dp)
            holder.title.setTextColor(holder.itemView.context.resources.getColor(R.color.gray))
        } else {
            holder.rootLayout.setBackgroundResource(R.drawable.round_size_background_2dp)
            holder.title.setTextColor(holder.itemView.context.resources.getColor(R.color.secondary_text_color))
        }
    }

    private fun enableView() {
        size.isSelected = true
        holder?.title!!.setTextColor(holder!!.itemView.context.resources.getColor(R.color.white))
        holder?.rootLayout!!.setBackgroundResource(R.drawable.selected_size_round_background_2dp)
    }

    private fun onItemClicked() {
        if (!size.has_stock) {
            return
        }

        val myAnim = AnimationUtils.loadAnimation(holder?.itemView?.context, R.anim.bounce)
        holder?.rootLayout?.startAnimation(myAnim)
        onItemClickListener?.onItemClicked(size)
        enableView()
    }

    fun disableView() {
        size.isSelected = false
        holder?.let {
            if (size.has_stock) {
                it.rootLayout.setBackgroundResource(R.drawable.round_size_background_2dp)
                it.title.setTextColor(it.itemView.context.resources.getColor(R.color.secondary_text_color))
            } else {
                it.rootLayout.setBackgroundResource(R.drawable.round_size_background_disable_gray_2dp)
                it.title.setTextColor(it.itemView.context.resources.getColor(R.color.gray))
            }
        }
    }
}
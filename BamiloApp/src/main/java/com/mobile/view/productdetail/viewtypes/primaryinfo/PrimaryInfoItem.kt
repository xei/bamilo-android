package com.mobile.view.productdetail.viewtypes.primaryinfo

import android.graphics.Paint
import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R
import com.mobile.view.productdetail.model.PrimaryInfoModel

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_primary_info, holder = PrimaryInfoHolder::class)
class PrimaryInfoItem(private var primaryInfoModel: PrimaryInfoModel) {
    @Binder
    public fun binder(holder: PrimaryInfoHolder) {
        setPriceAndDiscount(holder)

        holder.currency.text = primaryInfoModel.priceModel.currency

        holder.title.text = primaryInfoModel.title
        holder.ratingBar.rating = primaryInfoModel.rating.average
        holder.averageScore.text = primaryInfoModel.rating.average.toString()
        holder.scoreCount.text = primaryInfoModel.rating.ratingCount.toString()
    }

    private fun setPriceAndDiscount(holder: PrimaryInfoHolder) {
        if (!primaryInfoModel.isExist) {
            holder.discountPercentageRoot.visibility = View.GONE
            holder.discountPercentage.visibility = View.GONE
            holder.discountBenefit.visibility = View.GONE
            holder.currentPrice.visibility = View.GONE
            holder.oldPrice.visibility = View.GONE
            holder.currency.visibility = View.GONE

            return
        }

        if (primaryInfoModel.priceModel.discount.isEmpty()) {
            holder.currentPrice.text = primaryInfoModel.priceModel.cost

            holder.discountPercentageRoot.visibility = View.GONE
            holder.discountPercentage.visibility = View.GONE
            holder.discountBenefit.visibility = View.GONE
            holder.oldPrice.visibility = View.GONE

        } else {
            holder.oldPrice.text = primaryInfoModel.priceModel.cost
            holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.currentPrice.text = primaryInfoModel.priceModel.discount
            holder.discountBenefit.text = primaryInfoModel.priceModel.discountBenefit

            if (primaryInfoModel.priceModel.discountPercentage.isEmpty()) {
                holder.discountPercentageRoot.visibility = View.GONE
                holder.discountPercentage.visibility = View.GONE
            }

            holder.discountPercentage.text = primaryInfoModel.priceModel.discountPercentage
        }
    }
}
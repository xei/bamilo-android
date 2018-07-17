package com.mobile.view.productdetail.viewtypes.primaryinfo

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.service.utils.shop.CurrencyFormatter
import com.mobile.utils.ui.UIUtils
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
        holder.averageScore.text = primaryInfoModel.rating.average.toString().replace(".", "/")
        holder.scoreCount.text = primaryInfoModel.rating.total.toString()

        if (primaryInfoModel.brand.isEmpty()) {
            holder.brandLayout.visibility = View.GONE
        } else {
            holder.brandLayout.visibility = View.VISIBLE
            holder.brand.text = primaryInfoModel.brand
        }
    }

    private fun setPriceAndDiscount(holder: PrimaryInfoHolder) {
        if (!checkAndSetupHasStock(holder)) {
            return
        }

        if (primaryInfoModel.priceModel.oldPrice.isEmpty() ||
                primaryInfoModel.priceModel.oldPrice == primaryInfoModel.priceModel.price ||
                primaryInfoModel.priceModel.oldPrice.toLong() == 0L) {
            setPriceWithOutDiscount(holder)
        } else {
            setPriceWithDiscount(holder)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPriceWithDiscount(holder: PrimaryInfoHolder) {
        holder.outOfStockText.visibility = View.GONE

        holder.priceLayout.visibility = View.VISIBLE
        holder.discountPercentageRoot.visibility = View.VISIBLE
        holder.discountLayout.visibility = View.VISIBLE

        holder.oldPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.oldPrice, false)
        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        holder.currentPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.price, false)
        holder.discountBenefit.text = String.format(holder.itemView.context.getString(R.string.discount_benifit),
                CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.discount_benefit, false),
                primaryInfoModel.priceModel.currency)

        if (primaryInfoModel.priceModel.discount_percentage.isEmpty()) {
            holder.discountPercentageRoot.visibility = View.GONE
            holder.discountPercentage.visibility = View.GONE
        } else {
            holder.discountPercentageRoot.visibility = View.VISIBLE
            holder.discountPercentage.visibility = View.VISIBLE
            holder.discountPercentage.text = primaryInfoModel.priceModel.discount_percentage +
                    holder.itemView.context.getString(R.string.percent_sign)
        }

        setPriceLayoutMargin(holder, 0)
    }

    private fun setPriceWithOutDiscount(holder: PrimaryInfoHolder) {
        holder.priceLayout.visibility = View.VISIBLE
        holder.currentPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.price, false)

        holder.outOfStockText.visibility = View.GONE
        holder.discountLayout.visibility = View.GONE
        holder.discountPercentageRoot.visibility = View.GONE

        setPriceLayoutMargin(holder, 16)
    }

    private fun setPriceLayoutMargin(holder: PrimaryInfoHolder, margin: Int) {
        val layoutParam: LinearLayout.LayoutParams = holder.priceLayout.layoutParams as LinearLayout.LayoutParams
        layoutParam.bottomMargin = UIUtils.dpToPx(holder.itemView.context, margin.toFloat())

        holder.priceLayout.layoutParams = layoutParam
    }

    private fun checkAndSetupHasStock(holder: PrimaryInfoHolder): Boolean {
        if (!primaryInfoModel.hasStock) {
            holder.discountLayout.visibility = View.GONE
            holder.priceLayout.visibility = View.GONE
            holder.outOfStockText.visibility = View.VISIBLE

            return false
        }
        return true
    }
}
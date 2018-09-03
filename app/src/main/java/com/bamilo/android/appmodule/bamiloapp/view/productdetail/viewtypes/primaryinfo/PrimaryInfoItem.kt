package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.primaryinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.PrimaryInfoModel
import com.bamilo.android.appmodule.modernbamilo.product.comment.submit.startSubmitRateActivity
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.appmodule.modernbamilo.util.getMorphNumberString
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.framework.service.utils.TextUtils
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_primary_info, holder = PrimaryInfoHolder::class)
class PrimaryInfoItem(private var sku: String, private var primaryInfoModel: PrimaryInfoModel, var pdvMainView: PDVMainView) {
    @Binder
    public fun binder(holder: PrimaryInfoHolder) {
        setPriceAndDiscount(holder)
        holder.run {
            if (primaryInfoModel.rating.total == 0) {
                ratingLayout.visibility = View.GONE
            } else {
                ratingLayout.visibility = View.VISIBLE
            }

            currency.text = primaryInfoModel.priceModel.currency
            title.text = primaryInfoModel.title

            ratingBar.rating = primaryInfoModel.rating.average
            averageScore.text = getMorphNumberString(primaryInfoModel.rating.average).persianizeDigitsInString()
            scoreCount.text = primaryInfoModel.rating.total.toString().persianizeDigitsInString()

            brandLayout.visibility = View.GONE
            primaryInfoModel.brand?.let {
                brandLayout.visibility = View.VISIBLE
                brand.text = primaryInfoModel.brand
            }

            ratingLayout.setOnClickListener { gotoSubmitView(holder.itemView.context) }
            brandLayout.setOnClickListener { gotoBrandPage(itemView.context) }
        }
    }

    private fun gotoSubmitView(context: Context) {
        if (!BamiloApplication.isCustomerLoggedIn()) {
            pdvMainView.loginUser()
        } else {
            startSubmitRateActivity(context, sku!!)

        }
    }

    private fun setPriceAndDiscount(holder: PrimaryInfoHolder) {
        if (!checkAndSetupHasStock(holder)) {
            return
        }

        if (!TextUtils.isEmpty(primaryInfoModel.priceModel.oldPrice) &&
                primaryInfoModel.priceModel.oldPrice != primaryInfoModel.priceModel.price &&
                primaryInfoModel.priceModel.oldPrice?.toLong() != 0L) {
            setPriceWithDiscount(holder)
        } else {
            setPriceWithOutDiscount(holder)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPriceWithDiscount(holder: PrimaryInfoHolder) {
        holder.run {
            outOfStockText.visibility = View.GONE

            priceLayout.visibility = View.VISIBLE
            discountPercentageRoot.visibility = View.VISIBLE
            discountLayout.visibility = View.VISIBLE

            oldPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.oldPrice, false).persianizeDigitsInString()
            oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            currentPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.price, false).persianizeDigitsInString()
            discountBenefit.text = String.format(holder.itemView.context.getString(R.string.discount_benifit),
                    CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.discount_benefit, false),
                    primaryInfoModel.priceModel.currency).persianizeDigitsInString()

            if (TextUtils.isEmpty(primaryInfoModel.priceModel.discount_percentage)) {
                discountPercentageRoot.visibility = View.GONE
                discountPercentage.visibility = View.GONE
            } else {
                discountPercentageRoot.visibility = View.VISIBLE
                discountPercentage.visibility = View.VISIBLE
                discountPercentage.text = primaryInfoModel.priceModel.discount_percentage?.persianizeDigitsInString() +
                        itemView.context.getString(R.string.percent_sign)
            }

            setPriceLayoutMargin(this, 0)
        }
    }

    private fun setPriceWithOutDiscount(holder: PrimaryInfoHolder) {
        holder.run {
            priceLayout.visibility = View.VISIBLE
            currentPrice.text = CurrencyFormatter.formatCurrency(primaryInfoModel.priceModel.price, false).persianizeDigitsInString()

            outOfStockText.visibility = View.GONE
            discountLayout.visibility = View.GONE
            discountPercentageRoot.visibility = View.GONE

            setPriceLayoutMargin(this, 16)
        }
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

    private fun gotoBrandPage(context: Context?) {
        val bundle = Bundle()
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, primaryInfoModel.brand)
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CATALOG_BRAND)

        val intent = Intent(context, MainFragmentActivity::class.java).apply {
            putExtra("pdv_brand_bundle", bundle)
        }

        context?.startActivity(intent)
    }
}
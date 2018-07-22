package com.mobile.view.productdetail.viewtypes.recommendation

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.service.utils.shop.CurrencyFormatter
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R
import com.emarsys.predict.RecommendedItem

/**
 * Created by Farshid
 * since 7/21/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_recommendation, holder = RecommendationHolder::class)
class RecommendationItem(private var recommendedItem: RecommendedItem, private var currency: String) {
    @SuppressLint("SetTextI18n")
    @Binder
    public fun binder(holder: RecommendationHolder) {
        val data = recommendedItem.getData() as Map<String, Any>
        ImageManager.getInstance().loadImage(data["image"] as String,
                holder.image,
                holder.progressBar,
                R.drawable.no_image_small,
                false)

        holder.title.text = data["title"] as String

        val price: Double = data["price"] as Double
        val discountPrice: Double = data["msrp"] as Double

        if (price == discountPrice) {
            holder.price.text = CurrencyFormatter.formatCurrency(price.toString(),
                    false) + " $currency"
            holder.oldPrice.visibility = View.GONE
        } else {
            holder.price.text = CurrencyFormatter.formatCurrency(price.toString(),
                    false) + " $currency"

            holder.oldPrice.text = CurrencyFormatter.formatCurrency(discountPrice.toString()) + " $currency"
            holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.oldPrice.visibility = View.VISIBLE
        }
    }
}
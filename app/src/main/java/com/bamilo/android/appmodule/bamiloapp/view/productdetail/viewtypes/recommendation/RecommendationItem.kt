package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.recommendation

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter
import com.emarsys.predict.RecommendedItem

/**
 * Created by Farshid
 * since 7/21/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_recommendation, holder = RecommendationHolder::class)
class RecommendationItem(private var recommendedItem: RecommendedItem,
                         private var currency: String,
                         private var pdvMainView: PDVMainView) {
    @SuppressLint("SetTextI18n")
    @Binder
    public fun binder(holder: RecommendationHolder) {
        if (holder.isFilled) {
            return
        }
        val data = recommendedItem.data as Map<String, Any>
        ImageManager.getInstance().loadImage(data["image"] as String,
                holder.image,
                holder.progressBar,
                R.drawable.no_image_small,
                false)

        holder.title.text = data["title"] as String

        val price: Double = data["price"] as Double
        val discountPrice: Double = data["msrp"] as Double

        if (price == discountPrice) {
            holder.price.text = (CurrencyFormatter.formatCurrency(price.toString(),
                    false) + " $currency").persianizeDigitsInString()
            holder.oldPrice.visibility = View.GONE

        } else {
            holder.price.text = (CurrencyFormatter.formatCurrency(price.toString().persianizeDigitsInString(),
                    false) + " $currency").persianizeDigitsInString()

            holder.oldPrice.text = (CurrencyFormatter.formatCurrency(discountPrice.toString(),
                    false) +
                    " $currency").persianizeDigitsInString()

            holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.oldPrice.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            pdvMainView.onRelatedProductClicked(data["item"] as String)
        }
        holder.isFilled = true
    }
}
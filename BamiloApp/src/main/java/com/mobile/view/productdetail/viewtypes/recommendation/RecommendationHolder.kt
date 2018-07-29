package com.mobile.view.productdetail.viewtypes.recommendation

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/21/2018.
 * contact farshidabazari@gmail.com
 */
class RecommendationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isFilled = false
    var title: TextView = itemView.findViewById(R.id.recommendation_textView_title)
    var price: TextView = itemView.findViewById(R.id.recommendation_textView_price)
    var oldPrice: TextView = itemView.findViewById(R.id.recommendation_textView_oldPrice)

    var image: ImageView = itemView.findViewById(R.id.home_teaser_item_image)
    var progressBar: ProgressBar = itemView.findViewById(R.id.home_teaser_item_progress)
}
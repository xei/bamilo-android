package com.mobile.view.productdetail.viewtypes.primaryinfo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class PrimaryInfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_title)
    var averageScore: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_averageScore)
    var scoreCount: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_scoreCount)
    var oldPrice: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_oldPrice)
    var currentPrice: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_currentPrice)
    var discountPercentage: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_discountPercentage)
    var discountBenefit: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_discountBenefit)
    var currency: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_currency)
    var outOfStockText: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_outOfStock)
    var brand: TextView = itemView.findViewById(R.id.pdvPrimaryInfo_textView_brand)

    var ratingBar: RatingBar = itemView.findViewById(R.id.pdvPrimaryInfo_ratingBar)
    var discountPercentageRoot: RelativeLayout = itemView.findViewById(R.id.pdvPrimaryInfo_relativeLayout_discountPercentageRoot)

    var discountLayout: LinearLayout = itemView.findViewById(R.id.pdvPrimaryInfo_linearLayout_discountLayout)
    var priceLayout: LinearLayout = itemView.findViewById(R.id.pdvPrimaryInfo_linearLayout_priceLayout)
    var ratingLayout: LinearLayout = itemView.findViewById(R.id.pdvPrimaryInfo_linearLayout_ratingLayout)
    var brandLayout: LinearLayout = itemView.findViewById(R.id.pdvPrimaryInfo_linearLayout_brandLayout)
}
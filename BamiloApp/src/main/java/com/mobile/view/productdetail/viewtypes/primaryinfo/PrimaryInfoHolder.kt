package com.mobile.view.productdetail.viewtypes.primaryinfo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RatingBar
import android.widget.RelativeLayout
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class PrimaryInfoHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var title = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_title)
    var averageScore = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_averageScore)
    var scoreCount = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_scoreCount)
    var oldPrice = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_oldPrice)
    var currentPrice = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_currentPrice)
    var discountPercentage = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_discountPercentage)
    var discountBenefit = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_discountBenefit)
    var currency = itemView!!.findViewById<TextView>(R.id.pdvPrimaryInfo_textView_currency)

    var ratingBar = itemView!!.findViewById<RatingBar>(R.id.pdvPrimaryInfo_ratingBar)
    var discountPercentageRoot = itemView!!.findViewById<RelativeLayout>(R.id.pdvPrimaryInfo_relativeLayout_discountPercentageRoot)
}
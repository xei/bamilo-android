package com.mobile.view.productdetail.viewtypes.review

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RatingBar
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/3/2018.
 * contact farshidabazari@gmail.com
 */
class ReviewItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var more: TextView = itemView!!.findViewById(R.id.pdvReview_textView_more)
    var moreGradient: View = itemView!!.findViewById(R.id.pdvReview_view_moreButtonGradient)

    var comment: TextView = itemView!!.findViewById(R.id.pdvReview_textView_comment)
    var date: TextView = itemView!!.findViewById(R.id.pdvReview_textView_date)
    var title: TextView = itemView!!.findViewById(R.id.pdvReview_textView_title)

    var ratingBar: RatingBar = itemView!!.findViewById(R.id.pdvReview_ratingBar)
}
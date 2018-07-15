package com.mobile.view.productdetail.viewtypes.review

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
class ReviewsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var rate: TextView = itemView!!.findViewById(R.id.pdvReview_textView_rate)
    var total: TextView = itemView!!.findViewById(R.id.pdvReview_textView_total)
    var maxRate: TextView = itemView!!.findViewById(R.id.pdvReview_textView_maxRate)

    var reviewsRecycler: RecyclerView = itemView!!.findViewById(R.id.pdvReview_recyclerView_reviews)
    var addReview: LinearLayout = itemView!!.findViewById(R.id.pdvReview_linearLayout_addReview)
}
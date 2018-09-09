package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.review

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
class ReviewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isFilled = false
    var rate: TextView = itemView.findViewById(R.id.pdvReview_textView_rate)
    var total: TextView = itemView.findViewById(R.id.pdvReview_textView_total)
    var maxRate: TextView = itemView.findViewById(R.id.pdvReview_textView_maxRate)

    var reviewsRecycler: RecyclerView = itemView.findViewById(R.id.pdvReview_recyclerView_reviews)

    var addReview: LinearLayout = itemView.findViewById(R.id.pdvReview_linearLayout_addReview)
    var showAllReviews: LinearLayout = itemView.findViewById(R.id.pdvReview_linearLayout_seeAllReviews)

    var productRateLayout: ConstraintLayout = itemView.findViewById(R.id.pdvReview_constraintLayout_rateLayout)

    var viewDivider: View = itemView.findViewById(R.id.pdvReview_view_divider)
}
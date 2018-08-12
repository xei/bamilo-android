package com.mobile.view.productdetail.viewtypes.review

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RatingBar
import com.bamilo.modernbamilo.customview.XeiTextView
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/3/2018.
 * contact farshidabazari@gmail.com
 */
class ReviewItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var moreGradient: View = itemView.findViewById(R.id.pdvReview_view_moreButtonGradient)

    var more: TextView = itemView.findViewById(R.id.pdvReview_textView_more)
    var comment: TextView = itemView.findViewById(R.id.pdvReview_textView_comment)
    var date: XeiTextView = itemView.findViewById(R.id.pdvReview_textView_date)
    var title: TextView = itemView.findViewById(R.id.pdvReview_textView_title)
    var authorName: XeiTextView = itemView.findViewById(R.id.pdvReview_xeiTextView_authorName)
    var hasUserBeenBoughtText : XeiTextView = itemView.findViewById(R.id.pdvReview_xeiTextView_hasUserBeenBought)

    var ratingBar: RatingBar = itemView.findViewById(R.id.pdvReview_ratingBar)

    var hasUserBeenBoughtImage : AppCompatImageView = itemView.findViewById(R.id.pdvReview_imageView_hasUserBeenBought)
}
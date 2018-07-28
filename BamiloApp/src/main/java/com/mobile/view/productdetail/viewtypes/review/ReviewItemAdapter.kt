package com.mobile.view.productdetail.viewtypes.review

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.service.utils.TextUtils
import com.mobile.utils.ui.UIUtils.spToPx
import com.mobile.view.R
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.model.Review


/**
 * Created by Farshid
 * since 7/3/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_review, holder = ReviewItemHolder::class)
class ReviewItemAdapter(var review: Review, var pdvMainView: PDVMainView, var reviewsCount: Int) {
    @Binder
    public fun binder(holder: ReviewItemHolder) {
        holder.date.text = review.date
        holder.comment.text = review.comment
        holder.title.text = review.username

        review.rate?.let {
            holder.ratingBar.rating = it.toFloat()
        }

        if (reviewsCount > 1) {
            setupViewWidth(holder)
        }

        setUpMoreButton(holder, review.comment)

        holder.more.setOnClickListener { gotoReviewView() }
    }

    private fun setupViewWidth(holder: ReviewItemHolder) {
        holder.itemView.layoutParams.width = getCardSize(holder)
    }

    private fun getCardSize(holder: ReviewItemHolder): Int {
        val deviceWidth = getDeviceWidth(holder.itemView.context)
        return deviceWidth - (deviceWidth / 7)
    }

    private fun setUpMoreButton(holder: ReviewItemHolder, comment: String?) {
        if (TextUtils.isEmpty(comment)) {
            return
        }

        val bounds = Rect()
        val paint = Paint()

        paint.textSize = spToPx(15f, holder.itemView.context).toFloat()
        paint.getTextBounds(comment, 0, comment!!.length, bounds)

        val currentSize = getCardSize(holder)
        val numLines = Math.ceil((bounds.width().toFloat() / currentSize).toDouble()).toInt()

        if (numLines > 4) {
            holder.more.visibility = View.VISIBLE
            holder.moreGradient.visibility = View.VISIBLE
        } else {
            holder.more.visibility = View.GONE
            holder.moreGradient.visibility = View.GONE
        }
    }

    private fun getDeviceWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun gotoReviewView() {
        pdvMainView.onShowSpecificComment(review)
    }
}
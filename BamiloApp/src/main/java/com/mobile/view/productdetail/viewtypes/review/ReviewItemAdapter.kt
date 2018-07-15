package com.mobile.view.productdetail.viewtypes.review

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R
import com.mobile.view.productdetail.model.Review


/**
 * Created by Farshid
 * since 7/3/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_review, holder = ReviewItemHolder::class)
class ReviewItemAdapter(var review: Review) {
    @Binder
    public fun binder(holder: ReviewItemHolder) {
        holder.date.text = review.date
        holder.comment.text = review.comment
        holder.title.text = review.username
        holder.ratingBar.rating = review.rate.toFloat()

        setupViewWidth(holder)
        setUpMoreButton(holder, review.comment)

        holder.more.setOnClickListener { gotoReviewView(holder) }
    }

    private fun setupViewWidth(holder: ReviewItemHolder) {
        holder.itemView.layoutParams.width = getCardSize(holder)
    }

    private fun getCardSize(holder: ReviewItemHolder): Int{
        val deviceWidth = getDeviceWidth(holder.itemView.context)
        return deviceWidth - (deviceWidth / 7)
    }

    private fun setUpMoreButton(holder: ReviewItemHolder, comment: String) {
        val bounds = Rect()
        val paint = Paint()

        paint.textSize = spToPx(12f, holder.itemView.context).toFloat()
        paint.getTextBounds(comment, 0, comment.length, bounds)

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

    private fun spToPx(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    private fun getDeviceWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun gotoReviewView(holder: ReviewItemHolder) {
    }
}
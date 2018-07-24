package com.mobile.view.productdetail.viewtypes.review

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PagerSnapHelper
import com.bamilo.modernbamilo.product.comment.submit.startSubmitRateActivity
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.view.R
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.model.Reviews

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_reviews, holder = ReviewsHolder::class)
class ReviewsItem(var reviews: Reviews, var sku: String, var pdvMainView: PDVMainView) {

    private var adapter = GhostAdapter()
    private var recyclerItems = ArrayList<Any>()

    lateinit var holder: ReviewsHolder

    @Binder
    public fun binder(holder: ReviewsHolder) {
        this.holder = holder

        holder.run {
            total.text = reviews.total.toString()
            maxRate.text = itemView.context.getString(R.string.of_number, 5)

            if (reviews.average.toInt().toFloat() == reviews.average) {
                rate.text = reviews.average.toInt().toString()
            } else {
                rate.text = reviews.average.toString().replace(".", "/")
            }

            addReview.setOnClickListener { showAddReviewActivity(itemView.context) }
            showAllReviews.setOnClickListener { pdvMainView.onShowAllReviewsClicked() }
        }

        setupRecyclerView()
        showReviews()
    }

    private fun setupRecyclerView() {
        holder.reviewsRecycler.adapter = adapter
        holder.reviewsRecycler.layoutManager = GridLayoutManager(holder.itemView.context,
                1, GridLayoutManager.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(holder.reviewsRecycler)
    }

    private fun showReviews() {
        for (review in reviews.items) {
            recyclerItems.add(ReviewItemAdapter(review))
        }

        adapter.setItems(recyclerItems)
    }

    private fun showAddReviewActivity(context: Context) {
        startSubmitRateActivity(context, sku)
    }
}
package com.mobile.view.productdetail.viewtypes.review

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import com.bamilo.modernbamilo.product.comment.submit.startSubmitRateActivity
import com.bamilo.modernbamilo.util.getMorphNumberString
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
class ReviewsItem(private var reviews: Reviews, var sku: String, private var pdvMainView: PDVMainView) {

    private var adapter = GhostAdapter()
    private var recyclerItems = ArrayList<Any>()

    lateinit var holder: ReviewsHolder

    @Binder
    public fun binder(holder: ReviewsHolder) {
        if (holder.isFilled) {
            return
        }
        this.holder = holder

        setupRecyclerView()
        if (reviews.total > 0) {
            showReviews()
        } else {
            showRateToProduct()
        }

        holder.addReview.setOnClickListener { showAddReviewActivity(holder.itemView.context) }
        holder.showAllReviews.setOnClickListener { pdvMainView.onShowAllReviewsClicked() }
        holder.isFilled = true
    }

    private fun setupRecyclerView() {
        holder.reviewsRecycler.adapter = adapter
        holder.reviewsRecycler.layoutManager = GridLayoutManager(holder.itemView.context,
                1, GridLayoutManager.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(holder.reviewsRecycler)
    }

    private fun showReviews() {
        visibleReviewViews()

        holder.total.text = reviews.total.toString()
        holder.maxRate.text = holder.itemView.context.getString(R.string.of_number, 5)
        holder.rate.text = getMorphNumberString(reviews.average)

        recyclerItems.clear()
        adapter.removeAll()
        val size = reviews.items.size
        for (review in reviews.items) {
            recyclerItems.add(ReviewItemAdapter(review, pdvMainView, size))
        }

        adapter.setItems(recyclerItems)
    }

    private fun showRateToProduct() {
        inVisibleReviewViews()
        recyclerItems.clear()
        adapter.removeAll()

        recyclerItems.add(AddReviewItem(sku))
        adapter.setItems(recyclerItems)
    }

    private fun visibleReviewViews() {
        holder.productRateLayout.visibility = View.VISIBLE
        holder.showAllReviews.visibility = View.VISIBLE
        holder.viewDivider.visibility = View.VISIBLE
        holder.maxRate.visibility = View.VISIBLE
        holder.total.visibility = View.VISIBLE
        holder.rate.visibility = View.VISIBLE
    }

    private fun inVisibleReviewViews() {
        holder.productRateLayout.visibility = View.GONE
        holder.showAllReviews.visibility = View.GONE
        holder.viewDivider.visibility = View.GONE
        holder.maxRate.visibility = View.GONE
        holder.total.visibility = View.GONE
        holder.rate.visibility = View.GONE
    }

    private fun showAddReviewActivity(context: Context) {
        startSubmitRateActivity(context, sku)
    }
}
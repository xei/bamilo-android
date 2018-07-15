package com.mobile.view.productdetail.viewtypes.review

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.view.R
import com.mobile.view.productdetail.model.Reviews

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_reviews, holder = ReviewsHolder::class)
class ReviewsItem(var reviews: Reviews) {
    private var adapter = GhostAdapter()
    private var recyclerItems = ArrayList<Any>()

    lateinit var holder: ReviewsHolder

    @Binder
    public fun binder(holder: ReviewsHolder) {
        this.holder = holder

        holder.total.text = reviews.total.toString()
        holder.maxRate.text = holder.itemView.context.getString(R.string.of_number, 5)

        holder.rate.text = reviews.average.toString().replace(".", "/")

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
}
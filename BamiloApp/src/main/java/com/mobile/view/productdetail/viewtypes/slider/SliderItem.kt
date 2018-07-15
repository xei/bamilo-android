package com.mobile.view.productdetail.viewtypes.slider

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R
import com.mobile.view.productdetail.model.ImageSliderModel
import com.mobile.view.productdetail.slider.DepthPageTransformer
import com.mobile.view.productdetail.slider.ProductSliderPagerAdapter
import com.mobile.view.productdetail.slider.SliderPresenter


/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_slider, holder = SliderHolder::class)
class SliderItem(private var supportFragmentManager: FragmentManager,
                 private var imageSliderModel: ImageSliderModel) {

    private var holder: SliderHolder? = null
    private lateinit var sliderPresenter: SliderPresenter

    @Binder
    public fun binder(holder: SliderHolder) {
        this.holder = holder

        sliderPresenter = SliderPresenter(holder.itemView.context,
                imageSliderModel.productSku,
                imageSliderModel.images)

        setupViewPager(holder)
        holder.like.setOnClickListener { _ ->
            sliderPresenter.onLikeButtonClicked(holder.like)
            holder.like.isChecked = !holder.like.isChecked
            holder.like.playAnimation()
        }
        holder.share.setOnClickListener { _ -> sliderPresenter.shareProduct() }
    }

    private fun setupViewPager(holder: SliderHolder) {
        val pagerAdapter: PagerAdapter = ProductSliderPagerAdapter(supportFragmentManager, imageSliderModel.images)

        holder.viewPager.setPageTransformer(true, DepthPageTransformer())
        holder.viewPager.adapter = pagerAdapter

        sliderPresenter.handleOnViewPagerClicked(holder.viewPager)
    }
}
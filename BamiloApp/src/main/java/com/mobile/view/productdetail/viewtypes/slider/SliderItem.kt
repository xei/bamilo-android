package com.mobile.view.productdetail.viewtypes.slider

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R
import com.mobile.view.productdetail.slider.DepthPageTransformer
import com.mobile.view.productdetail.slider.ProductSliderPagerAdapter

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_slider, holder = SliderHolder::class)
class SliderItem(var supportFragmentManager: FragmentManager,
                 var imageList: ArrayList<String>) {

    @Binder
    public fun binder(holder: SliderHolder) {
        val pagerAdapter: PagerAdapter = ProductSliderPagerAdapter(supportFragmentManager, imageList)
        holder.viewPager.setPageTransformer(true, DepthPageTransformer())
        holder.viewPager.adapter = pagerAdapter

        holder.like.setOnClickListener { _ -> {} }
        holder.share.setOnClickListener { _ -> {} }
    }
}
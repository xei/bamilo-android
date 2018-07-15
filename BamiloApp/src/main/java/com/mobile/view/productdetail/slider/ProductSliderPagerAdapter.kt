package com.mobile.view.productdetail.slider

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mobile.view.productdetail.model.ImageList

/**
 * Created by Farshid since 6/13/2018. contact farshidabazari@gmail.com
 */
class ProductSliderPagerAdapter(fm: FragmentManager, private var images: ImageList?) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return ProductImageSlidePageFragment().newInstance(images!!.image_list[position].medium)
    }

    override fun getCount(): Int {
        if (images == null) {
            return 0
        }
        return images!!.image_list.size
    }
}

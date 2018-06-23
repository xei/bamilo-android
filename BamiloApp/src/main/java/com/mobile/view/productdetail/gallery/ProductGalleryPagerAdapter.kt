package com.mobile.view.productdetail.gallery

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mobile.view.productdetail.model.ImageList

/**
 * Created by Farshid since 6/13/2018. contact farshidabazari@gmail.com
 */
class ProductGalleryPagerAdapter(fm: FragmentManager, private var images: ArrayList<ImageList>?) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return ProductGalleryPageFragment().newInstance(images!![position].large)
    }

    override fun getCount(): Int {
        if (images == null) {
            return 0
        }
        return images!!.size
    }
}

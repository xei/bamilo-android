package com.mobile.view.productdetail.slider

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by Farshid since 6/13/2018. contact farshidabazari@gmail.com
 */
class ProductSliderPagerAdapter(fm: FragmentManager, var i: Int) : FragmentStatePagerAdapter(fm) {
    var images: ArrayList<String> = ArrayList()

    init {
        images.add("https://media.bamilo.com/p/honor-1601-6634413-1-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-2-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-2-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-3-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-4-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-5-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-6-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-7-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-8-product.jpg")
    }

    override fun getItem(position: Int): Fragment? {
        return ProductImageSlidePageFragment().newInstance(images[position])
    }

    override fun getCount(): Int {
        return i
    }
}

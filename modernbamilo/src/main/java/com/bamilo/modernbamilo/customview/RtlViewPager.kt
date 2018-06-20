package com.bamilo.modernbamilo.customview

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet

open class RtlViewPager : ViewPager {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        currentItem = getAdapter()!!.count - 1
    }
}

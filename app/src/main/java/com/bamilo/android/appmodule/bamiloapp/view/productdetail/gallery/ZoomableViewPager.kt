package com.bamilo.android.appmodule.bamiloapp.view.productdetail.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.view.MotionEvent

/**
 * Created by Farshid
 * since 6/18/2018.
 * contact farshidabazari@gmail.com
 */
class ZoomableViewPager(context: Context) : ViewPager(context) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (e: Exception) {

        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (e: Exception) {

        }
        return false
    }
}
package com.mobile.view.productdetail.viewtypes.slider

import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class SliderHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var viewPager = itemView!!.findViewById<ViewPager>(R.id.contentProductSlider_viewPager)
    var share = itemView!!.findViewById<AppCompatImageView>(R.id.pdvSlider_appImageView_share)
    var like = itemView!!.findViewById<AppCompatImageView>(R.id.pdvSlider_appImageView_like)
}
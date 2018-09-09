package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.slider

import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bamilo.android.framework.components.widget.likebutton.SparkButton
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class SliderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isFilled = false
    var viewPager: ViewPager = itemView.findViewById(R.id.contentProductSlider_viewPager)
    var share: AppCompatImageView = itemView.findViewById(R.id.pdvSlider_appImageView_share)
    var like: SparkButton = itemView.findViewById(R.id.pdvSlider_appImageView_like)
}
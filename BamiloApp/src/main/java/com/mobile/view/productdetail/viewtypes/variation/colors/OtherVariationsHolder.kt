package com.mobile.view.productdetail.viewtypes.variation.colors

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class OtherVariationsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var image: AppCompatImageView = itemView.findViewById(R.id.pdvColor_appImageView_Image)
    var progressBar: ProgressBar = itemView.findViewById(R.id.pdvColor_progressBar)
    var rootLayout: CardView = itemView.findViewById(R.id.pdvColor_cardView_rootLayout)
}
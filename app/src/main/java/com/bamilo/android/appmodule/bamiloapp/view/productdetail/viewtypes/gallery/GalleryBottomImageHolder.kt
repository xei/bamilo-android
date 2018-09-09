package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.gallery

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/19/2018.
 * contact farshidabazari@gmail.com
 */
class GalleryBottomImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isSelected = false

    var progressBar: ProgressBar = itemView.findViewById(R.id.pdvGalleryBottomImage_progressBar)
    var image: AppCompatImageView = itemView.findViewById(R.id.pdvGalleryBottomImage_appImageView_Image)
    var rootLayout: RelativeLayout = itemView.findViewById(R.id.pdvGalleryBottomImage_relativeView_rootLayout)
}
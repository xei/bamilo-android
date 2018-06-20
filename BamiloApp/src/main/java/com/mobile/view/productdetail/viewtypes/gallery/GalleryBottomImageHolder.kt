package com.mobile.view.productdetail.viewtypes.gallery

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/19/2018.
 * contact farshidabazari@gmail.com
 */
class GalleryBottomImageHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var progressBar = itemView!!.findViewById<ProgressBar>(R.id.pdvGalleryBottomImage_progressBar)
    var image = itemView!!.findViewById<AppCompatImageView>(R.id.pdvGalleryBottomImage_appImageView_Image)
}
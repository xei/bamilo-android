package com.mobile.utils.headerandmorebutton.morebutton

import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/25/2018.
 * contact farshidabazari@gmail.com
 */
class SeeMoreButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.moreButton_textView_title)
}
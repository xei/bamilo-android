package com.bamilo.android.appmodule.bamiloapp.utils.headerandmorebutton.morebutton

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 7/25/2018.
 * contact farshidabazari@gmail.com
 */
class SeeMoreButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.moreButton_textView_title)
}
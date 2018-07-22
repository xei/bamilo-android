package com.mobile.view.productdetail.viewtypes.recyclerheader

import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class RecyclerHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.recyclerViewHeader_textView_title)
    var more: TextView = itemView.findViewById(R.id.recyclerViewHeader_textView_more)
}
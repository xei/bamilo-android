package com.mobile.view.productdetail.viewtypes.variation.size

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class VariationsSizeHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var rootLayout: LinearLayout = itemView!!.findViewById(R.id.pdvSize_linearLayout_rootLayout)
    var title: TextView = itemView!!.findViewById(R.id.pdvSize_textView_title)
}
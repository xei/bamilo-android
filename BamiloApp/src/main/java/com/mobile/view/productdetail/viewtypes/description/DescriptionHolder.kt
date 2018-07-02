package com.mobile.view.productdetail.viewtypes.description

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/25/2018.
 * contact farshidabazari@gmail.com
 */
class DescriptionHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var shortDescription : TextView = itemView!!.findViewById(R.id.pdvDescription_textView_shortDescription)
    var showFullDescription : LinearLayout = itemView!!.findViewById(R.id.pdvDescription_linearLayout_showFullDescription)
}
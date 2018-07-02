package com.mobile.view.productdetail.viewtypes.specifications

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
class SpecificationHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var specification: TextView = itemView!!.findViewById(R.id.pdvSpecification_textView_shortSpecification)
    var showFullSpecification: LinearLayout = itemView!!.findViewById(R.id.pdvSpecification_linearLayout_showFullSpecification)
}
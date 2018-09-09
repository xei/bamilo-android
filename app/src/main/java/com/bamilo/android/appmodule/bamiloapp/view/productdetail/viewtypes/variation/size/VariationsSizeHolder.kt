package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.variation.size

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class VariationsSizeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var rootLayout: LinearLayout = itemView.findViewById(R.id.pdvSize_linearLayout_rootLayout)
    var title: TextView = itemView.findViewById(R.id.pdvSize_textView_title)
}
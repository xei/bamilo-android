package com.mobile.view.productdetail.viewtypes.variation

import android.support.v7.widget.CardView
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
class VariationsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var colorTitle: TextView = itemView!!.findViewById(R.id.pdvVariations_textView_colorTitle)
    var sizeTitle: TextView = itemView!!.findViewById(R.id.pdvVariations_textView_sizeTitle)

    var sizeHelp: LinearLayout = itemView!!.findViewById(R.id.pdvVariations_linearLayout_sizeHelp)

    var sizesRecyclerView: RecyclerView = itemView!!.findViewById(R.id.pdvVariations_recyclerView_size)
    var colorsRecyclerView: RecyclerView = itemView!!.findViewById(R.id.pdvVariations_recyclerView_colors)

    var othersRoot: LinearLayout = itemView!!.findViewById(R.id.pdvVariations_linearLayout_othersRoot)
    var sizeRoot: LinearLayout = itemView!!.findViewById(R.id.pdvVariations_linearLayout_sizeRoot)

    var parentView : CardView = itemView!!.findViewById(R.id.pdvVariations_cardView_root)
}
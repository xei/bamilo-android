package com.bamilo.android.appmodule.bamiloapp.utils.headerandmorebutton.recyclerheader

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
class RecyclerHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.recyclerViewHeader_textView_title)
    var more: TextView = itemView.findViewById(R.id.recyclerViewHeader_textView_more)
}
package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.breadcrumbs

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 7/22/2018.
 * contact farshidabazari@gmail.com
 */
class BreadCrumbHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var chevronImage: AppCompatImageView = itemView.findViewById(R.id.breadcrumbs_appImageView_chevron)
    var title: TextView = itemView.findViewById(R.id.breadcrumbs_textView_title)
}
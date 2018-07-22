package com.mobile.view.productdetail.viewtypes.breadcrumbs

import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.view.R

/**
 * Created by Farshid
 * since 7/22/2018.
 * contact farshidabazari@gmail.com
 */
class BreadCrumbListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var breadCrumbsRecycler: RecyclerView = itemView.findViewById(R.id.breadcrumbs_recyclerView)
}
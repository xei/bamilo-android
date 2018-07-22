package com.mobile.view.productdetail.viewtypes.returnpolicy

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
class ReturnPolicyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.returnPolicy_textView_returnPolicyText)
    var icon: AppCompatImageView = itemView.findViewById(R.id.returnPolicy_appImageView_returnPolicyIcon)
}
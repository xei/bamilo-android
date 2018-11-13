package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.returnpolicy

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView;
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
class ReturnPolicyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isFilled = false
    var view: View = itemView.findViewById(R.id.returnPolicy_cardView_view)
    var title: TextView = itemView.findViewById(R.id.returnPolicy_textView_returnPolicyText)
    var icon: AppCompatImageView = itemView.findViewById(R.id.returnPolicy_appImageView_returnPolicyIcon)
}
package com.mobile.view.productdetail.viewtypes.breadcrumbs

import android.content.Context
import android.content.Intent
import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.MainFragmentActivity
import com.mobile.view.R
import com.mobile.view.productdetail.model.Breadcrumbs

/**
 * Created by Farshid
 * since 7/22/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_breadcrumbs, holder = BreadCrumbHolder::class)
class BreadcrumbItem(var breadcrumbs: Breadcrumbs, var isLastItem: Boolean) {
    @Binder
    public fun binder(holder: BreadCrumbHolder) {
        holder.title.text = breadcrumbs.title

        if (isLastItem) {
            holder.chevronImage.visibility = View.GONE
        } else {
            holder.chevronImage.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener { showTargetView(holder.itemView.context) }
    }

    private fun showTargetView(context: Context?) {
        val intent = Intent(context!!, MainFragmentActivity::class.java).apply {
            putExtra("bread_crumb_target", breadcrumbs.target)
            putExtra("bread_crumb_title", breadcrumbs.title)
        }
        context.startActivity(intent)
    }
}
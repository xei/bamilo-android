package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.breadcrumbs

import android.content.Context
import android.content.Intent
import android.view.View
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Breadcrumbs
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder

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
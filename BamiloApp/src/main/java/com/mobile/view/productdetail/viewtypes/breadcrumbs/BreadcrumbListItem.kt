package com.mobile.view.productdetail.viewtypes.breadcrumbs

import android.support.v7.widget.LinearLayoutManager
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.view.R
import com.mobile.view.productdetail.model.Breadcrumbs

/**
 * Created by Farshid
 * since 7/22/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_breadcrumb_list, holder = BreadCrumbListHolder::class)
class BreadcrumbListItem(private var breadcrumbs: ArrayList<Breadcrumbs>) {
    @Binder
    public fun binder(holder: BreadCrumbListHolder) {
        val adapter = GhostAdapter()
        val items = arrayListOf<Any>()

        holder.breadCrumbsRecycler.adapter = adapter
        holder.breadCrumbsRecycler.layoutManager =
                LinearLayoutManager(holder.itemView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false)

        for (i in 0 until breadcrumbs.size) {
            items.add(BreadcrumbItem(breadcrumbs[i], i == breadcrumbs.size - 1))
        }

        adapter.setItems(items)
        holder.breadCrumbsRecycler.scrollToPosition(adapter.itemCount - 1)
    }
}
package com.mobile.utils.headerandmorebutton.recyclerheader

import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.recycler_view_header, holder = RecyclerHeaderHolder::class)
class RecyclerHeaderItem(private var title: String,
                         private var showMoreItem: Boolean = false,
                         private var moreTitle: String = "",
                         private var clickListener: View.OnClickListener? = null) {
    @Binder
    public fun binder(holder: RecyclerHeaderHolder) {
        if (showMoreItem) {
            holder.more.visibility = View.VISIBLE
            holder.more.text = moreTitle
        }
        holder.title.text = title

        holder.more.setOnClickListener { v ->
            if (clickListener != null) {
                clickListener!!.onClick(v)
            }
        }
    }
}
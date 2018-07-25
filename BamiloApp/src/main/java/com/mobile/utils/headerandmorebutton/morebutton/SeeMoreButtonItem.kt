package com.mobile.utils.headerandmorebutton.morebutton

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R
import com.mobile.utils.OnItemClickListener

/**
 * Created by Farshid
 * since 7/25/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.see_more_button, holder = SeeMoreButtonHolder::class)
class SeeMoreButtonItem(var text: String, var onItemClickListener: OnItemClickListener) {
    @Binder
    public fun binder(holder: SeeMoreButtonHolder) {
        holder.title.text = text
        holder.title.setOnClickListener { onItemClickListener.onItemClicked(null) }
    }
}
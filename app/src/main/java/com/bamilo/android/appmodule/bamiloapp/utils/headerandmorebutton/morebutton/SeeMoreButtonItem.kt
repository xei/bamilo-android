package com.bamilo.android.appmodule.bamiloapp.utils.headerandmorebutton.morebutton

import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.OnItemClickListener
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder

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
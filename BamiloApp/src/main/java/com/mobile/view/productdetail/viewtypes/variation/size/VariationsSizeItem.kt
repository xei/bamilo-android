package com.mobile.view.productdetail.viewtypes.variation.size

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_size, holder = VariationsSizeHolder::class)
class VariationsSizeItem(var size: String) {
    @Binder
    public fun binder(holder: VariationsSizeHolder) {
        holder.title.text = size
    }
}
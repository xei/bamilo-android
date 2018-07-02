package com.mobile.view.productdetail.viewtypes.description

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/25/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_description, holder = DescriptionHolder::class)
class DescriptionItem(private var shortDescription: String, private var sku: String) {
    @Binder
    public fun binder(holder: DescriptionHolder) {
        holder.shortDescription.text = shortDescription
        holder.showFullDescription.setOnClickListener { showFullDescription(holder) }
    }

    private fun showFullDescription(holder: DescriptionHolder) {

    }
}
package com.mobile.view.productdetail.viewtypes.specifications

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/25/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_specification, holder = SpecificationHolder::class)
class SpecificationItem(var specification: String, sku: String) {
    @Binder
    public fun binder(holder: SpecificationHolder) {
        holder.specification.text = specification
        holder.showFullSpecification.setOnClickListener { showFullSpecification(holder) }
    }

    private fun showFullSpecification(holder: SpecificationHolder) {
    }
}
package com.mobile.view.productdetail.viewtypes.primaryinfo

import android.graphics.Paint
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_primary_info, holder = PrimaryInfoHolder::class)
class PrimaryInfoItem() {
    @Binder
    public fun binder(primaryInfoHolder: PrimaryInfoHolder) {
        primaryInfoHolder.oldPrice.paintFlags = primaryInfoHolder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}
package com.mobile.view.productdetail.viewtypes.returnpolicy

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R
import com.mobile.view.productdetail.model.ReturnPolicy

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_return_policy, holder = ReturnPolicyHolder::class)
class ReturnPolicyItem(private var returnPolicy: ReturnPolicy) {
    @Binder
    public fun binder(holder: ReturnPolicyHolder) {
        if (holder.isFilled) {
            return
        }
        ImageManager.getInstance().loadImage(returnPolicy.icon,
                holder.icon,
                null,
                R.drawable.no_image_small,
                false)

        holder.title.text = returnPolicy.title
        holder.isFilled = true
    }
}
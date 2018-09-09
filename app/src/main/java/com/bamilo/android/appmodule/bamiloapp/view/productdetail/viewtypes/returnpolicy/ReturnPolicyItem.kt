package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.returnpolicy

import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ReturnPolicy

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
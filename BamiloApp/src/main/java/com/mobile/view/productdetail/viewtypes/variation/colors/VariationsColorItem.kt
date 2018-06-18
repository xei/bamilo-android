package com.mobile.view.productdetail.viewtypes.variation.colors

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_color, holder = VariationsColorHolder::class)
class VariationsColorItem(var imageUrl: String) {

    @Binder
    public fun binder(holder: VariationsColorHolder) {
        ImageManager.getInstance().loadImage(imageUrl, holder.image, holder.progressBar, R.drawable.no_image_small, false)
    }
}
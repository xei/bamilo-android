package com.mobile.view.productdetail.viewtypes.gallery

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R
import com.mobile.view.productdetail.OnItemClickListener

/**
 * Created by Farshid
 * since 6/19/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_gallery_bottom_image, holder = GalleryBottomImageHolder::class)
class GalleryBottomImageItem(var imageUrl: String, var onItemClickListener: OnItemClickListener) {
    @Binder
    public fun binder(holder: GalleryBottomImageHolder) {
        ImageManager.getInstance().loadImage(imageUrl,
                holder.image,
                holder.progressBar,
                R.drawable.no_image_small,
                false)

        holder.itemView.setOnClickListener { onItemClickListener.onItemClicked(holder.adapterPosition) }
    }
}
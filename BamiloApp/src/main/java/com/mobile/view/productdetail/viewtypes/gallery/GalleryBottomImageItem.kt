package com.mobile.view.productdetail.viewtypes.gallery

import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.utils.OnItemClickListener
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R

/**
 * Created by Farshid
 * since 6/19/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_gallery_bottom_image, holder = GalleryBottomImageHolder::class)
class GalleryBottomImageItem(var imageUrl: String, private var selectImage: Boolean, private var onItemClickListener: OnItemClickListener) {
    private lateinit var holder: GalleryBottomImageHolder
    @Binder
    public fun binder(holder: GalleryBottomImageHolder) {
        this.holder = holder

        ImageManager.getInstance().loadImage(imageUrl,
                holder.image,
                holder.progressBar,
                R.drawable.no_image_small,
                false)

        holder.itemView.setOnClickListener {
            if (!holder.isSelected) {
                onItemClickListener.onItemClicked(holder.adapterPosition)
                selectImage()
            }
        }

        if (holder.isSelected) {
            selectImage()
        } else {
            deSelectImage()
        }

        if (selectImage) {
            selectImage()
        }
    }

    public fun selectImage() {
        if (!::holder.isInitialized) {
            return
        }

        holder.isSelected = true
        holder.rootLayout.setBackgroundResource(R.drawable.round_stroke_orange_4)
    }

    public fun deSelectImage() {
        if (!::holder.isInitialized) {
            return
        }

        holder.isSelected = false
        holder.rootLayout.setBackgroundResource(0)
    }
}
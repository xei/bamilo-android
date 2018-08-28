package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.variation.colors

import android.graphics.drawable.Drawable
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.model.SimpleProduct
import com.bamilo.android.appmodule.bamiloapp.utils.OnItemClickListener

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */

@BindItem(layout = R.layout.content_pdv_other_variation, holder = OtherVariationsHolder::class)
class OtherVariationsItem(var product: SimpleProduct, private var onItemClickListener: OnItemClickListener) {
    private lateinit var cardViewDefaultBackground: Drawable
    private lateinit var holder: OtherVariationsHolder

    @Binder
    public fun binder(holder: OtherVariationsHolder) {
        this.holder = holder

        ImageManager.getInstance().loadImage(product.image,
                holder.image,
                holder.progressBar,
                R.drawable.no_image_small,
                false)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClicked(product)
            product.isSelected = true
            selectProduct(holder)
        }

        if (!::cardViewDefaultBackground.isInitialized) {
            if (holder.rootLayout.background !=
                    holder.itemView.context.resources.getDrawable(R.drawable.round_stroke_orange_4)) {
                cardViewDefaultBackground = holder.rootLayout.background
            }
        }

        if (product.isSelected) {
            selectProduct(holder)
        } else {
            deSelectProduct()
        }
    }

    private fun selectProduct(holder: OtherVariationsHolder) {
        holder.rootLayout.setBackgroundResource(R.drawable.round_stroke_orange_4)
    }

    public fun deSelectProduct() {
        product.isSelected = false
        if (::cardViewDefaultBackground.isInitialized) {
            holder.rootLayout.setBackgroundDrawable(cardViewDefaultBackground)
        }
    }
}
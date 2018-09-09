package com.bamilo.android.appmodule.bamiloapp.view.relatedproducts.viewtypes

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView;
import kotlinx.android.synthetic.main._def_home_teaser_image_loadable.view.*
import kotlinx.android.synthetic.main.content_related_products.view.*

/**
 * Created by Farshid
 * since 5/22/2018.
 * contact farshidabazari@gmail.com
 */
class RelatedProductsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var productName: TextView = itemView.contentRelatedProduct_TextView_productName as TextView
    var productBrand: TextView = itemView.contentRelatedProduct_TextView_brand as TextView
    var productPrice: TextView = itemView.contentRelatedProduct_TextView_price as TextView
    var productOldPrice: TextView = itemView.contentRelatedProduct_TextView_oldPrice as TextView
    var productImage: ImageView = itemView.home_teaser_item_image
    var imageLoadingProgress: ProgressBar = itemView.home_teaser_item_progress
}
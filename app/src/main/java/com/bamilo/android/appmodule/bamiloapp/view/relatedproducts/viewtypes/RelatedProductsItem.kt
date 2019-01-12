//package com.bamilo.android.appmodule.bamiloapp.view.relatedproducts.viewtypes
//
//import android.graphics.Paint
//import android.view.View
//import com.emarsys.predict.RecommendedItem
//import com.bamilo.android.framework.components.ghostadapter.BindItem
//import com.bamilo.android.framework.components.ghostadapter.Binder
//import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter
//import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
//import com.bamilo.android.R
//import com.bamilo.android.appmodule.bamiloapp.view.relatedproducts.OnItemClickListener
//import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
//
///**
// * Created by Farshid
// * since 5/22/2018.
// * contact farshidabazari@gmail.com
// */
//@BindItem(layout = R.layout.content_related_products, holder = RelatedProductsHolder::class)
//class RelatedProductsItem(private var mRecommendedItem: RecommendedItem) {
//    private lateinit var mOnItemClickListener: OnItemClickListener
//
//    init {
//    }
//
//    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
//        mOnItemClickListener = onItemClickListener
//    }
//
//    @Binder
//    fun binder(holder: RelatedProductsHolder) {
//        val data = mRecommendedItem.data
//        ImageManager.getInstance().loadImage(data["image"].toString(),
//                holder.productImage,
//                holder.imageLoadingProgress,
//                R.drawable.no_image_large,
//                false)
//
//        holder.productName.text = data["title"].toString()
//        holder.productBrand.text = data["brand"].toString()
//
//        showPrice(holder, data)
//
//        holder.itemView.setOnClickListener { _: View? -> mOnItemClickListener.onItemClicked(mRecommendedItem) }
//    }
//
//    private fun showPrice(holder: RelatedProductsHolder, data: MutableMap<String, Any>) {
//        val special: Double = data["msrp"] as Double
//        val price: Double = data["price"] as Double
//
//        if (price != special) {
//            holder.productPrice.text = CurrencyFormatter.formatCurrency(price).persianizeDigitsInString()
//            holder.productOldPrice.text = CurrencyFormatter.formatCurrency(special).persianizeDigitsInString()
//            holder.productOldPrice.paintFlags = holder.productOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            holder.productOldPrice.visibility = View.VISIBLE
//        } else {
//            holder.productPrice.text = CurrencyFormatter.formatCurrency(price).persianizeDigitsInString()
//            holder.productOldPrice.visibility = View.INVISIBLE
//        }
//    }
//}
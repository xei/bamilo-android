package com.mobile.view.productdetail

import android.content.Context
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.ProductDetail

/**
 * Created by Farshid
 * since 7/1/2018.
 * contact farshidabazari@gmail.com
 */
class ProductDetailPresenter(var context: Context, binding: ActivityProductDetailBinding, pdvMainView: PDVMainView) {

    private var chooseVariationBottomSheetHandler = ChooseVariationBottomSheetHandler(context, binding, pdvMainView)

    fun fillChooseVariationBottomSheet(product: ProductDetail) {
        chooseVariationBottomSheetHandler.fillChooseVariationBottomSheet(product)
    }

    fun showBottomSheet() {
        chooseVariationBottomSheetHandler.showBottomSheet()
    }

    fun hideBottomSheet() {
        chooseVariationBottomSheetHandler.hideBottomSheet()
    }

    fun isBottomSheetShown(): Boolean {
        return chooseVariationBottomSheetHandler.isBottomSheetShown()
    }
}
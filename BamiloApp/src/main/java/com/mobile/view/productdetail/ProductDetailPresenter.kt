package com.mobile.view.productdetail

import android.content.Context
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.PrimaryInfoModel
import com.mobile.view.productdetail.model.Variations

/**
 * Created by Farshid
 * since 7/1/2018.
 * contact farshidabazari@gmail.com
 */
class ProductDetailPresenter(var context: Context, binding: ActivityProductDetailBinding) {
    var chooseVariationBottomSheetHandler = ChooseVariationBottomSheetHandler(context, binding)
    fun fillChooseVariationBottomSheet(primaryInfo: PrimaryInfoModel, imageUrl: String, variations: Variations) {
        chooseVariationBottomSheetHandler.fillChooseVariationBottomSheet(primaryInfo, imageUrl, variations)
    }

    fun showBottomSheet() {
        chooseVariationBottomSheetHandler.showBottomSheet()
    }

    fun hideBottomSheet(){
        chooseVariationBottomSheetHandler.hideBottomSheet()
    }

    fun isBottomSheetShown(): Boolean{
        return chooseVariationBottomSheetHandler.isBottomSheetShown()
    }
}
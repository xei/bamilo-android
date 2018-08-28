package com.bamilo.android.appmodule.bamiloapp.view.productdetail

import android.content.Context
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.model.ProductDetail
import com.bamilo.android.databinding.ActivityProductDetailBinding

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
        return false
        return chooseVariationBottomSheetHandler.isBottomSheetShown()
    }

    fun addToCart(simpleSku: String, iResponseCallback: IResponseCallback) {
        BamiloApplication.INSTANCE.sendRequest(ShoppingCartAddItemHelper(),
                ShoppingCartAddItemHelper.createBundle(simpleSku), iResponseCallback)
    }
}
package com.mobile.view.productdetail

import com.mobile.utils.ui.WarningFactory
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.model.Review
import com.mobile.view.productdetail.model.SimpleProduct

/**
 * Created by Farshid
 * since 7/4/2018.
 * contact farshidabazari@gmail.com
 */
interface PDVMainView {
    fun onBackButtonClicked()
    fun onSizeVariationClicked(sizeVariation: SimpleProduct)
    fun onShowFragment(fragmentTag: ProductDetailActivity.FragmentTag)
    fun onProductReceived(product: ProductDetail)
    fun onOtherVariationClicked(product: SimpleProduct)
    fun onShowOtherSeller()
    fun onShowDesAndSpecPage()
    fun onShowSpecsAndSpecPage()
    fun onShowAllReviewsClicked()
    fun onRelatedProductClicked(sku: String)
    fun showProgressView()
    fun dismissProgressView()
    fun showErrorMessage(@WarningFactory.WarningErrorType warningFact: Int, message: String)
    fun onAddToCartClicked()
    fun onShowSpecificComment(review: Review)
    fun onShowMoreRelatedProducts()
    fun showOutOfStock()
    fun loginUser()
}
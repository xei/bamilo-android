package com.mobile.view.relatedproducts

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.emarsys.predict.RecommendedItem
import com.mobile.constants.ConstantsIntentExtra
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager

/**
 * Created by Farshid
 * since 5/21/2018.
 * contact farshidabazari@gmail.com
 */
class RecommendProductViewModel : ViewModel() {
    private var mProductList: MutableLiveData<List<RecommendedItem>>? = null

    public fun setProductList(productList: List<RecommendedItem>) {
        mProductList?.postValue(productList)
    }

    public fun getProductList(itemId: String, logic: String): LiveData<List<RecommendedItem>> {
        if (mProductList == null) {
            mProductList = MutableLiveData()
            getRecommendProducts(itemId, logic)
        }
        return mProductList as MutableLiveData<List<RecommendedItem>>
    }

    private fun getRecommendProducts(itemId: String, logic: String) {
        when (logic) {
            ConstantsIntentExtra.RELATED -> getRelatedProducts(itemId)
            ConstantsIntentExtra.PERSONAL -> getPersonalProducts(itemId)
            ConstantsIntentExtra.POPULAR -> getPopularProducts(itemId)
        }
    }

    private fun getRelatedProducts(itemId: String) {
        val recommendManager = RecommendManager()
        recommendManager.sendRelatedRecommend(null,
                null,
                itemId,
                null, 100) { _, data -> setProductList(data) }
    }

    private fun getPopularProducts(itemId: String) {
        val recommendManager = RecommendManager()
        recommendManager.sendPopularRecommend(100) { _, data -> setProductList(data) }
    }

    private fun getPersonalProducts(itemId: String) {
        val recommendManager = RecommendManager()
        recommendManager.sendPersonalRecommend(100) { _, data -> setProductList(data) }
    }
}

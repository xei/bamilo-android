//package com.bamilo.android.appmodule.bamiloapp.view.relatedproducts
//
//import android.arch.lifecycle.LiveData
//import android.arch.lifecycle.MutableLiveData
//import android.arch.lifecycle.ViewModel
//import com.emarsys.predict.RecommendedItem
//import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
////import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendManager
//
///**
// * Created by Farshid
// * since 5/21/2018.
// * contact farshidabazari@gmail.com
// */
//class RecommendProductViewModel : ViewModel() {
//    private var mProductList: MutableLiveData<List<RecommendedItem>>? = null
//
//    /**
//     * set and publish product list
//     *
//     * @param productList
//     * */
//    public fun setProductList(productList: List<RecommendedItem>) {
//        mProductList?.postValue(productList)
//    }
//
//    /**
//     * return product list if exist
//     * if not get from recommend manager
//     *
//     * @param itemId the product id to get products related to it
//     * @param logic the logic of recommendation can be popular, personal, related
//     * @see  ConstantsIntentExtra.POPULAR
//     *
//     * @return recommend products
//     *
//     * */
//    public fun getProductList(itemId: String, logic: String): LiveData<List<RecommendedItem>> {
//        if (mProductList == null) {
//            mProductList = MutableLiveData()
//            getRecommendProducts(itemId, logic)
//        }
//        return mProductList as MutableLiveData<List<RecommendedItem>>
//    }
//
//
//    /**
//     * get recommend products base on
//     * @param logic
//     * and
//     * @param itemId
//     * */
//    private fun getRecommendProducts(itemId: String, logic: String) {
//        when (logic) {
//            ConstantsIntentExtra.RELATED -> getRelatedProducts(itemId)
//            ConstantsIntentExtra.PERSONAL -> getPersonalProducts(itemId)
//            ConstantsIntentExtra.POPULAR -> getPopularProducts(itemId)
//        }
//    }
//
//    /**
//     * get related products
//     * return them by calling
//     * @see setProductList
//     * */
//    private fun getRelatedProducts(itemId: String) {
////        val recommendManager = RecommendManager()
////        recommendManager.sendRelatedRecommend(null,
////                null,
////                itemId,
////                null, 100) { _, data -> setProductList(data) }
//    }
//
//    /**
//     * get popular products
//     * return them by calling
//     * @see setProductList
//     * */
//    private fun getPopularProducts(itemId: String) {
////        val recommendManager = RecommendManager()
////        recommendManager.sendPopularRecommend(100) { _, data -> setProductList(data) }
//    }
//
//    /**
//     * get personal products
//     * return them by calling
//     * @see setProductList
//     * */
//    private fun getPersonalProducts(itemId: String) {
////        val recommendManager = RecommendManager()
////        recommendManager.sendPersonalRecommend(100) { _, data -> setProductList(data) }
//    }
//}

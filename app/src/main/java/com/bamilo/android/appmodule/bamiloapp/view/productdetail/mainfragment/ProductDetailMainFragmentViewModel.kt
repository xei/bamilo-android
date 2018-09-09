package com.bamilo.android.appmodule.bamiloapp.view.productdetail.mainfragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager
import com.bamilo.android.framework.service.tracking.TrackingPage
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ProductDetail
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.ProductWebApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Farshid
 * since 7/4/2018.
 * contact farshidabazari@gmail.com
 */
class ProductDetailMainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private var productDetail: MutableLiveData<ProductDetail>? = null

    fun setItems(items: ProductDetail?) {
        productDetail!!.postValue(items)
    }

    fun getItems(productId: String): LiveData<ProductDetail> {
        if (productDetail == null) {
            productDetail = MutableLiveData()
            getProductInfo(productId)
        }
        return productDetail as MutableLiveData<ProductDetail>
    }

    fun loadData(productId: String): LiveData<ProductDetail> {
        if (productDetail == null) {
            return getItems(productId)
        }

        getProductInfo(productId)
        return productDetail as MutableLiveData<ProductDetail>
    }

    private fun getProductInfo(sku: String) {
        val screenModel = BaseScreenModel(
                (getApplication() as Context).getString(TrackingPage.PDV.getName()),
                (getApplication() as Context).getString(R.string.gaScreen),
                sku, System.currentTimeMillis())

        TrackerManager.trackScreenTiming(getApplication(), screenModel)

        RetrofitHelper
                .makeWebApi(getApplication(), ProductWebApi::class.java)
                .getProductDetail(sku)
                .enqueue(object : Callback<ResponseWrapper<ProductDetail>> {
                    override fun onFailure(call: Call<ResponseWrapper<ProductDetail>>?, t: Throwable?) {
                        setItems(null)
                    }

                    override fun onResponse(call: Call<ResponseWrapper<ProductDetail>>?,
                                            response: Response<ResponseWrapper<ProductDetail>>?) {
                        response?.body()?.metadata?.let {
                            setItems(it)
                        }

                        if (response?.body()?.metadata == null) {
                            setItems(null)
                        }
                    }
                })
    }
}
package com.mobile.view.productdetail.mainfragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.network.ProductWebApi
import com.mobile.view.productdetail.network.response.ProductDetailResponse
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
        RetrofitHelper
                .makeWebApi(getApplication(), ProductWebApi::class.java)
                .getProductDetail(sku)
                .enqueue(object : Callback<ResponseWrapper<ProductDetailResponse>> {
                    override fun onFailure(call: Call<ResponseWrapper<ProductDetailResponse>>?, t: Throwable?) {
                        setItems(null)
                    }

                    override fun onResponse(call: Call<ResponseWrapper<ProductDetailResponse>>?,
                                            response: Response<ResponseWrapper<ProductDetailResponse>>?) {
                        if (response == null) {
                            return
                        }
                        if (!response.isSuccessful) {
                            return
                        }

                        if (response.body() == null) {
                            return
                        }
                        setItems(response.body().metadata.data)
                    }
                })
        //get from server
    }
}
/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file acts as entry point to MODEL layer in MVVM architecture and contains a repository of
 * fetched data and also a mapper to build the view model from DTOs.
 */

package com.bamilo.android.appmodule.modernbamilo.product.sellerslist.model

import android.content.Context
import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.model.webservice.GetSellersResponse
import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.model.webservice.SellerProduct
import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.model.webservice.SellersListWebApi
import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.viewmodel.SellersListItemViewModel
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_DEBUG = "SellersListRepository"

class SellersListRepository(context: Context) {

    private val mWebApi = RetrofitHelper.makeWebApi(context, SellersListWebApi::class.java)

    fun getAllSellersList(productId: String, listener: OnSellersListLoadListener) {
        val call = mWebApi.getSellers(productId)
        call.enqueue(object : Callback<ResponseWrapper<GetSellersResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetSellersResponse>>?, response: Response<ResponseWrapper<GetSellersResponse>>?) {
                response?.body()?.metadata?.sellers?.let {
                    listener.onSucceed(buildSellersListItemViewModels(it))
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<GetSellersResponse>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
                listener.onFailure(t?.message.toString())
            }

        })
    }

    /**
     * VIEWMODEL layer uses this interface to listen the repository for data changes.
     * TODO: Think about using LiveData instead and the limitation of testing by JUnit.
     */
    interface OnSellersListLoadListener {
        fun onSucceed(sellersListItemViewModels: ArrayList<SellersListItemViewModel>)
        fun onFailure(msg: String)
    }

    /**
     * This method gets the web API response as DTO and build corresponding View Model to serve to
     * VIEWMODEL layer.
     * This mapping can reduce the complexity of data model and make it more readable.
     */
    private fun buildSellersListItemViewModels(sellerProducts: ArrayList<SellerProduct>): ArrayList<SellersListItemViewModel> =
            ArrayList<SellersListItemViewModel>().apply {
                sellerProducts.forEach {
                    add(SellersListItemViewModel(
                            sku = it.productInfo.sku,
                            title = it.sellerInfo.title,
                            deliveryTime = it.sellerInfo.deliveryTime.str,
                            deliveryTimeEpoch = it.sellerInfo.deliveryTime.epoch,
                            rate = it.sellerInfo.rating.rating,
                            isRateValid = it.sellerInfo.rating.isValid,
                            baseAmount = it.productInfo.amount.baseAmount,
                            payableAmount = it.productInfo.amount.payableAmount,
                            discount = it.productInfo.amount.discountPercentage,
                            currency = it.productInfo.amount.currencySuffix
                    ))
                }
            }
}


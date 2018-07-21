package com.bamilo.modernbamilo.product.sellerslist

import android.content.Context
import com.bamilo.modernbamilo.product.sellerslist.webservice.GetSellersResponse
import com.bamilo.modernbamilo.product.sellerslist.webservice.SellersListWebApi
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_DEBUG = "SellersListRepository"

class SellersListRepository(context: Context) {

    private val mWebApi = RetrofitHelper.makeWebApi(context, SellersListWebApi::class.java)

    fun getAllSellersList(productId: String, listener: OnSellersListLoadListener) {
        val call = mWebApi.getSellers(productId)
        call.enqueue(object: Callback<ResponseWrapper<GetSellersResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetSellersResponse>>?, response: Response<ResponseWrapper<GetSellersResponse>>?) {
                response?.body()?.metadata?.sellers?.let {
                    val sellersListItemViewModels = ArrayList<SellersListItemViewModel>().apply {
                        it.forEach {
                            add(SellersListItemViewModel(
                                    sellerId = it.productInfo.sku,
                                    title = it.sellerInfo.title,
                                    deliveryTime = it.sellerInfo.deliveryTime.str,
                                    rate = it.sellerInfo.rating.rating,
                                    isRateValid = !it.sellerInfo.rating.isValid,
                                    baseAmount = it.productInfo.amount.baseAmount,
                                    payableAmount = it.productInfo.amount.payableAmount,
                                    discount = it.productInfo.amount.discountPercentage,
                                    currency = it.productInfo.amount.currencySuffix
                            ))
                        }
                    }
                    listener.onSucceed(sellersListItemViewModels)
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<GetSellersResponse>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
                listener.onFailure(t?.message.toString())
            }

        })
    }

    interface OnSellersListLoadListener {
        fun onSucceed(sellersListItemViewModels: ArrayList<SellersListItemViewModel>)
        fun onFailure(msg: String)
    }

}
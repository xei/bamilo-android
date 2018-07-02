package com.mobile.view.productdetail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * Created by Farshid since 6/12/2018. contact farshidabazari@gmail.com
 */
class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {
    private var mItems: MutableLiveData<ArrayList<Any>>? = null

    fun setItems(items: ArrayList<Any>) {
        mItems!!.postValue(items)
    }

    fun getItems(productId: String): LiveData<ArrayList<Any>> {
        if (mItems == null) {
            mItems = MutableLiveData()
            getProductInfo(productId)
        }
        return mItems as MutableLiveData<ArrayList<Any>>
    }

    fun loadData(productId: String): LiveData<ArrayList<Any>> {
        if (mItems == null) {
            return getItems(productId)
        }

        getProductInfo(productId)
        return mItems as MutableLiveData<ArrayList<Any>>
    }

    private fun getProductInfo(productId: String) {
        //get from server
    }
}

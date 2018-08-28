package com.bamilo.android.appmodule.bamiloapp.view.productdetail.model

import java.io.Serializable

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
class Variation : Serializable {
    var type: String ? = null
    var title: String ? = null
    var products = arrayListOf<SimpleProduct>()
}
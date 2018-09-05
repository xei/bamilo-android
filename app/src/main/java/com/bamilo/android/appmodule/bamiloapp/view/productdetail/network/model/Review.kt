package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model

import java.io.Serializable

/**
 * Created by Farshid
 * since 7/2/2018.
 * contact farshidabazari@gmail.com
 */
class Review : Serializable {
    var id: String? = null
    var title: String? = null
    var comment: String? = null
    var is_bought_by_user: Boolean = false
    var username: String? = null
    var date: String? = null
    var rate: String? = null
    var dislike: Int = 0
    var like: Int = 0
}
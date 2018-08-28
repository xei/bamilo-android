package com.bamilo.android.appmodule.bamiloapp.view.productdetail.model

import com.bamilo.android.appmodule.bamiloapp.view.productdetail.model.Image

/**
 * Created by Farshid
 * since 6/20/2018.
 * contact farshidabazari@gmail.com
 */
class ImageSliderModel{
    var images = arrayListOf<Image>()
    var productSku : String = ""
    var price : String = ""
    var isWishList : Boolean = false
    var shareUrl : String? = ""
    var category: String = ""
}
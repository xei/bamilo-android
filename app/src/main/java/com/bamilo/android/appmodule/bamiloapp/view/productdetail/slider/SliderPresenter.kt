package com.bamilo.android.appmodule.bamiloapp.view.productdetail.slider

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.view.ViewPager
import android.view.GestureDetector
import android.view.MotionEvent
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.gallery.GalleryActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.ProductWebApi
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Image
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ImageSliderModel
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.bamilo.android.framework.service.pojo.RestConstants
import retrofit2.Callback
import java.lang.Exception

/**
 * Created by Farshid since 6/19/2018. contact farshidabazari@gmail.com
 */
class SliderPresenter(var context: Context,
                      private var productSku: String,
                      private var imageList: ArrayList<Image>,
                      private var pdvMainView: PDVMainView) {

    fun onLikeButtonClicked(imageSliderModel: ImageSliderModel, callBack: Callback<ResponseWrapper<Any>>) {
        if (imageSliderModel.isWishList) {
            removeProductToWishList(callBack)
        } else {
            addProductToWishList(callBack)
        }
    }

    private fun removeProductToWishList(callBack: Callback<ResponseWrapper<Any>>) {
        RetrofitHelper.makeWebApi(context, ProductWebApi::class.java)
                .removeFromWishList(productSku)
                .enqueue(callBack)
    }

    private fun addProductToWishList(callBack: Callback<ResponseWrapper<Any>>) {
        RetrofitHelper.makeWebApi(context, ProductWebApi::class.java)
                .addToWishList(productSku)
                .enqueue(callBack)
    }

    fun shareProduct(productTitle: String, shareUrl: String?) {
        try {
            val extraSubject = context.getString(R.string.share_subject, context.getString(R.string.app_name_placeholder))
            val extraMsg = '\u200f' + productTitle + ' ' + context.getString(R.string.share_checkout_this_product) + "\n\n" + shareUrl

            val shareIntent = createShareIntent(extraSubject, extraMsg)
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_product)))

            EventTracker.shareProduct(productSku)

        } catch (ignore: Exception) {}
    }

    private fun createShareIntent(extraSubject: String, extraText: String): Intent {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, extraSubject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, extraText)
        sharingIntent.putExtra(RestConstants.SKU, productSku)
        return sharingIntent
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleOnViewPagerClicked(viewPager: ViewPager?) {
        val tapGestureDetector = GestureDetector(context, TapGestureListener())
        viewPager!!.setOnTouchListener { _, event ->
            viewPagerTouched(tapGestureDetector, event)
        }
    }

    private fun viewPagerTouched(tapGestureDetector: GestureDetector, event: MotionEvent?): Boolean {
        if (tapGestureDetector.onTouchEvent(event)) {
            return true
        }
        return false
    }

    internal inner class TapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            gotoGalleryActivity()
            return true
        }
    }

    private fun gotoGalleryActivity() {
        GalleryActivity.start(context, imageList)
    }
}

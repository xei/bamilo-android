package com.mobile.view.productdetail.slider

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.view.ViewPager
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.mobile.app.BamiloApplication
import com.mobile.components.widget.likebutton.SparkButton
import com.mobile.helpers.wishlist.AddToWishListHelper
import com.mobile.helpers.wishlist.RemoveFromWishListHelper
import com.mobile.interfaces.IResponseCallback
import com.mobile.service.pojo.BaseResponse
import com.mobile.service.pojo.RestConstants
import com.mobile.view.R
import com.mobile.view.productdetail.gallery.GalleryActivity
import com.mobile.view.productdetail.model.ImageList

/**
 * Created by Farshid since 6/19/2018. contact farshidabazari@gmail.com
 */
class SliderPresenter(var context: Context, private var productSku: String, private var imageList: ImageList) {
    fun onLikeButtonClicked(likeButton: SparkButton) {
        if (BamiloApplication.isCustomerLoggedIn()) {
            loginUser()
        } else {
            if (likeButton.isChecked) {
                removeProductToWishList(likeButton)
            } else {
                addProductToWishList(likeButton)
            }
        }
    }

    private fun loginUser() {
        Toast.makeText(context, R.string.please_login_first, Toast.LENGTH_SHORT).show()
    }

    private fun removeProductToWishList(likeButton: SparkButton) {
        BamiloApplication.INSTANCE.sendRequest(AddToWishListHelper(),
                AddToWishListHelper.createBundle(productSku),
                object : IResponseCallback {
                    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                        likeButton.isChecked = false
                        likeButton.playAnimation()
                    }

                    override fun onRequestError(baseResponse: BaseResponse<*>?) {
                    }
                })
    }

    private fun addProductToWishList(likeButton: SparkButton) {
        BamiloApplication.INSTANCE.sendRequest(RemoveFromWishListHelper(),
                RemoveFromWishListHelper.createBundle(productSku),
                object : IResponseCallback {
                    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                        likeButton.isChecked = true
                        likeButton.playAnimation()
                    }

                    override fun onRequestError(baseResponse: BaseResponse<*>?) {
                    }
                })

    }

    fun shareProduct() {
        try {
            val extraSubject = context.getString(R.string.share_subject, context.getString(R.string.app_name_placeholder))
            val extraMsg = context.getString(R.string.share_checkout_this_product) + "share url"
            val shareIntent = createShareIntent(extraSubject, extraMsg)
            shareIntent.putExtra(RestConstants.SKU, "sku")
            context.startActivity(shareIntent)
        } catch (e: NullPointerException) {
        }
    }

    private fun createShareIntent(extraSubject: String, extraText: String): Intent {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, extraSubject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, extraText)
        return sharingIntent
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleOnViewPagerClicked(viewPager: ViewPager?) {
        val tapGestureDetector = GestureDetector(context, TapGestureListener())
        viewPager!!.setOnTouchListener({ _, event ->
            viewPagerTouched(tapGestureDetector, event)
        })
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

package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.slider

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ImageSliderModel
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.slider.DepthPageTransformer
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.slider.ProductSliderPagerAdapter
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.slider.SliderPresenter
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.framework.service.tracking.TrackingPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_slider, holder = SliderHolder::class)
class SliderItem(private var supportFragmentManager: FragmentManager,
                 private var imageSliderModel: ImageSliderModel,
                 private var pdvMainView: PDVMainView) {

    private var holder: SliderHolder? = null
    private lateinit var sliderPresenter: SliderPresenter

    @Binder
    public fun binder(holder: SliderHolder) {
        if (holder.isFilled) {
            return
        }

        this.holder = holder

        sliderPresenter = SliderPresenter(holder.itemView.context,
                imageSliderModel.productSku,
                imageSliderModel.images,
                pdvMainView)

        holder.like.isChecked = imageSliderModel.isWishList

        setupViewPager(holder)
        bindLikeButtonClickListener(holder)
        holder.share.setOnClickListener { _ -> sliderPresenter.shareProduct(imageSliderModel.title, imageSliderModel.shareUrl) }
        holder.isFilled = true
    }

    private fun bindLikeButtonClickListener(holder: SliderHolder) {
        holder.like.setOnClickListener { _ ->
            if (!BamiloApplication.isCustomerLoggedIn()) {
                pdvMainView.loginUser()
                return@setOnClickListener
            }

            holder.like.isChecked = !imageSliderModel.isWishList
            holder.like.playAnimation()

            sliderPresenter.onLikeButtonClicked(imageSliderModel, object : Callback<ResponseWrapper<Any>> {
                override fun onFailure(call: Call<ResponseWrapper<Any>>?, t: Throwable?) {
                    pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                            holder.itemView.context.getString(R.string.error_occured))

                    holder.like.isChecked = imageSliderModel.isWishList
                }

                override fun onResponse(call: Call<ResponseWrapper<Any>>?, response: Response<ResponseWrapper<Any>>?) {
                    if (imageSliderModel.isWishList) {
                        itemRemovedFromWishList()
                    } else {
                        itemAddedToWishList()
                    }
                }
            })
        }
    }

    private fun itemAddedToWishList() {
        if (holder == null) {
            return
        }

        imageSliderModel.isWishList = true
        holder!!.like.isChecked = true

        pdvMainView.trackAddFromWishList()

        EventTracker.addToWishList(
                id = imageSliderModel.productSku,
                title = imageSliderModel.title,
                amount = imageSliderModel.price.toLong(),
                categoryId = imageSliderModel.category
        )

//        val addToWishListEventModel =
//                MainEventModel(holder!!.itemView.context.getString(TrackingPage.PRODUCT_DETAIL.getName()),
//                        EventActionKeys.ADD_TO_WISHLIST,
//                        imageSliderModel.productSku,
//                        imageSliderModel.price.toLong(),
//                        MainEventModel.createAddToWishListEventModelAttributes(imageSliderModel.productSku,
//                                imageSliderModel.category, true))
//
//        TrackerManager.trackEvent(holder!!.itemView.context,
//                EventConstants.AddToWishList,
//                addToWishListEventModel)
    }

    private fun itemRemovedFromWishList() {
        if (holder == null) {
            return
        }

        pdvMainView.trackRemoveFromWishList()

        imageSliderModel.isWishList = false
        holder!!.like.isChecked = false

        EventTracker.removeFromWishList(
                id = imageSliderModel.productSku,
                title = imageSliderModel.title,
                amount = imageSliderModel.price.toLong(),
                categoryId = imageSliderModel.category
        )

//        val sem = SimpleEventModel().apply {
//            category = holder!!.itemView.context.getString(TrackingPage.PRODUCT_DETAIL.getName())
//            action = EventActionKeys.REMOVE_FROM_WISHLIST
//            label = imageSliderModel.productSku
//            value = imageSliderModel.price.toLong()
//        }
//        TrackerManager.trackEvent(holder!!.itemView.context, EventConstants.RemoveFromWishList, sem)
    }

    private fun setupViewPager(holder: SliderHolder) {
        if (holder.viewPager.adapter != null) {
            return
        }

        val pagerAdapter: PagerAdapter = ProductSliderPagerAdapter(supportFragmentManager,
                ArrayList(imageSliderModel.images.asReversed()))

        with(holder.viewPager) {
            setPageTransformer(true, DepthPageTransformer())
            adapter = pagerAdapter
            currentItem = pagerAdapter.count - 1
        }

        sliderPresenter.handleOnViewPagerClicked(holder.viewPager)
    }
}
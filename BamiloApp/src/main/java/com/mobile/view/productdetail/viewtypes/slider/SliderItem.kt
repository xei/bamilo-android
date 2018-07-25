package com.mobile.view.productdetail.viewtypes.slider

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.mobile.classes.models.MainEventModel
import com.mobile.classes.models.SimpleEventModel
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.constants.tracking.EventActionKeys
import com.mobile.constants.tracking.EventConstants
import com.mobile.managers.TrackerManager
import com.mobile.service.tracking.TrackingPage
import com.mobile.utils.ui.WarningFactory
import com.mobile.view.R
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.model.ImageSliderModel
import com.mobile.view.productdetail.slider.DepthPageTransformer
import com.mobile.view.productdetail.slider.ProductSliderPagerAdapter
import com.mobile.view.productdetail.slider.SliderPresenter
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
        this.holder = holder

        sliderPresenter = SliderPresenter(holder.itemView.context,
                imageSliderModel.productSku,
                imageSliderModel.images)

        holder.like.isChecked = imageSliderModel.isWishList

        setupViewPager(holder)
        bindLikeButtonClickListener(holder)
        holder.share.setOnClickListener { _ -> sliderPresenter.shareProduct(imageSliderModel.shareUrl) }
    }

    private fun bindLikeButtonClickListener(holder: SliderHolder) {
        holder.like.setOnClickListener { _ ->
            holder.like.isChecked = !imageSliderModel.isWishList
            holder.like.playAnimation()

            sliderPresenter.onLikeButtonClicked(holder.like, object : Callback<ResponseWrapper<Any>> {
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

        val addToWishListEventModel =
                MainEventModel(holder!!.itemView.context.getString(TrackingPage.PRODUCT_DETAIL.getName()),
                        EventActionKeys.ADD_TO_WISHLIST,
                        imageSliderModel.productSku,
                        imageSliderModel.price.toLong(),
                        null)

        TrackerManager.trackEvent(holder!!.itemView.context,
                EventConstants.AddToWishList,
                addToWishListEventModel)
    }

    private fun itemRemovedFromWishList() {
        if (holder == null) {
            return
        }

        imageSliderModel.isWishList = false
        holder!!.like.isChecked = false

        val sem = SimpleEventModel().apply {
            category = holder!!.itemView.context.getString(TrackingPage.PRODUCT_DETAIL.getName())
            action = EventActionKeys.REMOVE_FROM_WISHLIST
            label = imageSliderModel.productSku
            value = imageSliderModel.price.toLong()
        }
        TrackerManager.trackEvent(holder!!.itemView.context, EventConstants.RemoveFromWishList, sem)
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
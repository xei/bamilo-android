package com.mobile.view.productdetail

import android.content.Context
import android.graphics.Paint
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.service.utils.TextUtils
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.viewtypes.bottomsheetvariation.PDVBottomSheetVariationItem
import java.util.*

/**
 * Created by Farshid
 * since 7/1/2018.
 * contact farshidabazari@gmail.com
 */
class ChooseVariationBottomSheetHandler(private var context: Context,
                                        private var binding: ActivityProductDetailBinding,
                                        private var pdvMainView: PDVMainView) {

    private var bottomSheetBehavior: BottomSheetBehavior<View> =
            BottomSheetBehavior
                    .from(binding.chooseVariationRelativeLayoutLayout!!
                            .chooseVariationLinearLayoutLayout)

    private lateinit var bottomSheetAdapter: GhostAdapter
    private lateinit var bottomSheetItems: ArrayList<Any>
    private var lastSlidingOffset: Float = 0F

    init {
        setupBottomSheetAdapter()
        setupBottomSheetRecycler()
        setupBottomSheetStateListener()
        setupOutSideTouchListener()
        bindAddToCartClickListener()
    }

    private fun bindAddToCartClickListener() {
        binding.chooseVariationRelativeLayoutLayout!!.pdvChooseVariationLinearLayoutAddToCart!!.setOnClickListener {
                    pdvMainView.onAddToCartClicked()
                    hideBottomSheet()
                }
    }

    private fun setupOutSideTouchListener() {
        binding.productDetailTransparentBackground.setOnClickListener {
            if (binding.productDetailTransparentBackground.visibility == View.VISIBLE) {
                hideBottomSheet()
            }
        }
    }

    private fun setupBottomSheetStateListener() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        lastSlidingOffset = 0.0F
                    }

                    BottomSheetBehavior.STATE_COLLAPSED or BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.productDetailTransparentBackground.visibility = View.GONE
                        binding.productDetailTransparentBackground.alpha = 0.0F
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.productDetailTransparentBackground.visibility = View.VISIBLE
                        binding.productDetailTransparentBackground.alpha = 1.0F
                        lastSlidingOffset = 1.0F
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > lastSlidingOffset) {
                    increaseBackGroundViewAlpha(slideOffset)
                } else if (slideOffset < lastSlidingOffset) {
                    decreaseBackGroundViewAlpha(slideOffset)
                }
                lastSlidingOffset = slideOffset
            }
        })
    }

    private fun increaseBackGroundViewAlpha(slideOffset: Float) {
        binding.productDetailTransparentBackground.visibility = View.VISIBLE
        binding.productDetailTransparentBackground.alpha = slideOffset
    }

    private fun decreaseBackGroundViewAlpha(slideOffset: Float) {
        binding.productDetailTransparentBackground.alpha = slideOffset
        if (slideOffset == 0.0F) {
            binding.productDetailTransparentBackground.visibility = View.GONE
        }
    }

    private fun setupBottomSheetAdapter() {
        bottomSheetAdapter = GhostAdapter()
        bottomSheetItems = ArrayList()
    }

    private fun setupBottomSheetRecycler() {
        binding.chooseVariationRelativeLayoutLayout!!
                .pdvChooseVariationRecyclerViewVariations.adapter = bottomSheetAdapter
        binding.chooseVariationRelativeLayoutLayout!!
                .pdvChooseVariationRecyclerViewVariations.layoutManager =
                LinearLayoutManager(context)
    }

    fun fillChooseVariationBottomSheet(product: ProductDetail) {
        bottomSheetAdapter.removeAll()
        bottomSheetItems.clear()

        product.image_list.let {
            ImageManager.getInstance().loadImage(it[0].medium, binding.chooseVariationRelativeLayoutLayout!!
                    .chooseVariationAppImageViewProductImage,
                    null,
                    R.drawable.no_image_large,
                    false)
        }

        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewTitle.text = product.title
        product.price.let {
            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewPrice.text = it.price
            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewCurrency.text = it.currency

            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.text = it.oldPrice
            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

            if (TextUtils.isEmpty(it.oldPrice) || it.oldPrice == "0") {
                binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.visibility = View.GONE
            }
        }

        bottomSheetItems.add(PDVBottomSheetVariationItem(product.variations, pdvMainView))

        bottomSheetAdapter.setItems(bottomSheetItems)
    }

    fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun isBottomSheetShown(): Boolean {
        return bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
    }
}
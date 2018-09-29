package com.bamilo.android.appmodule.bamiloapp.view.productdetail

import android.content.Context
import android.graphics.Paint
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ProductDetail
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.bottomsheetvariation.PDVBottomSheetVariationItem
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.databinding.ActivityProductDetailBinding
import com.bamilo.android.framework.components.ghostadapter.GhostAdapter
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
        bindBuyNowBtnClickListener()
    }

    private fun bindBuyNowBtnClickListener() {
        binding.chooseVariationRelativeLayoutLayout?.root?.findViewById<View>(R.id.addToCart_linearLayout_buyNow)?.setOnClickListener {
            pdvMainView.onBuyNowClicked()
        }
    }

    private fun bindAddToCartClickListener() {
        binding.chooseVariationRelativeLayoutLayout?.root?.findViewById<View>(R.id.addToCart_linearLayout_addToBasket)?.setOnClickListener {
            pdvMainView.onAddToCartClicked()
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
            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewPrice.text = it.price.persianizeDigitsInString()
            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewCurrency.text = it.currency

            binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.text = it.oldPrice?.persianizeDigitsInString()
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
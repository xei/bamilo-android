package com.mobile.view.productdetail

import android.content.Context
import android.graphics.Paint
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.utils.imageloader.ImageManager
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.PrimaryInfoModel
import com.mobile.view.productdetail.model.Variations
import com.mobile.view.productdetail.viewtypes.bottomsheetvariation.PDVBottomSheetVariationItem
import java.util.*

/**
 * Created by Farshid
 * since 7/1/2018.
 * contact farshidabazari@gmail.com
 */
class ChooseVariationBottomSheetHandler(private var context: Context, private var binding: ActivityProductDetailBinding) {
    private var bottomSheetBehavior: BottomSheetBehavior<View> =
            BottomSheetBehavior
                    .from(binding.chooseVariationRelativeLayoutLayout!!
                    .chooseVariationLinearLayoutLayout)

    private lateinit var bottomSheetAdapter: GhostAdapter
    private lateinit var bottomSheetItems: ArrayList<Any>

    init {
        setupBottomSheetAdapter()
        setupBottomSheetRecycler()
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

    fun fillChooseVariationBottomSheet(primaryInfo: PrimaryInfoModel, imageUrl: String, variations: Variations) {
        ImageManager.getInstance().loadImage(imageUrl, binding.chooseVariationRelativeLayoutLayout!!
                .chooseVariationAppImageViewProductImage,
                null,
                R.drawable.no_image_large,
                false)

        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewTitle.text = primaryInfo.title
        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewPrice.text = primaryInfo.priceModel.cost
        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewCurrency.text = primaryInfo.priceModel.currency
        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.text = primaryInfo.priceModel.discount

        binding.chooseVariationRelativeLayoutLayout!!.chooseVariationTextViewOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        bottomSheetItems.add(PDVBottomSheetVariationItem(variations))
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
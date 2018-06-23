package com.mobile.view.productdetail.viewtypes.variation

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.utils.ui.UIUtils
import com.mobile.view.R
import com.mobile.view.productdetail.OnItemClickListener
import com.mobile.view.productdetail.model.Size
import com.mobile.view.productdetail.model.Variations
import com.mobile.view.productdetail.viewtypes.variation.colors.VariationsColorItem
import com.mobile.view.productdetail.viewtypes.variation.size.VariationsSizeItem

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_variations, holder = VariationsHolder::class)
class VariationsItem(var variations: Variations?) {
    private lateinit var colorsAdapter: GhostAdapter
    private lateinit var sizesAdapter: GhostAdapter

    private lateinit var colorsItem: ArrayList<Any>
    private lateinit var sizesItem: ArrayList<Any>

    var selectedSize = Size()
    @Binder
    public fun binder(holder: VariationsHolder) {
        setupColorRecycler(holder)
        setupSizedRecycler(holder)

        addFakeData(holder)
        holder.sizeHelp.setOnClickListener { _ -> }
        holder.specifications.setOnClickListener { _ -> }
        holder.descriptions.setOnClickListener { _ -> }

        if(variations!!.otherVariations.size == 0 && variations!!.sizeVariation.size == 0){
            val layoutParams = holder.parentView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = UIUtils.dpToPx(holder.itemView.context, 8)
            holder.parentView.layoutParams = layoutParams
        }
    }

    private fun setupColorRecycler(holder: VariationsHolder) {
        setupColorsAdapter()
        holder.colorsRecyclerView.adapter = colorsAdapter
        holder.colorsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupColorsAdapter() {
        colorsAdapter = GhostAdapter()
        colorsItem = ArrayList()
    }

    private fun setupSizedRecycler(holder: VariationsHolder) {
        setupSizesAdapter()
        holder.sizesRecyclerView.adapter = sizesAdapter
        holder.sizesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupSizesAdapter() {
        sizesAdapter = GhostAdapter()
        sizesItem = ArrayList()
    }

    private fun addFakeData(holder: VariationsHolder) {
        addOtherVariations(holder)
        addSizeVariation(holder)
    }

    private fun addSizeVariation(holder: VariationsHolder) {
        if (variations != null && variations!!.sizeVariation != null) {
            for (sizeVariation in variations!!.sizeVariation!!) {
                sizesItem.add(VariationsSizeItem(sizeVariation, object : OnItemClickListener {
                    override fun onItemClicked(any: Any?) {
                        for (sizeItem in sizesItem) {
                            (sizeItem as VariationsSizeItem).disableView()
                        }
                        selectedSize = any as Size
                    }
                }))
            }
            sizesAdapter.setItems(sizesItem)
        } else {
            holder.sizeRoot.visibility = View.GONE
        }
    }

    private fun addOtherVariations(holder: VariationsHolder) {
        if (variations != null && variations!!.otherVariations != null) {
            for (otherVariation in variations!!.otherVariations!!) {
                val variationsColorItem = VariationsColorItem(otherVariation)
                colorsItem.add(variationsColorItem)
            }
            colorsAdapter.setItems(colorsItem)
        } else {
            holder.othersRoot.visibility = View.GONE
        }
    }
}
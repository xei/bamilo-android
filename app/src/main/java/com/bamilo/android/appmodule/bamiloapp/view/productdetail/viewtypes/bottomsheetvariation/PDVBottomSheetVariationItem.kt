package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.bottomsheetvariation

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.framework.components.ghostadapter.GhostAdapter
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.OnItemClickListener
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.SimpleProduct
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Variation
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.variation.colors.OtherVariationsItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.variation.size.VariationsSizeItem

/**
 * Created by Farshid
 * since 6/30/2018.
 * contact farshidabazari@gmail.com
 */

@BindItem(layout = R.layout.content_pdv_bottom_sheet_variations, holder = PDVBottomSheetVariationHolder::class)
class PDVBottomSheetVariationItem(var variations: ArrayList<Variation>?, var pdvMainView: PDVMainView) {
    private lateinit var colorsAdapter: GhostAdapter
    private lateinit var sizesAdapter: GhostAdapter

    private lateinit var colorsItem: ArrayList<Any>
    private lateinit var sizesItem: ArrayList<Any>

    @Binder
    public fun binder(holder: PDVBottomSheetVariationHolder) {
        setupColorRecycler(holder)
        setupSizedRecycler(holder)

        addOtherVariations(holder)
        addSizeVariation(holder)

        holder.sizeHelp.setOnClickListener { _ -> }
    }

    private fun setupColorRecycler(holder: PDVBottomSheetVariationHolder) {
        setupColorsAdapter()
        holder.colorsRecyclerView.adapter = colorsAdapter
        holder.colorsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupColorsAdapter() {
        colorsAdapter = GhostAdapter()
        colorsItem = ArrayList()
    }

    private fun setupSizedRecycler(holder: PDVBottomSheetVariationHolder) {
        setupSizesAdapter()
        holder.sizesRecyclerView.adapter = sizesAdapter
        holder.sizesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupSizesAdapter() {
        sizesAdapter = GhostAdapter()
        sizesItem = ArrayList()
    }

    private fun addSizeVariation(holder: PDVBottomSheetVariationHolder) {
        for (variation in variations!!) {
            if (variation.type == "size") {
                for (product in variation.products) {
                    sizesItem.add(VariationsSizeItem(product, object : OnItemClickListener {
                        override fun onItemClicked(any: Any?) {
                            for (sizeItem in sizesItem) {
                                (sizeItem as VariationsSizeItem).disableView()
                            }
                            pdvMainView.onSizeVariationClicked(any as SimpleProduct)
                        }
                    }))
                }
            }
            sizesAdapter.setItems(sizesItem)
        }

        if (sizesAdapter.itemCount == 0) {
            holder.sizeRoot.visibility = View.GONE
        } else {
            holder.sizeRoot.visibility = View.VISIBLE
        }
    }

    private fun addOtherVariations(holder: PDVBottomSheetVariationHolder) {
        for (variation in variations!!) {
            if (variation.type != "size") {
                for (product in variation.products) {
                    colorsItem.add(OtherVariationsItem(product, object : OnItemClickListener {
                        override fun onItemClicked(any: Any?) {
                            for (item in colorsItem) {
                                (item as OtherVariationsItem).deSelectProduct()
                            }
                            pdvMainView.onOtherVariationClicked(any as SimpleProduct)
                        }
                    }))
                }
            }
            colorsAdapter.setItems(colorsItem)
        }

        if (colorsAdapter.itemCount == 0) {
            holder.othersRoot.visibility = View.GONE
        } else {
            holder.othersRoot.visibility = View.VISIBLE
        }
    }
}
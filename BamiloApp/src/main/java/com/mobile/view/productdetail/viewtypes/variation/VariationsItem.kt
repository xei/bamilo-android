package com.mobile.view.productdetail.viewtypes.variation

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.utils.OnItemClickListener
import com.mobile.utils.ui.UIUtils
import com.mobile.view.R
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.mainfragment.ProductDetailMainFragment
import com.mobile.view.productdetail.model.SimpleProduct
import com.mobile.view.productdetail.model.Variation
import com.mobile.view.productdetail.viewtypes.variation.colors.OtherVariationsItem
import com.mobile.view.productdetail.viewtypes.variation.size.VariationsSizeItem

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */

@BindItem(layout = R.layout.content_pdv_variations, holder = VariationsHolder::class)
class VariationsItem(var sku: String,
                     var variations: ArrayList<Variation>,
                     private var pdvMainView: PDVMainView,
                     private var onSizeVariationClicked : ProductDetailMainFragment.OnSizeVariationClicked) {
    private lateinit var colorsAdapter: GhostAdapter
    private lateinit var sizesAdapter: GhostAdapter

    private lateinit var otherItems: ArrayList<Any>
    private lateinit var sizesItem: ArrayList<Any>

    var selectedSize = SimpleProduct()

    @Binder
    public fun binder(holder: VariationsHolder) {
        if (holder.isFilled) {
            return
        }

        setupColorRecycler(holder)
        setupSizedRecycler(holder)

        colorsAdapter.removeAll()
        sizesAdapter.removeAll()

        sizesItem.clear()
        otherItems.clear()

        if (variations.size <= 0) {
            val layoutParams = holder.parentView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = UIUtils.dpToPx(holder.itemView.context, 8f)
            holder.parentView.layoutParams = layoutParams
        } else {
            addOtherVariations(holder)
            addSizeVariation(holder)
        }

        holder.sizeHelp.setOnClickListener { _ -> }

        holder.itemView.findViewById<View>(R.id.pdvVariations_linearLayout_specification).setOnClickListener {
            gotoSpecificationPage()
        }

        holder.itemView.findViewById<View>(R.id.pdvVariations_linearLayout_descriptions).setOnClickListener {
            gotoDescriptionPage()
        }

        holder.isFilled = true
    }

    private fun gotoSpecificationPage() {
        pdvMainView.onShowSpecsAndSpecPage()
    }

    private fun gotoDescriptionPage() {
        pdvMainView.onShowDesAndSpecPage()
    }

    private fun setupColorRecycler(holder: VariationsHolder) {
        setupColorsAdapter()
        holder.colorsRecyclerView.adapter = colorsAdapter
        holder.colorsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupColorsAdapter() {
        colorsAdapter = GhostAdapter()
        otherItems = ArrayList()
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

    private fun addSizeVariation(holder: VariationsHolder) {
        for (variation in variations) {
            if (variation.type == "size") {
                for (product in variation.products) {
                    sizesItem.add(VariationsSizeItem(product, object : OnItemClickListener {
                        override fun onItemClicked(any: Any?) {
                            for (sizeItem in sizesItem) {
                                (sizeItem as VariationsSizeItem).disableView()
                            }
                            selectedSize = any as SimpleProduct
                            onSizeVariationClicked.onSizeVariationClicked(selectedSize)
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

    private fun addOtherVariations(holder: VariationsHolder) {
        for (variation in variations) {
            if (variation.type != "size") {
                if(variation.products.size == 1 && variation.products[0].sku == sku){
                    holder.othersRoot.visibility = View.GONE
                    return
                }
                holder.othersRoot.visibility = View.VISIBLE
                for (product in variation.products) {
                    otherItems.add(OtherVariationsItem(product, object : OnItemClickListener {
                        override fun onItemClicked(any: Any?) {
                            for (item in otherItems) {
                                (item as OtherVariationsItem).deSelectProduct()
                            }
                            pdvMainView.onOtherVariationClicked(any as SimpleProduct)
                        }
                    }))
                }
            }
            colorsAdapter.setItems(otherItems)
        }

        if (colorsAdapter.itemCount == 0) {
            holder.othersRoot.visibility = View.GONE
        } else {
            holder.othersRoot.visibility = View.VISIBLE
        }
    }
}
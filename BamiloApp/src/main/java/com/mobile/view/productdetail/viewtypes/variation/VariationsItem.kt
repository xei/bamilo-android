package com.mobile.view.productdetail.viewtypes.variation

import android.support.v7.widget.LinearLayoutManager
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.view.R
import com.mobile.view.productdetail.viewtypes.variation.colors.VariationsColorItem
import com.mobile.view.productdetail.viewtypes.variation.size.VariationsSizeItem

/**
 * Created by Farshid
 * since 6/17/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_variations, holder = VariationsHolder::class)
class VariationsItem {
    private lateinit var colorsAdapter: GhostAdapter
    private lateinit var sizesAdapter: GhostAdapter

    private lateinit var colorsItem: ArrayList<Any>
    private lateinit var sizesItem: ArrayList<Any>

    @Binder
    public fun binder(holder: VariationsHolder) {
        setupColorRecycler(holder)
        setupSizedRecycler(holder)

        addFakeData()
        holder.sizeHelp.setOnClickListener { _ -> }
        holder.specifications.setOnClickListener { _ -> }
        holder.descriptions.setOnClickListener { _ -> }
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

    private fun addFakeData() {
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4424-3103161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4422-4103161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4421-5103161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4498-0203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4495-1203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4492-2203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4489-3203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4492-4203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4533-8203161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-4713-3403161-1-cart.jpg"))
        colorsItem.add(VariationsColorItem("https://media.bamilo.com/p/navales-6514-4919681-1-cart.jpg"))

        sizesItem.add(VariationsSizeItem("XXS"))
        sizesItem.add(VariationsSizeItem("XS"))
        sizesItem.add(VariationsSizeItem("S"))
        sizesItem.add(VariationsSizeItem("M"))
        sizesItem.add(VariationsSizeItem("L"))
        sizesItem.add(VariationsSizeItem("XL"))
        sizesItem.add(VariationsSizeItem("XXL"))
        sizesItem.add(VariationsSizeItem("XXXL"))

        colorsAdapter.setItems(colorsItem)
        sizesAdapter.setItems(sizesItem)
    }
}
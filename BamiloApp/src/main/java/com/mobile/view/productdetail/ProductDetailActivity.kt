package com.mobile.view.productdetail

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.utils.ConfigurationWrapper
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.mobile.view.productdetail.viewtypes.recyclerheader.RecyclerHeaderItem
import com.mobile.view.productdetail.viewtypes.slider.SliderItem
import com.mobile.view.productdetail.viewtypes.variation.VariationsItem
import java.util.*

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private var productDetailViewModel: ProductDetailViewModel? = null

    private lateinit var adapter: GhostAdapter
    private lateinit var items: ArrayList<Any>

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && newBase != null) {
            super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, Locale("fa", "ir")))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        bindToolbarClickListener()

        setupViewModel()
        setupAdapter()
        setupRefreshView()
        setupDetailRecycler()

        getProductDetail("productId")

        addFakeItem()
    }

    private fun setupRefreshView() {
        binding.productDetailSwipeRefresh.setOnRefreshListener({
            Log.e(">>>>>", "on refresh")
            getProductDetail("productDetail")
        })
    }

    private fun setupDetailRecycler() {
        binding.productDetailRecyclerDetailList.adapter = adapter
        binding.productDetailRecyclerDetailList.layoutManager = LinearLayoutManager(this)
    }

    private fun addFakeItem() {
        val images = arrayListOf<String>()
        images.add("https://media.bamilo.com/p/honor-1601-6634413-1-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-2-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-2-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-3-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-4-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-5-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-6-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-7-product.jpg")
        images.add("https://media.bamilo.com/p/honor-1601-6634413-8-product.jpg")

        items.add(SliderItem(supportFragmentManager, images))
        items.add(PrimaryInfoItem())
        items.add(RecyclerHeaderItem("گزینه ها"))
        items.add(VariationsItem())

        adapter.setItems(items)
    }

    private fun getProductDetail(productId: String) {
        productDetailViewModel?.getItems(productId)
    }

    private fun setupViewModel() {
        if (productDetailViewModel != null) {
            return
        }

        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
    }

    private fun setupAdapter() {
        adapter = GhostAdapter()
        items = ArrayList()
    }

    private fun bindToolbarClickListener() {
        binding.productDetailAppImageViewBack.setOnClickListener { _ -> super.onBackPressed() }
        binding.productDetailAppImageViewCart.setOnClickListener { _ -> gotoCartView() }
    }

    private fun gotoCartView() {
    }
}

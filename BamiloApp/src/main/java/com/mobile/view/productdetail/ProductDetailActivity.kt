package com.mobile.view.productdetail

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.Toast
import com.mobile.app.BamiloApplication
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.helpers.wishlist.AddToWishListHelper
import com.mobile.helpers.wishlist.RemoveFromWishListHelper
import com.mobile.interfaces.IResponseCallback
import com.mobile.service.pojo.BaseResponse
import com.mobile.utils.ConfigurationWrapper
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.*
import com.mobile.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.mobile.view.productdetail.viewtypes.recyclerheader.RecyclerHeaderItem
import com.mobile.view.productdetail.viewtypes.slider.SliderItem
import com.mobile.view.productdetail.viewtypes.variation.VariationsItem
import java.util.*

class ProductDetailActivity : AppCompatActivity(), IResponseCallback {

    private lateinit var binding: ActivityProductDetailBinding

    private var productDetailViewModel: ProductDetailViewModel? = null

    private lateinit var adapter: GhostAdapter
    private lateinit var items: ArrayList<Any>

    var sku: String? = ""

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && newBase != null) {
            super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, Locale("fa", "ir")))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            sku = intent.getStringExtra("sku")
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        bindToolbarClickListener()

        setupViewModel()
        setupAdapter()
        setupRefreshView()
        setupDetailRecycler()

        sku = "productId"

        if (TextUtils.isEmpty(sku)) {
            showNoProductView()
            return
        }

        getProductDetail(sku!!)

        addFakeItem()
    }

    private fun showNoProductView() {
    }

    private fun setupRefreshView() {
        binding.productDetailSwipeRefresh.setOnRefreshListener({
            getProductDetail("productDetail")
        })
    }

    private fun setupDetailRecycler() {
        binding.productDetailRecyclerDetailList.adapter = adapter
        binding.productDetailRecyclerDetailList.layoutManager = LinearLayoutManager(this)
    }

    private fun addFakeItem() {
        addImagesToSlider()
        addPrimaryInfoItem()
        addVariations()

        adapter.setItems(items)
    }

    private fun addVariations() {
        val variations = Variations()
        variations.sizeVariation.add(createFakeSizeModel("XXS"))
        variations.sizeVariation.add(createFakeSizeModel("XS"))
        variations.sizeVariation.add(createFakeSizeModel("S"))
        variations.sizeVariation.add(createFakeSizeModel("L"))
        variations.sizeVariation.add(createFakeSizeModel("XL"))
        variations.sizeVariation.add(createFakeSizeModel("XXL"))

        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4661-1403161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4713-5403161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4661-8303161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4672-6303161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4661-1403161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4713-5403161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4661-8303161-1-cart.jpg"))
        variations.otherVariations.add(createFakeColorModel("https://media.bamilo.com/p/navales-4672-6303161-1-cart.jpg"))

        if (variations.sizeVariation.size > 0 || variations.otherVariations.size > 0) {
            addHeader("گزینه ها")
        }

        items.add(VariationsItem(variations))
    }

    private fun createFakeColorModel(imageUrl: String): OtherVariations {
        val otherVariations = OtherVariations()
        otherVariations.title = ""
        otherVariations.image = imageUrl
        otherVariations.sku = "9873264"
        return otherVariations
    }

    private fun createFakeSizeModel(s: String): Size {
        val size = Size()
        size.image = ""
        size.isSelected = false
        size.sku = "9876321654"
        size.title = s
        return size
    }

    private fun addHeader(headerTitle: String) {
        items.add(RecyclerHeaderItem("گزینه ها"))
    }

    private fun addPrimaryInfoItem() {
        val primaryInfoModel = PrimaryInfoModel()
        primaryInfoModel.title = "آیفون X-256GB"
        primaryInfoModel.priceModel.cost = "9,999,000"
        primaryInfoModel.priceModel.currency = "تومان"
        primaryInfoModel.priceModel.discount = "9,000,000"
        primaryInfoModel.priceModel.discountBenefit = "سود شما شما 900 هزار تومان"
        primaryInfoModel.priceModel.discountPercentage = "10%"
        primaryInfoModel.rating.average = 3.8f
        primaryInfoModel.rating.maxScore = 5
        primaryInfoModel.rating.ratingCount = 25
        items.add(PrimaryInfoItem(primaryInfoModel))
    }

    private fun addImagesToSlider() {
        val images = arrayListOf<ImageList>()
        for (i in 1 until 9) {
            val imageList = ImageList()
            imageList.medium = "https://media.bamilo.com/p/honor-1601-6634413-$i-product.jpg"
            imageList.large = "https://media.bamilo.com/p/honor-1601-6634413-$i-product.jpg"
            imageList.small = "https://media.bamilo.com/p/honor-1601-6634413-$i-product.jpg"
            images.add(imageList)
        }

        val imageSliderModel = ImageSliderModel()
        imageSliderModel.images = images
        imageSliderModel.isWishList = false
        imageSliderModel.productSku = sku!!

        items.add(SliderItem(supportFragmentManager, imageSliderModel))
    }

    private fun onLikeButtonClicked(any: Any?) {
        if (any == null) {
            loginUser()
        } else {
            if (any == true) {
                addProductToWishList()
            } else {
                removeProductToWishList()
            }
        }
    }

    private fun removeProductToWishList() {
        BamiloApplication.INSTANCE.sendRequest(AddToWishListHelper(),
                AddToWishListHelper.createBundle(sku),
                this)
    }

    private fun addProductToWishList() {
        BamiloApplication.INSTANCE.sendRequest(RemoveFromWishListHelper(),
                RemoveFromWishListHelper.createBundle(sku),
                this)
    }

    private fun loginUser() {
        Toast.makeText(this, R.string.please_login_first, Toast.LENGTH_SHORT).show()
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

    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
    }

    override fun onRequestError(baseResponse: BaseResponse<*>?) {
    }
}

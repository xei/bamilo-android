package com.mobile.view.productdetail

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.RelativeLayout
import android.widget.Toast
import com.bamilo.modernbamilo.util.dpToPx
import com.mobile.app.BamiloApplication
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.helpers.wishlist.AddToWishListHelper
import com.mobile.helpers.wishlist.RemoveFromWishListHelper
import com.mobile.interfaces.IResponseCallback
import com.mobile.service.pojo.BaseResponse
import com.mobile.utils.ConfigurationWrapper
import com.mobile.utils.ui.UIUtils
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.model.*
import com.mobile.view.productdetail.viewtypes.description.DescriptionItem
import com.mobile.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.mobile.view.productdetail.viewtypes.recyclerheader.RecyclerHeaderItem
import com.mobile.view.productdetail.viewtypes.returnpolicy.ReturnPolicyItem
import com.mobile.view.productdetail.viewtypes.seller.SellerItem
import com.mobile.view.productdetail.viewtypes.slider.SliderItem
import com.mobile.view.productdetail.viewtypes.specifications.SpecificationItem
import com.mobile.view.productdetail.viewtypes.variation.VariationsItem
import java.util.*

class ProductDetailActivity : AppCompatActivity(), IResponseCallback {

    var specificationItemPosition: Int = 0
    var descriptionItemPosition: Int = 0

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDetailPresenter: ProductDetailPresenter

    private var productDetailViewModel: ProductDetailViewModel? = null

    private lateinit var adapter: GhostAdapter
    private lateinit var items: ArrayList<Any>

    private lateinit var variationsItem: VariationsItem

    var sku: String? = ""

    private var toolbarFadingOffset: Float = 0.0f
    private var toolbarTitleSlidingOffset = 0
    private var recyclerViewOverallScroll: Int = 0
    private var toolbarTitleCenterPositionMargin: Int = 0

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
        productDetailPresenter = ProductDetailPresenter(this, binding)

        fetchExtraIntentData()

        bindToolbarClickListener()
        setupToolbarOnScrollBehavior()

        setupViewModel()
        setUpMainViewAdapter()
        setupRefreshView()
        setupDetailRecycler()

        loadProductDetail()
        bindAddToCartClickListener()

        addFakeItem()
    }

    private fun fetchExtraIntentData() {
        if (intent != null) {
            sku = intent.getStringExtra("sku")
        }

        sku = "productId"

        if (TextUtils.isEmpty(sku)) {
            showNoProductView()
            return
        }
    }

    private fun bindAddToCartClickListener() {
        binding.productDetailLinearLayoutAddToCart!!.setOnClickListener({
            if (variationsItem.selectedSize.sku.isEmpty()) {
                productDetailPresenter.showBottomSheet()
            } else {
                //TODO add production to card
            }
        })
    }

    private fun setupToolbarOnScrollBehavior() {
        toolbarFadingOffset = UIUtils.dpToPx(this, 300f).toFloat()
        toolbarTitleCenterPositionMargin = UIUtils.dpToPx(this, 16f)
        toolbarTitleSlidingOffset = UIUtils.dpToPx(this, 348f)
    }

    private fun showNoProductView() {
    }

    private fun setupRefreshView() {
        binding.productDetailSwipeRefresh.setProgressViewOffset(false,
                0,
                dpToPx(this, 64f))

        binding.productDetailSwipeRefresh.setOnRefreshListener({ loadProductDetail() })
    }

    private fun setupDetailRecycler() {
        binding.productDetailRecyclerDetailList.adapter = adapter
        binding.productDetailRecyclerDetailList.layoutManager = LinearLayoutManager(this)
        binding.productDetailRecyclerDetailList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerViewOverallScroll += dy
                setToolbarAlpha()
                setupToolbarTitle()

                val view = binding.productDetailRecyclerDetailList.getChildAt(0)
                if (view != null &&
                        binding.productDetailRecyclerDetailList.getChildAdapterPosition(view) == 0) {
                    view.translationY = (-view.top / 3).toFloat()
                }
            }
        })
    }

    private fun setupToolbarTitle() {
        val layoutParams: RelativeLayout.LayoutParams =
                binding.productDetailTextViewTitle.layoutParams as RelativeLayout.LayoutParams

        layoutParams.topMargin = toolbarTitleSlidingOffset - recyclerViewOverallScroll
        binding.productDetailTextViewTitle.layoutParams = layoutParams

        if (toolbarTitleSlidingOffset - recyclerViewOverallScroll < toolbarTitleCenterPositionMargin) {
            layoutParams.topMargin = toolbarTitleCenterPositionMargin
            binding.productDetailTextViewTitle.layoutParams = layoutParams

        }
    }

    private fun setToolbarAlpha() {
        var alpha = (recyclerViewOverallScroll / toolbarFadingOffset)
        if (alpha > 1) {
            alpha = 1f
        }
        binding.productDetailRelativeLayoutPrimaryToolbar.alpha = alpha
    }

    private fun addFakeItem() {
        addImagesToSlider()
        addPrimaryInfoItem()
        addVariations()
        addReturnPolicy()
        addHeader("تامین کننده")
        addSellerInfo()
        addHeader("توضیحات")
        addDescription()
        addHeader("مشخصات")
        addSpecification()

        adapter.setItems(items)

        productDetailPresenter.fillChooseVariationBottomSheet(createFakePrimaryInfo(),
                "https://media.bamilo.com/p/honor-1601-6634413-1-product.jpg",
                createFakeVariation())
    }

    private fun addSpecification() {
        items.add(SpecificationItem("• \tدو سیم کارته\n" +
                "• \tپردازنده هشت هسته ای\n" +
                "• \tصفحه نمایش ۵/۴ اینچی IPS\t",
                sku!!))

        specificationItemPosition = items.size - 1

    }

    private fun addDescription() {
        items.add(DescriptionItem("سمنتبسکمینتب سمنیتب سکمینتب کسمنیتب کسمنتیب " +
                "سیمنبت سکیبنت سکمینتب " +
                "سمینتب کسینتب کسمینتب کسمینت ب" +
                "مسنیتب سکمنتیب کسیتنب کسیمتب " +
                "سمینبت سکمنیبت سکمینتب کسمینت ب" +
                "منستیب مسنتیب مسنیتبسگیسکمینبسکیمنب ستنیا بسنمتیبا سمینت" +
                "منستی بکسمنیتب کسمینت بکسیتب " +
                "مسنیتب سمنیتب سمینتب سمیتنب ", sku!!))

        descriptionItemPosition = items.size - 1

    }

    private fun addSellerInfo() {
        val seller = Seller()
        val sellerScore = getScore()

        val presenceDuration = PresenceDuration()
        presenceDuration.label = "مدت همکاری"
        presenceDuration.value = "۲ سال و ۴ ماه"

        seller.name = "سامان گستر"
        seller.isNew = false
        seller.score = sellerScore
        seller.presenceDuration = presenceDuration

        items.add(SellerItem(seller, 5, sku!!))
    }

    private fun getScore(): Score {
        val sellerScore = Score()
        val fullFillment = FullFillment()
        val notReturned = NotReturned()
        val sLAReached = SLAReached()
        val overall = Overall()

        fullFillment.label = "تامین به موقع"
        fullFillment.value = 4.2

        notReturned.label = "بدون بازگشت"
        notReturned.value = 3.5

        sLAReached.label = "ارسال به موقع"
        sLAReached.value = 3.9

        overall.color = "#47b638"
        overall.label = "امتیاز"
        overall.value = 4.0

        sellerScore.maxValue = 5
        sellerScore.isEnabled = true

        sellerScore.fullFillment = fullFillment
        sellerScore.notReturned = notReturned
        sellerScore.overall = overall
        sellerScore.sLAReached = sLAReached

        return sellerScore
    }

    private fun addReturnPolicy() {
        val warranty = Warranty()
        warranty.title = "۱۰۰ روز بازگشت کالا"
        warranty.icon = "https://image.flaticon.com/icons/png/512/248/248968.png"
        warranty.url = "https://www.bamilo.com/help/?source=fwb"

        items.add(ReturnPolicyItem(warranty))
    }

    private fun addVariations() {
        variationsItem = VariationsItem(createFakeVariation())
        variationsItem.onItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(any: Any?) {
                if (any == VariationsItem.description) {
                    binding.productDetailRecyclerDetailList.smoothScrollToPosition(descriptionItemPosition)
                } else {
                    binding.productDetailRecyclerDetailList.smoothScrollToPosition(specificationItemPosition)
                }
            }
        })

        items.add(variationsItem)
    }

    private fun createFakeVariation(): Variations {
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

        return variations
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
        items.add(RecyclerHeaderItem(headerTitle))
    }

    private fun addPrimaryInfoItem() {
        items.add(PrimaryInfoItem(createFakePrimaryInfo()))
    }

    private fun createFakePrimaryInfo(): PrimaryInfoModel {
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
        primaryInfoModel.isExist = false

        return primaryInfoModel
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

    private fun loadProductDetail() {
        productDetailViewModel?.loadData(sku!!)
    }

    private fun setupViewModel() {
        if (productDetailViewModel != null) {
            return
        }

        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
    }

    private fun setUpMainViewAdapter() {
        adapter = GhostAdapter()
        items = ArrayList()
    }

    private fun bindToolbarClickListener() {
        binding.productDetailAppImageViewBack.setOnClickListener { _ -> super.onBackPressed() }
        binding.productDetailAppImageViewCart.setOnClickListener { _ -> gotoCartView() }
    }

    override fun onBackPressed() {
        if (productDetailPresenter.isBottomSheetShown()) {
            productDetailPresenter.hideBottomSheet()
            return
        }

        super.onBackPressed()
    }

    private fun gotoCartView() {
    }

    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
    }

    override fun onRequestError(baseResponse: BaseResponse<*>?) {
    }
}

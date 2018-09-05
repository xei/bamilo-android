package com.bamilo.android.appmodule.bamiloapp.view.productdetail.mainfragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.Item
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendManager
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel
import com.bamilo.android.appmodule.bamiloapp.utils.OnItemClickListener
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator
import com.bamilo.android.appmodule.bamiloapp.utils.headerandmorebutton.morebutton.SeeMoreButtonItem
import com.bamilo.android.appmodule.bamiloapp.utils.headerandmorebutton.recyclerheader.RecyclerHeaderItem
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.ProductDetailActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ImageSliderModel
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.PrimaryInfoModel
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ProductDetail
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.SimpleProduct
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.breadcrumbs.BreadcrumbListItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.recommendation.RecommendationItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.returnpolicy.ReturnPolicyItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.review.ReviewsItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.seller.SellerItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.slider.SliderItem
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.variation.VariationsItem
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.databinding.FragmentPdvMainViewBinding
import com.bamilo.android.framework.components.ghostadapter.GhostAdapter
import com.bamilo.android.framework.service.database.BrandsTableHelper
import com.bamilo.android.framework.service.database.LastViewedTableHelper
import com.bamilo.android.framework.service.tracking.AdjustTracker
import com.bamilo.android.framework.service.tracking.TrackingPage
import com.emarsys.predict.RecommendedItem


/**
 * Created by Farshid
 * since 7/4/2018.
 * contact farshidabazari@gmail.com
 */

class ProductDetailMainFragment : Fragment() {
    private var sku: String? = ""
    private lateinit var binding: FragmentPdvMainViewBinding

    private var toolbarFadingOffset: Float = 0.0f
    private var toolbarTitleSlidingOffset = 0
    private var recyclerViewOverallScroll: Int = 0
    private var toolbarTitleCenterPositionMargin: Int = 0

    private var itemPositionToChangeGridSpanCount = 0
    private var itemPositionToChangeGridSpanCountToDefault = -1

    private var product = ProductDetail()

    private lateinit var adapter: GhostAdapter
    private lateinit var items: ArrayList<Any>
    private lateinit var recommendationAdapterItems: ArrayList<Any>

    private var pdvMainFragmentViewModel: ProductDetailMainFragmentViewModel? = null

    private lateinit var pdvMainView: PDVMainView
    private var recommendedItems: Any? = null

    private var sizeVariation = SimpleProduct()

    companion object {
        fun newInstance(sku: String?): ProductDetailMainFragment {
            val args = Bundle()
            args.putString("sku", sku)
            val fragment = ProductDetailMainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pdvMainView = (context as ProductDetailActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (::binding.isInitialized) {
            return binding.root
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdv_main_view, container, false)

        fetchExtraIntentData()

        bindToolbarClickListener()
        setupToolbarOnScrollBehavior()

        setupViewModel()
        setUpMainViewAdapter()
        setupDetailRecycler()

        setupCartItemClickListener()

        loadProductDetail(sku!!)

        return binding.root
    }

    private fun setupCartItemClickListener() {
        binding.pdvAppImageViewWhiteCart.setOnClickListener { onCartClicked() }
        binding.pdvAppImageViewCart.setOnClickListener { onCartClicked() }
    }

    private fun onCartClicked() {
        val intent = Intent(context, MainFragmentActivity::class.java)
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.SHOPPING_CART)
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false)

        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        pdvMainView.onProductReceived(product)
        pdvMainView.onSizeVariationClicked(sizeVariation)
    }

    private fun fetchExtraIntentData() {
        if (arguments == null) {
            sku = ""
            return
        }

        sku = arguments!!.getString("sku")
    }

    private fun setupViewModel() {
        if (pdvMainFragmentViewModel != null) {
            return
        }

        pdvMainFragmentViewModel = ViewModelProviders.of(this).get(ProductDetailMainFragmentViewModel::class.java)
    }

    private fun setUpMainViewAdapter() {
        adapter = GhostAdapter()
        items = ArrayList()
        recommendationAdapterItems = ArrayList()
    }

    private fun setupToolbarOnScrollBehavior() {
        toolbarFadingOffset = UIUtils.dpToPx(context, 300f).toFloat()
        toolbarTitleCenterPositionMargin = UIUtils.dpToPx(context, 16f)
        toolbarTitleSlidingOffset = UIUtils.dpToPx(context, 348f)
    }

    private fun setupDetailRecycler() {
        binding.pdvRecyclerDetailList.adapter = adapter
        setupRecyclerViewLayoutManager()
        setupRecyclerViewDecoration()
        setupRecyclerViewScrollListener()
    }

    private fun setupRecyclerViewLayoutManager() {
        val layoutManager =
                GridLayoutManager(context,
                        2,
                        GridLayoutManager.VERTICAL,
                        false)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (recommendedItems == null) {
                    return 2
                }

                if (position in itemPositionToChangeGridSpanCount..(itemPositionToChangeGridSpanCountToDefault - 1)) {
                    return 1
                }

                return 2
            }
        }

        binding.pdvRecyclerDetailList.layoutManager = layoutManager
    }

    private fun setupRecyclerViewDecoration() {
        binding.pdvRecyclerDetailList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                if (recommendedItems != null) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position in
                            itemPositionToChangeGridSpanCount..(itemPositionToChangeGridSpanCountToDefault - 1)) {
                        if (position % 2 == 0) {
                            outRect.right = UIUtils.dpToPx(context, 4f)
                            outRect.left = UIUtils.dpToPx(context, 6f)
                        } else {
                            outRect.right = UIUtils.dpToPx(context, 6f)
                            outRect.left = UIUtils.dpToPx(context, 4f)
                        }
                    }
                }
            }
        })
    }

    private fun setupRecyclerViewScrollListener() {
        binding.pdvRecyclerDetailList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerViewOverallScroll += dy
                setToolbarAlpha()
                setupToolbarTitle()

                val view = binding.pdvRecyclerDetailList.getChildAt(0)
                if (view != null &&
                        binding.pdvRecyclerDetailList.getChildAdapterPosition(view) == 0) {
                    view.translationY = (-view.top / 3).toFloat()
                }
            }
        })
    }

    private fun setupToolbarTitle() {
        val layoutParams: RelativeLayout.LayoutParams =
                binding.pdvTextViewTitle.layoutParams as RelativeLayout.LayoutParams

        layoutParams.topMargin = toolbarTitleSlidingOffset - recyclerViewOverallScroll
        binding.pdvTextViewTitle.layoutParams = layoutParams

        if (toolbarTitleSlidingOffset - recyclerViewOverallScroll < toolbarTitleCenterPositionMargin) {
            layoutParams.topMargin = toolbarTitleCenterPositionMargin
            binding.pdvTextViewTitle.layoutParams = layoutParams

        }
    }

    private fun setToolbarAlpha() {
        var alpha = (recyclerViewOverallScroll / toolbarFadingOffset)
        if (alpha > 1) {
            alpha = 1f
        }
        binding.pdvRelativeLayoutPrimaryToolbar.alpha = alpha
    }

    private fun addItemsToRecyclerInputList() {
        if (!isAdded || context == null) {
            return
        }
        items.clear()
        adapter.removeAll()

        binding.pdvTextViewTitle.text = product.title
        addImagesToSlider()
        addPrimaryInfoItem()

        if (!product.has_stock) {
            showOutOfStock()
            return
        }

        addVariations()
        addReturnPolicy()
        addSellerInfo()
        addReviews()
    }

    private fun showOutOfStock() {
        pdvMainView.showOutOfStock()
    }

    private fun addItemsToAdapter() {
        adapter.setItems(items)

        val viewProductEventModel = MainEventModel(getString(TrackingPage.PDV.getName()), EventActionKeys.VIEW_PRODUCT, product.sku,
                product.price.price.toLong(),
                MainEventModel.createViewProductEventModelAttributes("categoryUrlKey",
                        product.price.price.toLong()))
        TrackerManager.trackEvent(context, EventConstants.ViewProduct, viewProductEventModel)

        val params = Bundle()
        params.putSerializable(AdjustTracker.PRODUCT, product)
        params.putString(AdjustTracker.TREE, product.breadcrumbs[0].target?.let { it.split("::")[1] })
        TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_DETAIL_LOADED, params)
    }

    private fun addImagesToSlider() {
        val imageSliderModel = ImageSliderModel()
        imageSliderModel.productSku = sku!!
        imageSliderModel.shareUrl = product.share_url
        imageSliderModel.isWishList = product.isWishList
        imageSliderModel.images = product.image_list
        imageSliderModel.price = product.price.price
        imageSliderModel.category = product.breadcrumbs[0].target!!.split("::")[1]

        items.add(SliderItem(fragmentManager!!, imageSliderModel, pdvMainView))
    }

    private fun addPrimaryInfoItem() {
        val primaryInfoModel = PrimaryInfoModel()
        primaryInfoModel.priceModel = product.price
        primaryInfoModel.hasStock = product.has_stock
        primaryInfoModel.rating = product.rating
        primaryInfoModel.title = product.title
        primaryInfoModel.brand = product.brand

        items.add(PrimaryInfoItem(sku!!, primaryInfoModel, pdvMainView))
    }

    private fun addVariations() {

        items.add(VariationsItem(sku!!, product.variations, pdvMainView, object : OnSizeVariationClicked {
            override fun onSizeVariationClicked(selectedSize: SimpleProduct) {
                sizeVariation = selectedSize.copy()
                pdvMainView.onSizeVariationClicked(sizeVariation)
            }
        }))
    }

    private fun addReturnPolicy() {
        items.add(ReturnPolicyItem(product.return_policy))
    }

    private fun addHeader(header: String) {
        items.add(RecyclerHeaderItem(header))
    }

    private fun addSellerInfo() {
        addHeader(context!!.getString(R.string.fulfilled))
        items.add(SellerItem(product.seller, product.other_seller_count, product.simple_sku!!, pdvMainView))
    }

    private fun addReviews() {
        addHeader(context!!.getString(R.string.customers_review))
        product.reviews.average = product.rating.average
        items.add(ReviewsItem(product.reviews, sku!!, pdvMainView))
    }

    private fun addBreadCrumbs() {
        if (product.breadcrumbs.size <= 0) {
            return
        }
        items.add(BreadcrumbListItem(product.breadcrumbs))
    }

    private fun loadProductDetail(sku: String) {
        pdvMainView.showProgressView()
        pdvMainFragmentViewModel!!.loadData(sku).observe(this, Observer {
            if (it != null) {
                this.product = it

                addItemsToRecyclerInputList()
                addBreadCrumbs()
                addItemsToAdapter()

                addProductToLastViewDatabase()
                pdvMainView.onProductReceived(it)

                getRecommendedProducts()

            } else if (context != null) {
                pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                        context!!.getString(R.string.error_occured))
            }

            pdvMainView.dismissProgressView()
        })
    }

    private fun addProductToLastViewDatabase() {
        LastViewedTableHelper.insertLastViewedProduct(product)
        BrandsTableHelper.updateBrandCounter(product.brand)
    }

    private fun getRecommendedProducts() {
        val recommendManager = RecommendManager()
        recommendManager.sendRelatedRecommend(getRecommendationItem(),
                null,
                product.sku,
                null,
                6)
        { _, data ->
            if (isAdded && context != null) {
                recommendedItems = data
                if (data != null) {
                    addHeader(getString(R.string.related_products))

                    itemPositionToChangeGridSpanCount = items.size - 2
                    itemPositionToChangeGridSpanCountToDefault = itemPositionToChangeGridSpanCount

                    for (i in 0 until data.size) {
                        if (i > 5) {
                            break
                        }
                        itemPositionToChangeGridSpanCountToDefault++
                        recommendationAdapterItems.add(RecommendationItem(data[i], product.price.currency, pdvMainView))
                    }

                    addSeeMoreRecommendItem()
                    addRecommendItemsToAdapter()
                }
            }
        }
    }

    private fun addRecommendItemsToAdapter() {
        if (adapter.itemCount > 0) {
            adapter.addItems(adapter.itemCount - 1, recommendationAdapterItems)
        } else {
            adapter.addItems(recommendationAdapterItems)
        }
    }

    private fun addSeeMoreRecommendItem() {
        recommendationAdapterItems.add(SeeMoreButtonItem(getString(R.string.see_all_related_products), object : OnItemClickListener {
            override fun onItemClicked(any: Any?) {
                pdvMainView.onShowMoreRelatedProducts()
            }
        }))
    }

    private fun getRecommendationItem(): RecommendedItem? {
        val item = Item().apply {
            brand = product.brand

            if (product.breadcrumbs.size > 0) {
                product.breadcrumbs[0].target?.let {
                    category = it.split("::")[1]
                }
            }

            image = product.image
            isAvailable = product.has_stock
            itemID = product.sku
            price = product.price.price.toDouble()
            title = product.title
        }

        return item.recommendedItem
    }

    private fun bindToolbarClickListener() {
        binding.pdvAppImageViewBack.setOnClickListener { _ -> pdvMainView.onBackButtonClicked() }
        binding.pdvAppImageViewWhiteBack.setOnClickListener { _ -> pdvMainView.onBackButtonClicked() }
    }

    fun updateCartBadge() {
        val cart = BamiloApplication.INSTANCE.cart
        when {
            cart == null -> binding.pdvAppImageViewCartBadge.visibility = View.GONE
            cart.cartCount == 0 -> binding.pdvAppImageViewCartBadge.visibility = View.GONE
            else -> {
                binding.pdvAppImageViewCartBadge.visibility = View.VISIBLE
                binding.pdvAppImageViewCartBadge.visibility = View.VISIBLE
                binding.pdvAppImageViewCartBadge.text = cart.cartCount.toString().persianizeDigitsInString()
            }
        }
    }

    fun updateSizeVariation(sizeVariation: SimpleProduct) {
        this.sizeVariation = sizeVariation.copy()
    }

    public interface OnSizeVariationClicked {
        fun onSizeVariationClicked(selectedSize: SimpleProduct)
    }
}
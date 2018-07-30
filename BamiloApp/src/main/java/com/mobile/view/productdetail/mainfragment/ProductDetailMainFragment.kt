package com.mobile.view.productdetail.mainfragment

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.emarsys.predict.RecommendedItem
import com.mobile.app.BamiloApplication
import com.mobile.classes.models.MainEventModel
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.constants.ConstantsIntentExtra
import com.mobile.constants.tracking.EventActionKeys
import com.mobile.constants.tracking.EventConstants
import com.mobile.controllers.fragments.FragmentType
import com.mobile.extlibraries.emarsys.predict.recommended.Item
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager
import com.mobile.managers.TrackerManager
import com.mobile.utils.OnItemClickListener
import com.mobile.utils.headerandmorebutton.morebutton.SeeMoreButtonItem
import com.mobile.utils.headerandmorebutton.recyclerheader.RecyclerHeaderItem
import com.mobile.utils.ui.UIUtils
import com.mobile.utils.ui.WarningFactory
import com.mobile.view.MainFragmentActivity
import com.mobile.view.R
import com.mobile.view.databinding.FragmentPdvMainViewBinding
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.ProductDetailActivity
import com.mobile.view.productdetail.model.ImageSliderModel
import com.mobile.view.productdetail.model.PrimaryInfoModel
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.viewtypes.breadcrumbs.BreadcrumbListItem
import com.mobile.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.mobile.view.productdetail.viewtypes.recommendation.RecommendationItem
import com.mobile.view.productdetail.viewtypes.returnpolicy.ReturnPolicyItem
import com.mobile.view.productdetail.viewtypes.review.ReviewsItem
import com.mobile.view.productdetail.viewtypes.seller.SellerItem
import com.mobile.view.productdetail.viewtypes.slider.SliderItem
import com.mobile.view.productdetail.viewtypes.variation.VariationsItem


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

    private var pdvMainFragmentViewModel: ProductDetailMainFragmentViewModel? = null

    private lateinit var pdvMainView: PDVMainView
    private var recommendedItems: Any? = null

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

        val viewProductEventModel = MainEventModel("pdv", EventActionKeys.VIEW_PRODUCT, product.sku,
                product.price.price.toLong(),
                MainEventModel.createViewProductEventModelAttributes("categoryUrlKey",
                        product.price.price.toLong()))
        TrackerManager.trackEvent(context, EventConstants.ViewProduct, viewProductEventModel)
    }

    private fun addImagesToSlider() {
        val imageSliderModel = ImageSliderModel()
        imageSliderModel.productSku = sku!!
        imageSliderModel.shareUrl = product.share_url
        imageSliderModel.isWishList = product.isWishList
        imageSliderModel.images = product.image_list
        imageSliderModel.price = product.price.price

        items.add(SliderItem(fragmentManager!!, imageSliderModel, pdvMainView))
    }

    private fun addPrimaryInfoItem() {
        val primaryInfoModel = PrimaryInfoModel()
        primaryInfoModel.priceModel = product.price
        primaryInfoModel.hasStock = product.has_stock
        primaryInfoModel.rating = product.rating
        primaryInfoModel.title = product.title
        primaryInfoModel.brand = product.brand

        items.add(PrimaryInfoItem(sku!!, primaryInfoModel))
    }

    private fun addVariations() {
        items.add(VariationsItem(sku!!, product.variations, pdvMainView))
    }

    private fun addReturnPolicy() {
        items.add(ReturnPolicyItem(product.return_policy))
    }

    private fun addHeader(header: String) {
        items.add(RecyclerHeaderItem(header))
    }

    private fun addSellerInfo() {
//        addHeader(context!!.getString(R.string.fulfilled))
        items.add(SellerItem(product.seller, product.other_seller_count, product.simple_sku!!, pdvMainView))
    }

    private fun addReviews() {
        addHeader(context!!.getString(R.string.customers_review))
        product.reviews.average = product.rating.average
        items.add(ReviewsItem(product.reviews, sku!!, pdvMainView))
    }

    private fun addSeeMoreRecommendItem() {
        items.add(SeeMoreButtonItem(getString(R.string.see_all_related_products), object : OnItemClickListener {
            override fun onItemClicked(any: Any?) {
                pdvMainView.onShowMoreRelatedProducts()
            }
        }))
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
                pdvMainView.onProductReceived(it)
                getRecommendedProducts()
            } else if (context != null) {
                pdvMainView.dismissProgressView()
                pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                        context!!.getString(R.string.error_occured))
            }
        })
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
                addItemsToRecyclerInputList()
                recommendedItems = data
                if (data != null) {
                    addHeader(getString(R.string.related_products))

                    itemPositionToChangeGridSpanCount = items.size
                    itemPositionToChangeGridSpanCountToDefault = itemPositionToChangeGridSpanCount

                    for (i in 0 until data.size) {
                        if (i > 5) {
                            break
                        }
                        itemPositionToChangeGridSpanCountToDefault++
                        items.add(RecommendationItem(data[i], product.price.currency, pdvMainView))
                    }

                    addSeeMoreRecommendItem()
                }
                addBreadCrumbs()
                addItemsToAdapter()
                pdvMainView.dismissProgressView()
            }
        }
    }

    private fun getRecommendationItem(): RecommendedItem? {
        val item = Item().apply {
            brand = product.brand

            product.breadcrumbs[0].target?.let {
                category = it.split("::")[1]
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
                binding.pdvAppImageViewCartBadge.text = cart.cartCount.toString()
            }
        }
    }
}
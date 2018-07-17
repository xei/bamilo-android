package com.mobile.view.productdetail.mainfragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.mobile.app.BamiloApplication
import com.mobile.classes.models.MainEventModel
import com.mobile.classes.models.SimpleEventModel
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.constants.tracking.EventActionKeys
import com.mobile.constants.tracking.EventConstants
import com.mobile.helpers.wishlist.AddToWishListHelper
import com.mobile.interfaces.IResponseCallback
import com.mobile.managers.TrackerManager
import com.mobile.service.pojo.BaseResponse
import com.mobile.service.tracking.TrackingPage
import com.mobile.utils.ui.UIUtils
import com.mobile.view.R
import com.mobile.view.databinding.FragmentPdvMainViewBinding
import com.mobile.view.productdetail.OnItemClickListener
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.ProductDetailActivity
import com.mobile.view.productdetail.model.ImageSliderModel
import com.mobile.view.productdetail.model.PrimaryInfoModel
import com.mobile.view.productdetail.model.Product
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.viewtypes.primaryinfo.PrimaryInfoItem
import com.mobile.view.productdetail.viewtypes.recyclerheader.RecyclerHeaderItem
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
class ProductDetailMainFragment : Fragment(), IResponseCallback {
    private var sku: String? = ""
    private lateinit var binding: FragmentPdvMainViewBinding

    private var toolbarFadingOffset: Float = 0.0f
    private var toolbarTitleSlidingOffset = 0
    private var recyclerViewOverallScroll: Int = 0
    private var toolbarTitleCenterPositionMargin: Int = 0

    private var product = ProductDetail()

    private lateinit var adapter: GhostAdapter
    private lateinit var items: ArrayList<Any>

    private var pdvMainFragmentViewModel: ProductDetailMainFragmentViewModel? = null

    private lateinit var pdvMainView: PDVMainView

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
        setupRefreshView()
        setupDetailRecycler()

        loadProductDetail(sku!!)
        return binding.root
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

    private fun setupRefreshView() {
        binding.pdvSwipeRefresh.setProgressViewOffset(false,
                0,
                UIUtils.dpToPx(context, 64f))

        binding.pdvSwipeRefresh.setOnRefreshListener({ loadProductDetail(sku!!) })
    }

    private fun setupDetailRecycler() {
        binding.pdvRecyclerDetailList.adapter = adapter
        binding.pdvRecyclerDetailList.layoutManager = LinearLayoutManager(context)
        binding.pdvRecyclerDetailList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
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

    private fun updateUi() {
        items.clear()
        adapter.removeAll()

        binding.pdvTextViewTitle.text = product.title
        addImagesToSlider()
        addPrimaryInfoItem()
        addVariations()
        addReturnPolicy()
        addSellerInfo()
        addReviews()

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
        imageSliderModel.isWishList = product.isWishList
        imageSliderModel.images = product.image_list

        items.add(SliderItem(fragmentManager!!, imageSliderModel))
    }

    private fun addPrimaryInfoItem() {
        val primaryInfoModel = PrimaryInfoModel()
        primaryInfoModel.priceModel = product.price
        primaryInfoModel.hasStock = product.has_stock
        primaryInfoModel.rating = product.rating
        primaryInfoModel.title = product.title

        items.add(PrimaryInfoItem(primaryInfoModel))
    }

    private fun addVariations() {
        if (product.variations.size < 0) {
            return
        }

        items.add(VariationsItem(product.variations, pdvMainView))
    }


    private fun addReturnPolicy() {
        items.add(ReturnPolicyItem(product.return_policy))
    }

    private fun addHeader(header: String) {
        items.add(RecyclerHeaderItem(header))
    }

    private fun addSellerInfo() {
        addHeader(context!!.getString(R.string.fulfilled))
        items.add(SellerItem(product.seller, product.other_seller_count, product.simple_sku))
    }

    private fun addReviews() {
        if (product.reviews.items.size > 0) {
            addHeader(context!!.getString(R.string.customers_review))
            items.add(ReviewsItem(product.reviews))
        }
    }

    public fun reloadData(sku: String) {
        loadProductDetail(sku)
    }

    private fun loadProductDetail(sku: String) {
        binding.pdvSwipeRefresh.isRefreshing = true
        pdvMainFragmentViewModel!!.loadData(sku).observe(this, Observer { product ->
            binding.pdvSwipeRefresh.isRefreshing = false
            if (product != null) {
                this.product = product
                updateUi()
                pdvMainView.onProductReceived(product)
            }
        })
    }

    private fun bindToolbarClickListener() {
        binding.pdvAppImageViewBack.setOnClickListener { _ -> pdvMainView.onBackButtonClicked() }
        binding.pdvAppImageViewWhiteBack.setOnClickListener { _ -> pdvMainView.onBackButtonClicked() }

        binding.pdvAppImageViewCart.setOnClickListener { _ -> gotoCartView() }
    }

    private fun gotoCartView() {
    }

    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
    }

    override fun onRequestError(baseResponse: BaseResponse<*>?) {
    }
}
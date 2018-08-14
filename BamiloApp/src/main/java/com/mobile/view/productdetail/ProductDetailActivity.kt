package com.mobile.view.productdetail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.product.comment.CommentViewModel
import com.bamilo.modernbamilo.product.comment.CommentsFragment
import com.bamilo.modernbamilo.product.comment.submit.startSubmitRateActivity
import com.bamilo.modernbamilo.product.descspec.spec.SpecificationFragment
import com.bamilo.modernbamilo.product.descspec.tempdesc.TemporaryDescriptionFragment
import com.bamilo.modernbamilo.product.sellerslist.view.SellersListAdapter
import com.google.gson.Gson
import com.mobile.app.BamiloApplication
import com.mobile.classes.models.BaseScreenModel
import com.mobile.classes.models.MainEventModel
import com.mobile.constants.ConstantsIntentExtra
import com.mobile.constants.tracking.EventActionKeys
import com.mobile.constants.tracking.EventConstants
import com.mobile.controllers.fragments.FragmentController
import com.mobile.controllers.fragments.FragmentType
import com.mobile.interfaces.IResponseCallback
import com.mobile.managers.TrackerManager
import com.mobile.service.pojo.BaseResponse
import com.mobile.service.tracking.TrackingPage
import com.mobile.service.utils.TextUtils
import com.mobile.utils.TrackerDelegator
import com.mobile.utils.dialogfragments.DialogProgressFragment
import com.mobile.utils.ui.WarningFactory
import com.mobile.view.MainFragmentActivity
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.mainfragment.ProductDetailMainFragment
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.model.Review
import com.mobile.view.productdetail.model.SimpleProduct
import com.mobile.view.productdetail.seller.SellersListFragment

class ProductDetailActivity : BaseActivity(),
        PDVMainView,
        SellersListAdapter.OnAddToCartButtonClickListener,
        CommentsFragment.OnSubmitCommentButtonClickListener {
    private lateinit var productDetail: ProductDetail
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDetailPresenter: ProductDetailPresenter

    private var sizeVariation = SimpleProduct()

    private var sku: String? = ""

    private var progressDialog: DialogProgressFragment? = null

    private lateinit var warningFactory: WarningFactory

    /**
     * create a bundle of requirements
     **/
    companion object {
        @JvmStatic
        fun start(invokerContext: Context, sku: String) {
            val intent = Intent(invokerContext, ProductDetailActivity::class.java)
            intent.putExtra("sku", sku)
            invokerContext.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        productDetailPresenter = ProductDetailPresenter(this, binding, this)

        fetchExtraIntentData()
        setupAddToCard()
        setupWarningMessage()

        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)

        FragmentController.getInstance().addToBackStack("pdv")
        val screenModel = BaseScreenModel(
                getString(TrackingPage.PRODUCT_DETAIL.getName()),
                getString(R.string.gaScreen), "", System.currentTimeMillis())
        TrackerManager.trackScreen(this, screenModel, false)
    }

    override fun onPause() {
        super.onPause()
        progressDialog?.dismiss()
    }

    private fun setupAddToCard() {
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.GONE
        bindAddToCartClickListener()
    }

    private fun fetchExtraIntentData() {
        if (intent != null) {
            sku = intent.getStringExtra("sku")
        }
    }

    private fun setupWarningMessage() {
        warningFactory = WarningFactory(binding.productDetailRelativeLayoutWarningLayout)
    }

    private fun bindAddToCartClickListener() {
        binding.productDetailLinearLayoutAddToCart!!.setOnClickListener {
            addProductToCart(productDetail.simple_sku!!)
        }
    }

    private fun addProductToCart(sku: String) {
        if (productHasSizeVariation() && TextUtils.isEmpty(sizeVariation.sku)) {
            productDetailPresenter.showBottomSheet()
        } else {
            showProgress()
            productDetailPresenter.addToCart(sku, object : IResponseCallback {
                override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                    dismissProgressDialog()
                    trackAddToCartEvent()
                    onProductAddedToCart()
                }

                override fun onRequestError(baseResponse: BaseResponse<*>?) {
                    dismissProgressDialog()
                    warningFactory.showWarning(WarningFactory.ERROR_MESSAGE, baseResponse?.errorMessage)
                }
            })
        }
    }

    private fun showProgress() {
        if (progressDialog == null) {
            progressDialog = DialogProgressFragment.newInstance()
        }

        if (progressDialog!!.isAdded) {
            progressDialog!!.dismiss()
        }

        progressDialog?.run {
            isCancelable = true
            show(supportFragmentManager, null)
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.run {
            if (isAdded && context != null) {
                dismiss()
            }
        }
    }

    fun onProductAddedToCart() {
        try {
            if (getCurrentFragment() is ProductDetailMainFragment) {
                val productDetailMainFragment = supportFragmentManager
                        .findFragmentByTag(ProductDetailMainFragment::class.java.simpleName)
                        as ProductDetailMainFragment

                productDetailMainFragment.updateCartBadge()
            }
        } catch (ignored: Exception) {

        }
    }

    private fun onProductUpdateSizeVariation(sizeVariation: SimpleProduct) {
        try {
            if (getCurrentFragment() is ProductDetailMainFragment) {
                val productDetailMainFragment = supportFragmentManager
                        .findFragmentByTag(ProductDetailMainFragment::class.java.simpleName)
                        as ProductDetailMainFragment

                productDetailMainFragment.updateSizeVariation(sizeVariation)
            }
        } catch (ignored: Exception) {

        }
    }

    private fun productHasSizeVariation(): Boolean {
        productDetail.variations.let {
            for (variation in it) {
                if (variation.type == "size") {
                    return true
                }
            }
        }

        return false
    }

    private fun trackAddToCartEvent() {
        TrackerDelegator.trackProductAddedToCart(productDetail)

        val addToCartEventModel = MainEventModel(getString(TrackingPage.PDV.getName()), EventActionKeys.ADD_TO_CART,
                productDetail.sku, productDetail.price.price.toLong(), null)

        val cart = BamiloApplication.INSTANCE.cart

        if (cart != null) {
            addToCartEventModel.customAttributes = MainEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label,
                    cart.total.toLong(), true)
        } else {
            addToCartEventModel.customAttributes = MainEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label,
                    0, true)
        }

        TrackerManager.trackEvent(this, EventConstants.AddToCart, addToCartEventModel)
    }

    private fun getCurrentFragment(): Fragment {
        return try {
            val fragmentManager = supportFragmentManager
            val fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
            fragmentManager.findFragmentByTag(fragmentTag)!!
        } catch (e: Exception) {
            ProductDetailMainFragment()
        }
    }

    private fun displaySelectedScreen(fragmentTag: FragmentTag, showAnimation: Boolean = false) {
        val fragment = getFragment(fragmentTag) ?: return

        try {
            replaceFragment(fragment, showAnimation)
        } catch (ignored: Exception) {
        }
    }

    private fun displaySelectedScreen(fragment: Fragment, showAnimation: Boolean = false) {
        try {
            replaceFragment(fragment, showAnimation)
        } catch (ignored: Exception) {
        }
    }

    private fun replaceFragment(fragment: Fragment, showAnimation: Boolean = false) {
        val backStateName = fragment.javaClass.simpleName
        val ft = supportFragmentManager.beginTransaction()

        if (showAnimation) {
            ft.setCustomAnimations(R.anim.slide_from_right,
                    R.anim.slide_to_left,
                    R.anim.slide_from_left,
                    R.anim.slide_to_right)
        }

        ft.replace(R.id.pdv_frameLayout_fragmentContainer, fragment, backStateName)
        ft.addToBackStack(backStateName)

        ft.commit()
    }

    private fun getFragment(fragmentTag: FragmentTag): Fragment? {
        when (fragmentTag.name) {
            FragmentTag.PRODUCT_MAIN_VIEW.name -> {
                return ProductDetailMainFragment.newInstance(sku)
            }

            FragmentTag.OTHER_SELLERS.name -> {
                return SellersListFragment.newInstance(sku!!,
                        productDetail.title,
                        productDetail.image!!)
            }
            FragmentTag.DESCRIPTION.name -> {
                return TemporaryDescriptionFragment.newInstance(sku!!)
            }

            FragmentTag.SPECIFICATIONS.name -> {
                return SpecificationFragment.newInstance(sku!!)
            }
        }

        return ProductDetailMainFragment.newInstance(sku)
    }

    private fun trackCommentsView() {
        val viewCommentsModel = MainEventModel(getString(TrackingPage.PDV.getName()),
                EventActionKeys.RATE_TAPPED,
                sku,
                0,
                null)

        TrackerManager.trackEvent(this, EventConstants.RATE_TAPPED, viewCommentsModel)
    }

    override fun onBackButtonClicked() {
        onBackPressed()
    }

    override fun onAddToCartButtonClicked(sku: String) {
        addProductToCart(sku)
    }

    override fun onOtherVariationClicked(product: SimpleProduct) {
        if (product.sku == sku) {
            return
        }

        sku = product.sku
        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)
    }

    override fun onRelatedProductClicked(sku: String) {
        this.sku = sku
        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)
    }

    override fun onShowOtherSeller() {
        val viewOtherSellerModel = MainEventModel(getString(TrackingPage.PDV.getName()),
                EventActionKeys.OTHER_SELLERS_TAPPED,
                sku,
                0,
                null)

        TrackerManager.trackEvent(this, EventConstants.OTHER_SELLERS_TAPPED, viewOtherSellerModel)

        displaySelectedScreen(FragmentTag.OTHER_SELLERS)
    }

    override fun onShowDesAndSpecPage() {
        val viewDescView = MainEventModel(getString(TrackingPage.PDV.getName()),
                EventActionKeys.DESCRIPTION_TAPPED,
                sku,
                0,
                null)

        TrackerManager.trackEvent(this, EventConstants.DESCRIPTION_TAPPED, viewDescView)

        displaySelectedScreen(FragmentTag.DESCRIPTION, true)
    }

    override fun onShowSpecsAndSpecPage() {
        val viewSpecsModel = MainEventModel(getString(TrackingPage.PDV.getName()),
                EventActionKeys.SPECIFICATIONS_TAPPED,
                sku,
                0,
                null)

        TrackerManager.trackEvent(this, EventConstants.SPECIFICATIONS_TAPPED, viewSpecsModel)

        displaySelectedScreen(FragmentTag.SPECIFICATIONS, true)
    }

    override fun onShowAllReviewsClicked() {
        trackCommentsView()
        with(productDetail.rating) {
            displaySelectedScreen(CommentsFragment.newInstance(sku!!,
                    average,
                    5,
                    productDetail.reviews.total,
                    stars[0].count.toFloat(),
                    stars[1].count.toFloat(),
                    stars[2].count.toFloat(),
                    stars[3].count.toFloat(),
                    stars[4].count.toFloat()))
        }
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.GONE
    }

    override fun onShowSpecificComment(review: Review) {
        trackCommentsView()
        with(productDetail.rating) {
            displaySelectedScreen(CommentsFragment.newInstanceForJustOneDistinctComment(sku!!,
                    productDetail.rating.average,
                    productDetail.rating.total,
                    productDetail.reviews.total,
                    productDetail.rating.stars[0].count.toFloat(),
                    productDetail.rating.stars[1].count.toFloat(),
                    productDetail.rating.stars[2].count.toFloat(),
                    productDetail.rating.stars[3].count.toFloat(),
                    productDetail.rating.stars[4].count.toFloat(),
                    Gson().toJson(CommentViewModel(review.id!!, review.title, review.date!!, review.username!!,
                            review.is_bought_by_user, review.rate!!.toFloat(), review.comment!!,
                            review.like, review.dislike))))
        }
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.GONE
    }

    override fun onSubmitCommentButtonClicked() {
        if (!BamiloApplication.isCustomerLoggedIn()) {
            loginUser()
        } else {
            val viewAddReviewModel = MainEventModel(getString(TrackingPage.PDV.getName()),
                    EventActionKeys.ADD_REVIEW_TAPPED,
                    sku,
                    0,
                    null)

            TrackerManager.trackEvent(this, EventConstants.ADD_REVIEW_TAPPED, viewAddReviewModel)

            startSubmitRateActivity(this, sku!!)
        }
    }

    override fun loginUser() {
        val intent = Intent(this, MainFragmentActivity::class.java)

        val bundle = Bundle()
        bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true)

        intent.putExtra("pdv_login_bundle", bundle)
        startActivity(intent)
    }

    override fun showOutOfStock() {
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.GONE
        binding.productDetailLinearLayoutNotifyMe!!.visibility = View.VISIBLE
    }

    override fun showProgressView() {
        showProgress()
    }

    override fun dismissProgressView() {
        dismissProgressDialog()
    }

    override fun showErrorMessage(warningFact: Int, message: String) {
        if (::warningFactory.isInitialized) {
            warningFactory.showWarning(warningFact, message)
        }
    }

    override fun onAddToCartClicked() {
        addProductToCart(productDetail.simple_sku!!)
    }

    override fun onSizeVariationClicked(sizeVariation: SimpleProduct) {
        this.sizeVariation = sizeVariation.copy()
        onProductUpdateSizeVariation(this.sizeVariation)
    }

    override fun trackRemoveFromWishList() {
        TrackerDelegator.trackRemoveFromFavorites(productDetail)
    }

    override fun trackAddFromWishList() {
        TrackerDelegator.trackAddToFavorites(productDetail)
    }

    override fun onShowMoreRelatedProducts() {
        val intent = Intent(this, MainFragmentActivity::class.java)
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.MORE_RELATED_PRODUCTS)
        intent.putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false)

        startActivity(intent)
    }

    override fun onShowFragment(fragmentTag: FragmentTag) {
        displaySelectedScreen(fragmentTag)
    }

    override fun onBackPressed() {
        if (getCurrentFragment().tag == CommentsFragment::class.java.simpleName) {
            binding.productDetailLinearLayoutAddToCart!!.visibility = View.VISIBLE
        }

        if (productDetailPresenter.isBottomSheetShown()) {
            productDetailPresenter.hideBottomSheet()
            return
        }

        if (supportFragmentManager.backStackEntryCount == 1) {
            FragmentController.getInstance().popPdvFromBackStack()
            finish()
            return
        }

        super.onBackPressed()
    }

    override fun onProductReceived(product: ProductDetail) {
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.VISIBLE
        binding.productDetailLinearLayoutNotifyMe!!.visibility = View.GONE

        this.sku = product.sku
        productDetail = product
        sizeVariation.sku = null

        if (TextUtils.isEmpty(productDetail.sku)) {
            return
        }

        productDetailPresenter.fillChooseVariationBottomSheet(productDetail)

        val viewProductEventModel = MainEventModel("category",
                EventActionKeys.VIEW_PRODUCT,
                sku,
                product.price.price.replace(",", "").toLong(),
                MainEventModel.createViewProductEventModelAttributes("category uri key",
                        product.price.price.replace(",", "").toLong()))
        TrackerManager.trackEvent(this, EventConstants.ViewProduct, viewProductEventModel)
    }

    enum class FragmentTag {
        EMPTY,
        PRODUCT_MAIN_VIEW,
        DESCRIPTION,
        SPECIFICATIONS,
        RETURN_POLICY,
        OTHER_SELLERS
    }
}
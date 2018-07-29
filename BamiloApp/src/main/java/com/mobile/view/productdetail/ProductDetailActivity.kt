package com.mobile.view.productdetail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.product.comment.CommentViewModel
import com.bamilo.modernbamilo.product.comment.startCommentsActivity
import com.bamilo.modernbamilo.product.comment.startCommentsActivityForJustOneDistinctComment
import com.bamilo.modernbamilo.product.descspec.DescSpecFragment
import com.bamilo.modernbamilo.product.sellerslist.view.SellersListFragment
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
import com.mobile.utils.ConfigurationWrapper
import com.mobile.utils.dialogfragments.DialogProgressFragment
import com.mobile.utils.ui.WarningFactory
import com.mobile.view.MainFragmentActivity
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.mainfragment.ProductDetailMainFragment
import com.mobile.view.productdetail.model.ProductDetail
import com.mobile.view.productdetail.model.Review
import com.mobile.view.productdetail.model.SimpleProduct
import java.util.*

class ProductDetailActivity : BaseActivity(), PDVMainView, SellersListFragment.OnAddToCartButtonClickListener {
    private lateinit var productDetail: ProductDetail
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDetailPresenter: ProductDetailPresenter

    private var sizeVariation = SimpleProduct()

    private var sku: String? = ""
    private var productToShowSku: String? = ""

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

//    override fun attachBaseContext(newBase: Context?) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && newBase != null) {
//            super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, Locale("fa", "ir")))
//        } else {
//            super.attachBaseContext(newBase)
//        }
//    }

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

    private fun setupAddToCard() {
        binding.productDetailLinearLayoutAddToCart!!.visibility = View.GONE
        bindAddToCartClickListener()
    }

    private fun fetchExtraIntentData() {
        if (intent != null) {
            sku = intent.getStringExtra("sku")
            productToShowSku = sku
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
                }
            })
        }
    }

    private fun showProgress() {
        if (progressDialog == null) {
            progressDialog = DialogProgressFragment.newInstance()
        }

        progressDialog?.run {
            isCancelable = true
            show(supportFragmentManager, null)
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.run {
            dismiss()
        }
    }

    fun onProductAddedToCart() {
        if (getCurrentFragment() is ProductDetailMainFragment) {
            val productDetailMainFragment = supportFragmentManager
                    .findFragmentByTag(ProductDetailMainFragment::class.java.simpleName)
                    as ProductDetailMainFragment

            productDetailMainFragment.updateCartBadge()
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

    private fun displaySelectedScreen(fragmentTag: FragmentTag) {
        val fragment = getFragment(fragmentTag) ?: return

        try {
            replaceFragment(fragment)
        } catch (ignored: Exception) {
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.simpleName
        supportFragmentManager.beginTransaction().run {
            replace(R.id.pdv_frameLayout_fragmentContainer, fragment, backStateName)
            addToBackStack(backStateName)
            commit()
        }
    }

    private fun getFragment(fragmentTag: FragmentTag): Fragment? {
        when (fragmentTag.name) {
            FragmentTag.PRODUCT_MAIN_VIEW.name -> {
                return ProductDetailMainFragment.newInstance(productToShowSku)
            }

            FragmentTag.OTHER_SELLERS.name -> {
                return SellersListFragment.newInstance(sku!!,
                        productDetail.title,
                        productDetail.image!!)
            }
            FragmentTag.DESCRIPTION.name -> {
                return DescSpecFragment.newInstance(sku!!, DescSpecFragment.WHICH_SCREEN_DESC)
            }

            FragmentTag.SPECIFICATIONS.name -> {
                return DescSpecFragment.newInstance(sku!!, DescSpecFragment.WHICH_SCREEN_SPEC)
            }
        }

        return ProductDetailMainFragment.newInstance(productToShowSku)
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

        productToShowSku = product.sku
        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)
    }

    override fun onRelatedProductClicked(sku: String) {
        productToShowSku = sku
        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)
    }

    override fun onShowOtherSeller() {
        displaySelectedScreen(FragmentTag.OTHER_SELLERS)
    }

    override fun onShowDesAndSpecPage() {
        displaySelectedScreen(FragmentTag.DESCRIPTION)
    }

    override fun onShowSpecsAndSpecPage() {
        displaySelectedScreen(FragmentTag.SPECIFICATIONS)
    }

    override fun onShowAllReviewsClicked() {
        with(productDetail.rating) {
            startCommentsActivity(this@ProductDetailActivity, sku!!,
                    average,
                    5,
                    productDetail.reviews.total,
                    stars[0].count.toFloat(),
                    stars[1].count.toFloat(),
                    stars[2].count.toFloat(),
                    stars[3].count.toFloat(),
                    stars[4].count.toFloat())
        }
    }

    override fun onShowSpecificComment(review: Review) {
        startCommentsActivityForJustOneDistinctComment(this,
                sku!!,
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
                        review.like, review.dislike)))
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
        this.sizeVariation = sizeVariation
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

        productDetail = product
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
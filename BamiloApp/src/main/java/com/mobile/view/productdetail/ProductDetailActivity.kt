package com.mobile.view.productdetail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mobile.app.BamiloApplication
import com.mobile.classes.models.BaseScreenModel
import com.mobile.classes.models.MainEventModel
import com.mobile.constants.tracking.EventActionKeys
import com.mobile.constants.tracking.EventConstants
import com.mobile.managers.TrackerManager
import com.mobile.service.tracking.TrackingPage
import com.mobile.utils.ConfigurationWrapper
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.mainfragment.ProductDetailMainFragment
import com.mobile.view.productdetail.model.Product
import com.mobile.view.productdetail.model.ProductDetail
import java.util.*

class ProductDetailActivity : AppCompatActivity(), PDVMainView {

    private lateinit var productDetail: ProductDetail
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDetailPresenter: ProductDetailPresenter

    private var changeProductDetailOnViewVisible = false

    private var sizeVariation = Product()

    private var sku: String? = ""

    companion object {
        @JvmStatic
        fun start(invokerContext: Context, sku: String) {
            val intent = Intent(invokerContext, ProductDetailActivity::class.java)
            intent.putExtra("sku", sku)
            invokerContext.startActivity(intent)
        }
    }

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
        productDetailPresenter = ProductDetailPresenter(this, binding, this)

        fetchExtraIntentData()
        bindAddToCartClickListener()

        displaySelectedScreen(FragmentTag.PRODUCT_MAIN_VIEW)

        val screenModel = BaseScreenModel(
                getString(TrackingPage.PRODUCT_DETAIL.getName()),
                getString(R.string.gaScreen), "", System.currentTimeMillis())
        TrackerManager.trackScreen(this, screenModel, false)
    }

    private fun fetchExtraIntentData() {
        if (intent != null) {
            sku = intent.getStringExtra("sku")
        }
    }

    private fun bindAddToCartClickListener() {
        binding.productDetailLinearLayoutAddToCart!!.setOnClickListener({
            if (sizeVariation.sku.isEmpty()) {
                productDetailPresenter.showBottomSheet()
            } else {
                trackAddToCartEvent()
            }
        })
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
            fragmentManager.findFragmentByTag(fragmentTag)
        } catch (e: Exception) {
            ProductDetailMainFragment.newInstance(sku)
        }
    }

    private fun displaySelectedScreen(fragmentTag: FragmentTag) {
        val fragment = getFragment(fragmentTag) ?: return

//        if (getCurrentFragment() == fragment /* and if selected product sku is equal to current sku*/) {
//            return
//        }

        try {
            replaceFragment(fragment)
        } catch (ignored: Exception) {
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.simpleName
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.pdv_frameLayout_fragmentContainer, fragment, backStateName)
        ft.addToBackStack(backStateName)
        ft.commit()
    }

    private fun getFragment(fragmentTag: FragmentTag): Fragment? {
        when (fragmentTag.name) {
            FragmentTag.PRODUCT_MAIN_VIEW.name -> {
                return ProductDetailMainFragment.newInstance(sku)
            }
            FragmentTag.RATE_AND_REVIEW.name -> {
            }
        }

        return ProductDetailMainFragment.newInstance(sku)
    }

    override fun onBackButtonClicked() {
        onBackPressed()
    }

    override fun onOtherVariationClicked(product: Product) {
        changeProductDetailOnViewVisible = if (getCurrentFragment() is ProductDetailMainFragment) {
            val productDetailMainFragment = supportFragmentManager
                    .findFragmentByTag(ProductDetailMainFragment::class.java.simpleName)
                    as ProductDetailMainFragment
            productDetailMainFragment.reloadData(product.sku)
            false
        } else {
            true
        }
    }

    override fun onSizeVariationClicked(sizeVariation: Product) {
        this.sizeVariation = sizeVariation
    }

    override fun onShowFragment(fragmentTag: FragmentTag) {
        displaySelectedScreen(fragmentTag)
    }

    override fun onBackPressed() {
        if (productDetailPresenter.isBottomSheetShown()) {
            productDetailPresenter.hideBottomSheet()
            return
        }

        if (fragmentManager.backStackEntryCount == 0) {
            finish()
            return
        }

        super.onBackPressed()
    }

    override fun onProductReceived(product: ProductDetail) {
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
        RATE_AND_REVIEW,
        SPECIFICATIONS_DESCRIPTION,
        RETURN_POLICY
    }
}

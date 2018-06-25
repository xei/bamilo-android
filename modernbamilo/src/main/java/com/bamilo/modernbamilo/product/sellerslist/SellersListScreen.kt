package com.bamilo.modernbamilo.product.sellerslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager



const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
const val KEY_EXTRA_PRODUCT_TITLE = "KEY_EXTRA_PRODUCT_TITLE"
const val KEY_EXTRA_PRODUCT_THUMBNAIL_URL = "KEY_EXTRA_PRODUCT_THUMBNAIL_URL"
const val KEY_EXTRA_PRODUCT_VARIANT = "KEY_EXTRA_PRODUCT_VARIANT"

fun startActivity(invokerContext: Context, productId: String, productTitle: String, productThumbnailUrl: String, productVariant: String) {
    val startIntent = Intent(invokerContext, SellersListActivity::class.java)
    startIntent.putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    startIntent.putExtra(KEY_EXTRA_PRODUCT_TITLE, productTitle)
    startIntent.putExtra(KEY_EXTRA_PRODUCT_THUMBNAIL_URL, productThumbnailUrl)
    startIntent.putExtra(KEY_EXTRA_PRODUCT_VARIANT, productVariant)
    invokerContext.startActivity(startIntent)
}

class SellersListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mViewModel: ViewModel

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView

    private lateinit var mProductThumbnailImageView: ImageView
    private lateinit var mProductTitleTextView: TextView
    private lateinit var mProductVariantTextView: TextView

    private lateinit var mPriceFilterButton: FilterButton
    private lateinit var mRateFilterButton: FilterButton
    private lateinit var mLeadTimeFilterButton: FilterButton

    private lateinit var mSellersRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sellers_list)

        createViewModel()
        findViews()
        mPriceFilterButton.selectButton()
        bindViewModel()
        setOnClickListeners()
        initSellersRecyclerView()
    }

    private fun createViewModel() {

        val sellers = ArrayList<SellerViewModel>()
        sellers.add(SellerViewModel("123", "ایران رهجو مرکزی", "پنجشنبه ۲۴ اسفند ۹۸", 4.3F, "۲۰۳۴۹۰۰۰", "۱۱۳۴۶۰۰۰", 33))
        sellers.add(SellerViewModel("123", "ایران رهجو مرکزی", "پنجشنبه ۲۴ اسفند ۹۸", 4.3F, "۲۰۳۴۹۰۰۰", "۱۱۳۴۶۰۰۰", 33))
        sellers.add(SellerViewModel("123", "ایران رهجو مرکزی", "پنجشنبه ۲۴ اسفند ۹۸", 4.3F, "۲۰۳۴۹۰۰۰", "۱۱۳۴۶۰۰۰", 33))
        sellers.add(SellerViewModel("123", "ایران رهجو مرکزی", "پنجشنبه ۲۴ اسفند ۹۸", 4.3F, "۲۰۳۴۹۰۰۰", "۱۱۳۴۶۰۰۰", 33))
        sellers.add(SellerViewModel("123", "ایران رهجو مرکزی", "پنجشنبه ۲۴ اسفند ۹۸", 4.3F, "۲۰۳۴۹۰۰۰", "۱۱۳۴۶۰۰۰", 33))

        mViewModel = ViewModel(intent.getStringExtra(KEY_EXTRA_PRODUCT_TITLE),
                intent.getStringExtra(KEY_EXTRA_PRODUCT_THUMBNAIL_URL),
                intent.getStringExtra(KEY_EXTRA_PRODUCT_VARIANT), sellers)
    }

    private fun findViews() {
        mCloseBtnImageButton = findViewById(R.id.layoutToolbar_imageButton_close)
        mToolbarTitleTextView = findViewById(R.id.layoutToolbar_xeiTextView_title)
        mProductThumbnailImageView = findViewById(R.id.layoutSellerslistProduct_imageView_thumbnail)
        mProductTitleTextView = findViewById(R.id.layoutSellerslistProduct_xeiTextView_title)
        mProductVariantTextView = findViewById(R.id.layoutSellerslistProduct_xeiTextView_variant)
        mPriceFilterButton = findViewById(R.id.activitySellersList_filterButton_price)
        mRateFilterButton = findViewById(R.id.activitySellersList_filterButton_rate)
        mLeadTimeFilterButton = findViewById(R.id.activitySellersList_filterButton_leadTime)
        mSellersRecyclerView = findViewById(R.id.activitySellersList_recyclerView_sellers)
    }

    private fun bindViewModel() {
        mToolbarTitleTextView.text = "فروشندگان دیگر"

        mProductThumbnailImageView.loadImageFromNetwork(mViewModel.productThumbnailUrl)
        mProductTitleTextView.text = mViewModel.productTitle
        mProductVariantTextView.text = mViewModel.productVariant
    }

    private fun setOnClickListeners() {
        mCloseBtnImageButton.setOnClickListener(this)
        mPriceFilterButton.setOnClickListener(this)
        mRateFilterButton.setOnClickListener(this)
        mLeadTimeFilterButton.setOnClickListener(this)
    }

    private fun initSellersRecyclerView() {
        mSellersRecyclerView.layoutManager = LinearLayoutManager(this)
        mSellersRecyclerView.itemAnimator = DefaultItemAnimator()
        mSellersRecyclerView.adapter = SellersListAdapter(mViewModel.sellersViewModel)

    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> finish()
            R.id.activitySellersList_filterButton_price -> {
                mPriceFilterButton.selectButton()
                mRateFilterButton.deselectButton()
                mLeadTimeFilterButton.deselectButton()
            }
            R.id.activitySellersList_filterButton_rate -> {
                mPriceFilterButton.deselectButton()
                mRateFilterButton.selectButton()
                mLeadTimeFilterButton.deselectButton()
            }
            R.id.activitySellersList_filterButton_leadTime -> {
                mPriceFilterButton.deselectButton()
                mRateFilterButton.deselectButton()
                mLeadTimeFilterButton.selectButton()
            }
        }
    }

}

data class ViewModel(val productTitle: String, val productThumbnailUrl: String, val productVariant: String, val sellersViewModel: ArrayList<SellerViewModel>)
data class SellerViewModel(val sellerId: String, val title: String, val deliveryTime: String, val rate: Float, val baseAmount: String, val payableAmount: String, val discount: Int)
package com.bamilo.modernbamilo.product.sellerslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.product.sellerslist.pojo.SellerViewModel
import com.bamilo.modernbamilo.product.sellerslist.pojo.SellersListScreenViewModel
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG_DEBUG = "SellersListScreen"

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

    private val mWebApi = RetrofitHelper.makeWebApi(this, SellersListWebApi::class.java)

    private lateinit var mViewModel: SellersListScreenViewModel

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

        findViews()
        createViewModel()
        bindViewModel()
        setOnClickListeners()
        initSellersRecyclerView()
        loadSellers()

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

    private fun createViewModel() {
        mViewModel = SellersListScreenViewModel(intent.getStringExtra(KEY_EXTRA_PRODUCT_TITLE),
                intent.getStringExtra(KEY_EXTRA_PRODUCT_THUMBNAIL_URL),
                intent.getStringExtra(KEY_EXTRA_PRODUCT_VARIANT), ArrayList())
    }

    private fun bindViewModel() {
        mToolbarTitleTextView.text = resources.getString(R.string.sellersList_title)

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

    private fun loadSellers() {
        val call = mWebApi.getSellers(intent.getStringExtra(KEY_EXTRA_PRODUCT_ID))
        call.enqueue(object: Callback<ResponseWrapper<ArrayList<SellerViewModel>>> {

            override fun onResponse(call: Call<ResponseWrapper<ArrayList<SellerViewModel>>>?, response: Response<ResponseWrapper<ArrayList<SellerViewModel>>>?) {
                val sellers = response?.body()?.metadata

                if (sellers != null && sellers.size == 0) {
                    mViewModel.sellersViewModel.removeAll(mViewModel.sellersViewModel)
                    mViewModel.sellersViewModel.addAll(sellers)
                    sortSellersByPayableAmount()
                }

            }

            override fun onFailure(call: Call<ResponseWrapper<ArrayList<SellerViewModel>>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
            }

        })
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> finish()
            R.id.activitySellersList_filterButton_price -> sortSellersByPayableAmount()
            R.id.activitySellersList_filterButton_rate -> sortSellersByRate()
            R.id.activitySellersList_filterButton_leadTime -> sortSellersByLeadTime()
        }
    }

    private fun sortSellersByPayableAmount() {
        mViewModel.sellersViewModel.sortWith(compareBy(SellerViewModel::payableAmount))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.selectButton()
        mRateFilterButton.deselectButton()
        mLeadTimeFilterButton.deselectButton()
    }

    private fun sortSellersByRate() {
        mViewModel.sellersViewModel.sortWith(compareByDescending(SellerViewModel::rate))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.deselectButton()
        mRateFilterButton.selectButton()
        mLeadTimeFilterButton.deselectButton()
    }

    private fun sortSellersByLeadTime() {
        mViewModel.sellersViewModel.sortWith(compareBy(SellerViewModel::deliveryTime))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.deselectButton()
        mRateFilterButton.deselectButton()
        mLeadTimeFilterButton.selectButton()
    }

}
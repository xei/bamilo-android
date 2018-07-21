package com.bamilo.modernbamilo.product.sellerslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger

private const val TAG_DEBUG = "SellersListScreen"

const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
const val KEY_EXTRA_PRODUCT_TITLE = "KEY_EXTRA_PRODUCT_TITLE"
const val KEY_EXTRA_PRODUCT_THUMBNAIL_URL = "KEY_EXTRA_PRODUCT_THUMBNAIL_URL"

fun startActivity(context: Context, productId: String, productTitle: String, productThumbnailUrl: String) {
    val startIntent = Intent(context, SellersListActivity::class.java).apply {
        putExtra(KEY_EXTRA_PRODUCT_ID, productId)
        putExtra(KEY_EXTRA_PRODUCT_TITLE, productTitle)
        putExtra(KEY_EXTRA_PRODUCT_THUMBNAIL_URL, productThumbnailUrl)
    }
    context.startActivity(startIntent)
}

class SellersListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mViewModel: SellersListScreenViewModel
    private val mRepository = SellersListRepository(this)

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView

    private lateinit var mProductThumbnailImageView: ImageView
    private lateinit var mProductTitleTextView: TextView

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
        mPriceFilterButton = findViewById(R.id.activitySellersList_filterButton_price)
        mRateFilterButton = findViewById(R.id.activitySellersList_filterButton_rate)
        mLeadTimeFilterButton = findViewById(R.id.activitySellersList_filterButton_leadTime)
        mSellersRecyclerView = findViewById(R.id.activitySellersList_recyclerView_sellers)
    }

    private fun createViewModel() {
        mViewModel = SellersListScreenViewModel(
                intent.getStringExtra(KEY_EXTRA_PRODUCT_TITLE),
                intent.getStringExtra(KEY_EXTRA_PRODUCT_THUMBNAIL_URL)
        )
    }

    private fun bindViewModel() {
        mToolbarTitleTextView.text = resources.getString(R.string.sellersList_title)

        mProductThumbnailImageView.loadImageFromNetwork(mViewModel.productThumbnailUrl)
        mProductTitleTextView.text = mViewModel.productTitle
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
        mRepository.getAllSellersList(intent.getStringExtra(KEY_EXTRA_PRODUCT_ID), object: SellersListRepository.OnSellersListLoadListener {

            override fun onSucceed(sellersListItemViewModels: ArrayList<SellersListItemViewModel>) {
                if (sellersListItemViewModels.size != 0) {
                    mViewModel.sellersViewModel.removeAll(mViewModel.sellersViewModel)
                    mViewModel.sellersViewModel.addAll(sellersListItemViewModels)
                    sortSellersByPayableAmount()
                }
            }

            override fun onFailure(msg: String) {
                Logger.log(msg, TAG_DEBUG, LogType.ERROR)
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
        mViewModel.sellersViewModel.sortWith(compareBy(SellersListItemViewModel::payableAmount))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.selectButton()
        mRateFilterButton.deselectButton()
        mLeadTimeFilterButton.deselectButton()
    }

    private fun sortSellersByRate() {
        mViewModel.sellersViewModel.sortWith(compareByDescending(SellersListItemViewModel::rate))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.deselectButton()
        mRateFilterButton.selectButton()
        mLeadTimeFilterButton.deselectButton()
    }

    private fun sortSellersByLeadTime() {
        mViewModel.sellersViewModel.sortWith(compareBy(SellersListItemViewModel::deliveryTime))
        mSellersRecyclerView.adapter?.notifyDataSetChanged()
        mSellersRecyclerView.smoothScrollToPosition(0)

        mPriceFilterButton.deselectButton()
        mRateFilterButton.deselectButton()
        mLeadTimeFilterButton.selectButton()
    }

}
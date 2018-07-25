package com.bamilo.modernbamilo.product.sellerslist.view


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.sellerslist.model.SellersListRepository
import com.bamilo.modernbamilo.product.sellerslist.view.customview.FilterButton
import com.bamilo.modernbamilo.product.sellerslist.viewmodel.SellersListItemViewModel
import com.bamilo.modernbamilo.product.sellerslist.viewmodel.SellersListScreenViewModel
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger

private const val TAG_DEBUG = "SellersListFragment"

const val ARG_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
const val ARG_PRODUCT_TITLE = "KEY_EXTRA_PRODUCT_TITLE"
const val ARG_PRODUCT_THUMBNAIL_URL = "KEY_EXTRA_PRODUCT_THUMBNAIL_URL"

/**
 * A simple [Fragment] subclass.
 * Use the [SellersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SellersListFragment : Fragment(), View.OnClickListener {

    private lateinit var mViewModel: SellersListScreenViewModel
    private lateinit var mRepository: SellersListRepository

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView

    private lateinit var mProductThumbnailImageView: ImageView
    private lateinit var mProductTitleTextView: TextView

    private lateinit var mPriceFilterButton: FilterButton
    private lateinit var mRateFilterButton: FilterButton
    private lateinit var mLeadTimeFilterButton: FilterButton

    private lateinit var mSellersRecyclerView: RecyclerView
    private lateinit var mOnAddToCartButtonClickListener: OnAddToCartButtonClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sellers_list, container, false)
        findViews(view)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnAddToCartButtonClickListener) {
            mOnAddToCartButtonClickListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createViewModel()
        createRepository()
        bindViewModel()
        setOnClickListeners()
        initSellersRecyclerView()
        loadSellers()
    }

    companion object {
        @JvmStatic
        fun newInstance(productId: String, productTitle: String, productThumbnailUrl: String) =
                SellersListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                        putString(ARG_PRODUCT_TITLE, productTitle)
                        putString(ARG_PRODUCT_THUMBNAIL_URL, productThumbnailUrl)
                    }
                }
    }

    private fun findViews(rootView: View) {
        mCloseBtnImageButton = rootView.findViewById(R.id.layoutToolbar_imageButton_close)
        mToolbarTitleTextView = rootView.findViewById(R.id.layoutToolbar_xeiTextView_title)
        mProductThumbnailImageView = rootView.findViewById(R.id.layoutSellerslistProduct_imageView_thumbnail)
        mProductTitleTextView = rootView.findViewById(R.id.layoutSellerslistProduct_xeiTextView_title)
        mPriceFilterButton = rootView.findViewById(R.id.activitySellersList_filterButton_price)
        mRateFilterButton = rootView.findViewById(R.id.activitySellersList_filterButton_rate)
        mLeadTimeFilterButton = rootView.findViewById(R.id.activitySellersList_filterButton_leadTime)
        mSellersRecyclerView = rootView.findViewById(R.id.activitySellersList_recyclerView_sellers)
    }

    private fun createViewModel() {
        arguments?.let {
            mViewModel = SellersListScreenViewModel(
                    it.getString(ARG_PRODUCT_ID),
                    it.getString(ARG_PRODUCT_TITLE),
                    it.getString(ARG_PRODUCT_THUMBNAIL_URL)
            )
        }
    }

    private fun createRepository() {
        mRepository = SellersListRepository(context!!)
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
        mSellersRecyclerView.layoutManager = LinearLayoutManager(context)
        mSellersRecyclerView.itemAnimator = DefaultItemAnimator()
        mSellersRecyclerView.adapter = SellersListAdapter(mViewModel.sellersViewModel, mOnAddToCartButtonClickListener)

    }

    private fun loadSellers() {
        mRepository.getAllSellersList(mViewModel.productId, object: SellersListRepository.OnSellersListLoadListener {

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
            R.id.layoutToolbar_imageButton_close -> activity?.onBackPressed()
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

    interface OnAddToCartButtonClickListener {
        fun onAddToCartButtonClicked(sku: String)
    }
}

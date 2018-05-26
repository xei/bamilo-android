package com.mobile.view.relatedproducts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.emarsys.predict.RecommendedItem
import com.mobile.app.BamiloApplication
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.constants.ConstantsIntentExtra
import com.mobile.controllers.fragments.FragmentController
import com.mobile.controllers.fragments.FragmentType
import com.mobile.service.pojo.IntConstants
import com.mobile.utils.MyMenuItem
import com.mobile.utils.NavigationAction
import com.mobile.view.BaseActivity
import com.mobile.view.R
import com.mobile.view.fragments.BaseFragment
import com.mobile.view.relatedproducts.viewtypes.RelatedProductsItem
import kotlinx.android.synthetic.main.fragment_related_products.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Farshid
 * since 5/21/2018.
 * contact farshidabazari@gmail.com
 */
class RecommendProductsFragment : BaseFragment(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET),
        NavigationAction.RELATED_PRODUCTS,
        R.layout.fragment_related_products,
        IntConstants.ACTION_BAR_NO_TITLE,
        BaseFragment.NO_ADJUST_CONTENT) {

    private var mGABeginRequestMillis: Long = 0
    private var argument: Bundle? = null

    private lateinit var mGhostAdapter: GhostAdapter
    private lateinit var items: ArrayList<Any>

    private lateinit var rootView: View

    private var mRecommendProductViewModel: RecommendProductViewModel? = null

    private var itemId: String = ""
    private var logic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGABeginRequestMillis = System.currentTimeMillis()
        argument = savedInstanceState ?: arguments

        fetchDataFromArguments(argument)
    }

    private fun fetchDataFromArguments(argument: Bundle?) {
        if (arguments == null) {
            itemId = ""
            logic = "POPULAR"

            logic = if (BamiloApplication.isCustomerLoggedIn()) {
                ConstantsIntentExtra.PERSONAL
            } else {
                ConstantsIntentExtra.POPULAR
            }

            return
        }

        itemId = argument!!.getString(ConstantsIntentExtra.CONTENT_ID, "")
        logic = argument.getString(ConstantsIntentExtra.LOGIC, ConstantsIntentExtra.POPULAR)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        initRecyclerView()
        getRelatedProducts()
    }

    private fun initRecyclerView() {
        mGhostAdapter = GhostAdapter()
        items = ArrayList()

        rootView.relatedProducts_recyclerView_relatedProducts.adapter = mGhostAdapter
        rootView.relatedProducts_recyclerView_relatedProducts.layoutManager = GridLayoutManager(context, 2)
    }

    private fun getRelatedProducts() {
        if (mRecommendProductViewModel == null) {
            mRecommendProductViewModel = ViewModelProviders.of(this).get(RecommendProductViewModel::class.java)
        }
        mRecommendProductViewModel!!.getProductList(itemId, logic)
                .observe(this,
                        Observer<List<RecommendedItem>?> { data: List<RecommendedItem>? ->
                            updateUi(data)
                        })
    }

    private fun updateUi(data: List<RecommendedItem>?) {
        data?.forEach { recommendedItem: RecommendedItem ->
            val relatedProductsItem = RelatedProductsItem(recommendedItem)
            relatedProductsItem.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(obj: Any) {
                    gotoProductDetailPage(obj as RecommendedItem)
                }
            })
            items.add(relatedProductsItem)
        }
        mGhostAdapter.setItems(items)
    }

    private fun gotoProductDetailPage(recommendedItem: RecommendedItem) {
        val bundle = Bundle()
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, recommendedItem.data["item"].toString())
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true)
        (context as BaseActivity).onSwitchFragment(FragmentType.PRODUCT_DETAILS,
                bundle,
                FragmentController.ADD_TO_BACK_STACK)
    }
}

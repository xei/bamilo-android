package com.bamilo.modernbamilo.product.descspec


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.customview.XeiTextView


private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [DescSpecFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DescSpecFragment : Fragment() {
    private var mProductId: String? = null

    private lateinit var mPagerViewPager: ViewPager
    private lateinit var mTabsTabLayout: TabLayout

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param productId Product ID
         * @return A new instance of fragment DescSpecFragment.
         */
        @JvmStatic
        fun newInstance(productId: String) =
                DescSpecFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mProductId = it.getString(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_desc_spec, container, false)

        findViews(rootView)
        initViewPager()
        initTabLayout()

        return rootView
    }

    private fun findViews(rootView: View) {
        mPagerViewPager = rootView.findViewById(R.id.fragmentDescSpec_viewPager_pager)
        mTabsTabLayout = rootView.findViewById(R.id.fragmentDescSpec_tabLayout_tabs)
    }

    private fun initViewPager() {
        mPagerViewPager.adapter = DescSpecPagerAdapter(fragmentManager!!, mProductId!!)
        mPagerViewPager.currentItem = 1
    }

    private fun initTabLayout() {
        mTabsTabLayout.setupWithViewPager(mPagerViewPager)

        val descriptionTabView = LayoutInflater.from(context).inflate(R.layout.row_tablayout, null) as XeiTextView
        val specificationTabView = LayoutInflater.from(context).inflate(R.layout.row_tablayout, null) as XeiTextView

        descriptionTabView.text = "توضیحات"
        specificationTabView.text = "مشخصات"

        descriptionTabView.isSelected = true

        mTabsTabLayout.getTabAt(1)?.customView = descriptionTabView
        mTabsTabLayout.getTabAt(0)?.customView = specificationTabView

    }

}

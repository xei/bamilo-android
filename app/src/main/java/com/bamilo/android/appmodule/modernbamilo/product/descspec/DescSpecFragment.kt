//package com.bamilo.android.appmodule.modernbamilo.product.descspec
//
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.support.design.widget.TabLayout
//import android.support.v4.app.Fragment
//import android.support.v4.view.ViewPager
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.bamilo.android.R
//import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView
//
//
//private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"
//private const val ARG_WHICH_SCREEN = "ARG_WHICH_SCREEN"
//
///**
// * A simple [Fragment] subclass.
// * Use the [DescSpecFragment.newInstance] factory method to
// * create an instance of this fragment.
// *
// */
//class DescSpecFragment : Fragment() {
//    private var mProductId: String? = null
//    private var mWhichPage: Int = 0
//
//    private lateinit var mPagerViewPager: ViewPager
//    private lateinit var mTabsTabLayout: TabLayout
//
//    companion object {
//        const val WHICH_SCREEN_DESC = 1
//        const val WHICH_SCREEN_SPEC = 0
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param productId Product ID
//         * @return A new instance of fragment DescSpecFragment.
//         */
//        @JvmStatic
//        fun newInstance(productId: String, whichScreen: Int) =
//                DescSpecFragment().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PRODUCT_ID, productId)
//                        putInt(ARG_WHICH_SCREEN, whichScreen)
//                    }
//                }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            mProductId = it.getString(ARG_PRODUCT_ID)
//            mWhichPage = it.getInt(ARG_WHICH_SCREEN)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//
//        val rootView = inflater.inflate(R.layout.fragment_desc_spec, container, false)
//
//        findViews(rootView)
//        initViewPager()
//        initTabLayout()
//
//        return rootView
//    }
//
//    private fun findViews(rootView: View) {
//        mPagerViewPager = rootView.findViewById(R.id.fragmentDescSpec_viewPager_pager)
//        mTabsTabLayout = rootView.findViewById(R.id.fragmentDescSpec_tabLayout_tabs)
//    }
//
//    private fun initViewPager() {
//        mPagerViewPager.adapter = DescSpecPagerAdapter(childFragmentManager, mProductId!!)
//        mPagerViewPager.currentItem = mWhichPage
//    }
//
//    @SuppressLint("InflateParams")
//    private fun initTabLayout() {
//        mTabsTabLayout.setupWithViewPager(mPagerViewPager)
//
//        val descriptionTabView = LayoutInflater.from(context).inflate(R.layout.row_tablayout, null) as XeiTextView
//        val specificationTabView = LayoutInflater.from(context).inflate(R.layout.row_tablayout, null) as XeiTextView
//
//        descriptionTabView.text = resources.getString(R.string.decSpec_tab_description)
//        specificationTabView.text = resources.getString(R.string.decSpec_tab_specification)
//
//        descriptionTabView.isSelected = true
//
//        mTabsTabLayout.getTabAt(WHICH_SCREEN_SPEC)?.customView = specificationTabView
//        mTabsTabLayout.getTabAt(WHICH_SCREEN_DESC)?.customView = descriptionTabView
//    }
//}

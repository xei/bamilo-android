package com.mobile.view.relatedproducts

import com.mobile.service.pojo.IntConstants
import com.mobile.utils.MyMenuItem
import com.mobile.utils.NavigationAction
import com.mobile.view.R
import com.mobile.view.fragments.BaseFragment
import java.util.*

/**
 * Created by Farshid
 * since 5/21/2018.
 * contact farshidabazari@gmail.com
 */
class RelatedProductsFragment : BaseFragment(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET),
        NavigationAction.RELATED_PRODUCTS,
        R.layout.pdv_fragment_main,
        IntConstants.ACTION_BAR_NO_TITLE,
        BaseFragment.NO_ADJUST_CONTENT) {

    init {
    }
}
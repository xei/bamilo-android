package com.bamilo.modernbamilo.product.descspec

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.bamilo.modernbamilo.product.descspec.spec.SpecificationFragment
import com.bamilo.modernbamilo.product.descspec.tempdesc.TemporaryDescriptionFragment

class DescSpecPagerAdapter(fm: FragmentManager, private val productId: String): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        DescSpecFragment.WHICH_SCREEN_SPEC -> TemporaryDescriptionFragment.newInstance(productId)
        DescSpecFragment.WHICH_SCREEN_DESC -> SpecificationFragment.newInstance(productId)
        else -> throw Exception("THIS EXCEPTION NEVER THROWS!")
    }

    override fun getCount() = 2

}
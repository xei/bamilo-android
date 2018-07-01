package com.bamilo.modernbamilo.product.descspec

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.bamilo.modernbamilo.product.descspec.desc.DescriptionFragment
import com.bamilo.modernbamilo.product.descspec.spec.SpecificationFragment

class DescSpecPagerAdapter(fm: FragmentManager, private val productId: String): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        1 -> DescriptionFragment.newInstance(productId)
        0 -> SpecificationFragment.newInstance(productId)
        else -> throw Exception("TRAP")
    }

    override fun getCount() = 2

}
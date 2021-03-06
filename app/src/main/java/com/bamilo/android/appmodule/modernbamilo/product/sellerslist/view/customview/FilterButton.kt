/*
 * Copyright 2018 Bamilo, Inc.
 */

package com.bamilo.android.appmodule.modernbamilo.product.sellerslist.view.customview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView

/**
 * This custom view is a selectable round button to filter sellers list based on some criteria.
 */
class FilterButton : XeiTextView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        deselectButton()
    }

    fun selectButton() {
        setBackgroundResource(R.drawable.background_filter_btn)
        setTextColor(ContextCompat.getColor(context, R.color.sellersList_background))
    }

    fun deselectButton() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
        setTextColor(ContextCompat.getColor(context, R.color.sellersList_filterBtn))
    }
}

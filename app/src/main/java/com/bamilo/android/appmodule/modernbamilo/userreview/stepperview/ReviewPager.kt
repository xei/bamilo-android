package com.bamilo.android.appmodule.modernbamilo.userreview.stepperview

import android.content.Context
import android.util.AttributeSet
import com.bamilo.android.appmodule.modernbamilo.customview.RtlViewPager


class ReviewPager : RtlViewPager {
    constructor(context: Context) : super(context) {
        setTransformationAnimation()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setTransformationAnimation()
    }

    /**
     * This method sets the transformer animation while swiping the pages.
     * You can check out some animations in the following page:
     * https://github.com/geftimov/android-viewpager-transformers
     */
    private fun setTransformationAnimation() {

        setPageTransformer(false) { view, position ->
            view.rotationY = position * -30
        }

    }

}

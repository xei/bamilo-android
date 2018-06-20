package com.bamilo.modernbamilo.userreview.stepperview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bamilo.modernbamilo.R

/**
 * This view is a stepper view that can bind to a ViewPager and step through the swipe
 */
class StepperView : LinearLayout {

    private val mStepViewsList: ArrayList<View> = ArrayList()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

//    /**
//     * This method must call when the ViewPager has bined to it's adapter, otherwise it causes an exception.
//     */
//    fun setViewPager(viewPager: RtlViewPager) {
//        setPagesCount(viewPager.adapter!!.count)
//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                setCurrentPage(position)
//            }
//
//        })
//    }

    /**
     * This method add some StepViews to the stepper based on the pages count in the ViewPager
     */
    fun setPagesCount(pagesCount: Int) {
        weightSum = pagesCount.toFloat()
        val firstStepView = LayoutInflater.from(context).inflate(R.layout.layout_stepper_step, this, false)
        mStepViewsList.add(firstStepView)
        addView(firstStepView)
        for (i in 2..pagesCount) {
            val stepView = LayoutInflater.from(context).inflate(R.layout.layout_stepper_step, this, false)
            mStepViewsList.add(stepView)
            addView(stepView)
        }
    }

    /**
     * This method will call while new page has selected and change the step.
     */
    fun setCurrentPage(currentPage: Int) {
        for (i in 0 until mStepViewsList.size) {
            mStepViewsList[i].setBackgroundResource(if (i <= currentPage) R.drawable.background_step_full else R.drawable.background_step_empty)
        }
    }

}

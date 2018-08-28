package com.bamilo.android.appmodule.modernbamilo.userreview.optionview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bamilo.android.R

class CheckboxOptionView : RelativeLayout {

    private lateinit var mTextCheckBox: CheckBox

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, width: Int, height: Int, marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) :super(context) {
        val linearLayoutParams = LinearLayout.LayoutParams(dpToPx(width), dpToPx(height))
        linearLayoutParams.setMargins(dpToPx(marginLeft), dpToPx(marginTop), dpToPx(marginRight), dpToPx(marginBottom))
        layoutParams = linearLayoutParams
        requestLayout()

        init()
    }

    private fun init() {
        mTextCheckBox = LayoutInflater.from(context).inflate(R.layout.layout_review_checkbox_option, this, false) as CheckBox
        addView(mTextCheckBox)
    }

    private fun dpToPx(dp: Int) = com.bamilo.android.appmodule.modernbamilo.util.dpToPx(context, dp.toFloat())

    fun setText(text: String) {
        mTextCheckBox.text = text
    }

    fun isOptionSelected() = mTextCheckBox.isChecked

    fun select() {
        mTextCheckBox.isChecked = true
    }

    fun deselect() {
        mTextCheckBox.isChecked = false
    }

}

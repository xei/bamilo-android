package com.bamilo.android.appmodule.modernbamilo.userreview.optionview

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.getInterpolatedColors

class RadioOptionView : RelativeLayout {

    private lateinit var mTextTextView: TextView

    private var mIsOptionSelected = false

    companion object {
        fun getRainbow(n: Int): IntArray {
            return getInterpolatedColors(Color.RED, Color.GREEN, n)
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, width: Int, height: Int, marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int, @ColorInt color: Int) :super(context) {
        val linearLayoutParams = LinearLayout.LayoutParams(dpToPx(width), dpToPx(height))
        linearLayoutParams.setMargins(dpToPx(marginLeft), dpToPx(marginTop), dpToPx(marginRight), dpToPx(marginBottom))
        layoutParams = linearLayoutParams
        requestLayout()

        init(color)
    }

    private fun init(@ColorInt color: Int = Color.GRAY) {
        setBackgroundColor(color)
        mTextTextView = LayoutInflater.from(context).inflate(R.layout.layout_review_radio_option, this, false) as TextView
        mTextTextView.setTextColor(ContextCompat.getColor(context, R.color.userReview_typeRadio_optionButton_textColor))
        addView(mTextTextView)
    }

    private fun dpToPx(dp: Int) = com.bamilo.android.appmodule.modernbamilo.util.dpToPx(context, dp.toFloat())

    fun setText(text: String) {
        mTextTextView.text = text
    }

    fun isOptionSelected() = mIsOptionSelected

    fun select() {
        mIsOptionSelected = true
        mTextTextView.alpha = 0.8f
    }

    fun deselect() {
        mIsOptionSelected = false
        mTextTextView.alpha = 1f
    }

}

package com.bamilo.modernbamilo.customview

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import com.bamilo.modernbamilo.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class PriceView: XeiTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PriceView)

        try {
            val needStrikeLine = typedArray.getBoolean(R.styleable.PriceView_strike, false)

            if (needStrikeLine) {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } catch (e: Exception) {

        } finally {
            typedArray.recycle()
        }

    }

    override fun setText(text: CharSequence?, type: BufferType?) =
            if(text == null || text.isEmpty()) {
                super.setText(text, type)
            } else {
                super.setText(resources.getString(R.string.suffix_currency, format(text!!.trim())), type)
            }


    private fun format(price: CharSequence): String {
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = ','
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3

        return df.format(java.lang.Long.parseLong(price.toString()))
    }

}
package com.bamilo.modernbamilo.customview

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger
import org.jetbrains.annotations.NotNull
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private const val TAG_DEBUG = "PriceView"

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
            Logger.log(message = e.message.toString(), tag = TAG_DEBUG, logType = LogType.ERROR)
        } finally {
            typedArray.recycle()
        }

    }

    fun setPrice(text: String, currencyStr: String) =
            setText(resources.getString(R.string.priceAndCurrency, format(text.trim()), currencyStr.trim()))

    private fun format(price: CharSequence): String {
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = ','
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3

        return df.format(java.lang.Long.parseLong(price.toString()))
    }

}
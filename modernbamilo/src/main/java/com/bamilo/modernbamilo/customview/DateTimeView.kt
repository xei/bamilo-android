package com.bamilo.modernbamilo.customview

import android.content.Context
import android.util.AttributeSet
import com.ibm.icu.text.DateFormat
import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale

const val ID_LOCALE = "fa_IR@calendar=persian"
const val STYLE_DATE_FORMAT = DateFormat.RELATIVE

class DateTimeView: XeiTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setTime(timeEpochInMillis: Long?) {
        if(timeEpochInMillis != null) {
            val locale = ULocale(ID_LOCALE)
            val calendar = Calendar.getInstance(locale)
            calendar.timeInMillis = timeEpochInMillis
            val dateFormat = DateFormat.getDateInstance(STYLE_DATE_FORMAT, locale)
            text = dateFormat.format(calendar)
        } else {
            text = "Invalid Time"
        }

    }

}
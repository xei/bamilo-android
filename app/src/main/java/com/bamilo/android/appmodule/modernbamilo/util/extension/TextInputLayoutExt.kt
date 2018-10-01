package com.bamilo.android.appmodule.modernbamilo.util.extension

import android.graphics.Typeface
import android.support.design.widget.TextInputLayout
import android.text.method.ScrollingMovementMethod
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * This method sets a typeface to the error section of a TextInputLayout.
 * The method must be invoked once when the layout is inflating not when an error is setting.
 *
 * @param typeface
 */
fun TextInputLayout.setErrorTypeface(typeface: Typeface) {
    val linearLayout = getChildAt(1) as LinearLayout
    ((linearLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextView).typeface = typeface
    ((linearLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextView).maxLines = 2
    ((linearLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextView).movementMethod = ScrollingMovementMethod()
}

/**
 * This method makes the error section of a TextInputLayout scrollable.
 * Errors that come from web server, can be too long and may affect the layout. since we limit
 * the maximum number of lines.
 *
 * @param maxLines
 */
fun TextInputLayout.makeErrorViewScrollable(maxLines: Int) {
    val linearLayout = getChildAt(1) as LinearLayout
    ((linearLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextView).maxLines = maxLines
    ((linearLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextView).movementMethod = ScrollingMovementMethod()
}
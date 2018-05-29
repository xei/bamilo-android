package com.bamilo.modernbamilo.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

private const val TAG_DEBUG = "HelperFunctions"

/**
 * This helper function converts dp unit to the corresponding pixel value.
 */
fun dpToPx(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return Math.round(dp * scale)
}

fun showRtlSnackbar(parent: View, alertText: String, actionText: String, actionColor: Int, actionClickListener: View.OnClickListener) {
    val snackbar = Snackbar.make(parent, alertText, Snackbar.LENGTH_INDEFINITE)
    val snackbarView = snackbar.view
    //         snackbarView.setBackgroundColor(Color.parseColor("#f5363f"));
    val alertTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
    val actionTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_action) as TextView
    //        alertTextView.setTypeface(TypeFaceHelper.getTypeFace(TypeFaceHelper.IRAN_SANS));
    //        actionTextView.setTypeface(TypeFaceHelper.getTypeFace(TypeFaceHelper.IRAN_SANS));
    (snackbarView as LinearLayout).removeView(alertTextView)
    snackbarView.addView(alertTextView)
    alertTextView.setTextColor(Color.WHITE)
    snackbar.setActionTextColor(ContextCompat.getColor(parent.context, actionColor))
    snackbar.setAction(actionText, actionClickListener)
    //		   snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
    snackbar.show()
}

fun getAppVersionCode(context: Context): Int {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e(TAG_DEBUG, "Package name not found", e)
        return 0
    }

}

fun getAppVersionName(context: Context): String? {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e(TAG_DEBUG, "Package name not found", e)
        return null
    }

}

fun formatPrice(price: String): String {
    val symbols = DecimalFormatSymbols()
    symbols.groupingSeparator = ','
    val df = DecimalFormat()
    df.decimalFormatSymbols = symbols
    df.groupingSize = 3

    return df.format(java.lang.Long.parseLong(price))
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
}

/**
 * This method converts a number string to persian equivalent.
 *
 * @param numberStr The number string that must convert to Persian.
 * @return Persian number string
 * @throws NumberFormatException occurs when the "numberStr" is invalid.
 */
@Throws(NumberFormatException::class)
fun convertNumberStringToPersian(numberStr: String): String {
    var persianNumberStr = ""
    for (i in 0 until numberStr.length) {
        val numberUnicode = numberStr[i].toInt()
        val persianUnicode: Int
        if (numberUnicode in 48..57) {
            // The digit character is Latin
            persianUnicode = numberUnicode + 1728
        } else if (numberUnicode in 1632..1641) {
            // The digit is Arabic
            persianUnicode = numberUnicode + 144
        } else if (numberUnicode in 1776..1785) {
            // The digit character is Persian
            persianUnicode = numberUnicode
        } else {
            // The digit character is invalid
            throw NumberFormatException("\"numberStr\" has an invalid digit character")
        }
        persianNumberStr += persianUnicode.toChar()
    }
    return persianNumberStr
}

/**
 * This method gets a string and converts all digits in it to Persian equivalent.
 *
 * @param str the string that may have some digit characters.
 * @return the processed string that don't have any non-persian digit character.
 */
fun persianizeDigitsInString(str: String): String {
    var persianizedStr = ""
    for (i in 0 until str.length) {
        val unicode = str[i].toInt()
        val persianizedUnicode: Int
        if (unicode in 48..57) {
            // The character is Latin digit
            persianizedUnicode = unicode + 1728
        } else if (unicode in 1632..1641) {
            // The character is Arabic digit
            persianizedUnicode = unicode + 144
        } else {
            persianizedUnicode = unicode
        }
        persianizedStr += persianizedUnicode.toChar()
    }
    return persianizedStr
}

fun quantize() {

}

/**
 * This helper function interpolate from a color to another one and create n relevant color.
 *
 */
fun getInterpolatedColors(n: Int) : IntArray{
    val t = 0.2
    var firstColor = Color.RED
    val secondColor = Color.GREEN

    val colors = IntArray(n + 2)
    colors[0] = firstColor
    for (i in 1..n) {
        val newColor = Color.argb(
                (Color.alpha(firstColor) + (Color.alpha(secondColor) - Color.alpha(firstColor)) * t).toInt(),
                (Color.red(firstColor) + (Color.red(secondColor) - Color.red(firstColor)) * t).toInt(),
                (Color.green(firstColor) + (Color.green(secondColor) - Color.green(firstColor)) * t).toInt(),
                (Color.blue(firstColor) + (Color.blue(secondColor) - Color.blue(firstColor)) * t).toInt())
        colors[i] = newColor
        firstColor = newColor
    }

    return colors
}

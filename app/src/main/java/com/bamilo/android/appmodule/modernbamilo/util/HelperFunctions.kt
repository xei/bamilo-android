package com.bamilo.android.appmodule.modernbamilo.util

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


@SuppressLint("SimpleDateFormat")
fun getCurrentDateTime(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
}

/**
 * This helper function interpolate from a color to another one and create n relevant color.
 *
 */
fun getInterpolatedColors(startColor: Int, endColor: Int, n: Int) : IntArray{
    val t = 0.2

    val colors = IntArray(n + 2)
    colors[0] = startColor
    for (i in 1..n) {
        colors[i] = Color.argb(
                (Color.alpha(colors[i - 1]) + (Color.alpha(endColor) - Color.alpha(colors[i - 1])) * t).toInt(),
                (Color.red(colors[i - 1]) + (Color.red(endColor) - Color.red(colors[i - 1])) * t).toInt(),
                (Color.green(colors[i - 1]) + (Color.green(endColor) - Color.green(colors[i - 1])) * t).toInt(),
                (Color.blue(colors[i - 1]) + (Color.blue(endColor) - Color.blue(colors[i - 1])) * t).toInt())
    }

    return colors
}

/**
 * This helper function convert . to / also
 * also remove .0 from end of float numbers
 * */
public fun getMorphNumberString(value: Float): String {
    @SuppressLint("DefaultLocale")
    var result = String.format("%.1f", value)

    if (result.contains("٫")) {
        result = result.replace("٫", "/")
    }

    if (result.contains("/0") || result.contains("/۰")) {
        result = result.replace("/0", "")
        result = result.replace("/۰", "")
    }

    return result
}

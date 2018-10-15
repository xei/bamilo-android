package com.bamilo.android.appmodule.modernbamilo.util.customtoast

import android.graphics.Color
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.view.Gravity
import android.widget.Toast

/**
 * Created by FarshidAbz.
 * Since 2/21/2017.
 */

internal class PoiziToastOptionModel {
    @ColorInt
    private val defaultTextColor = Color.parseColor("#FFFFFF")

    @ColorInt
    private val ERROR_COLOR = Color.parseColor("#e73d3d")

    @ColorInt
    private val INFO_COLOR = Color.parseColor("#0076FF")

    @ColorInt
    private val SUCCESS_COLOR = Color.parseColor("#37BC9B")

    @ColorInt
    private val WARNING_COLOR = Color.parseColor("#E08A1E")

    var gravity: Int = 0
        get() = if (field != 0) field else Gravity.BOTTOM
    var backgroundColor: Int = 0
        get() = if (field != 0)
            field
        else {
            when (toastKind) {
                ToastKind.NORMAL_TOAST -> 0
                ToastKind.INFO_TOAST -> INFO_COLOR
                ToastKind.ERROR_TOAST -> ERROR_COLOR
                ToastKind.SUCCESS_TOAST -> SUCCESS_COLOR
                ToastKind.WARNING_TOAST -> WARNING_COLOR
                else -> 0
            }
        }
    var icon: Int = 0
    var textColor: Int = 0
        get() = if (field != 0)
            field
        else
            defaultTextColor
    var duration: Int = 0
        @Duration
        get() = if (field != 0) field else Toast.LENGTH_SHORT

    var textTypeFace: Typeface? = null
        get() = if (field != null) field else null
    var message: String? = null
    var textSize: Int = 0
        get() = if (field != 0) field else 14

    var toastKind: Int = 0
}

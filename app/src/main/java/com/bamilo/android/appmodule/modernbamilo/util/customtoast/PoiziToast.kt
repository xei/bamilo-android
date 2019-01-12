package com.bamilo.android.appmodule.modernbamilo.util.customtoast

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes

/**
 * Created by FarshidAbz.
 * Since 2/21/2017.
 */

class PoiziToast private constructor(context: Context) {
    private val exhibitioner: Exhibitioner

    private val poiziToastOptionModel: PoiziToastOptionModel

    init {
        if (poiziToast == null) {
            poiziToast = this
        }

        PoiziToast.context = context

        poiziToastOptionModel = PoiziToastOptionModel()
        exhibitioner = Exhibitioner(context, poiziToastOptionModel)
    }

    fun normal(message: String, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        poiziToastOptionModel.toastKind = ToastKind.NORMAL_TOAST
        return exhibitioner
    }

    fun info(message: String, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        poiziToastOptionModel.toastKind = ToastKind.INFO_TOAST
        return exhibitioner
    }

    fun error(message: String?, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        poiziToastOptionModel.toastKind = ToastKind.ERROR_TOAST
        return exhibitioner
    }

    fun success(message: String, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        poiziToastOptionModel.toastKind = ToastKind.SUCCESS_TOAST
        return exhibitioner
    }

    fun warning(message: String, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        poiziToastOptionModel.toastKind = ToastKind.WARNING_TOAST
        return exhibitioner
    }

    fun makeToast(message: String, @Duration duration: Int): Exhibitioner {
        poiziToastOptionModel.message = message
        poiziToastOptionModel.duration = duration
        return exhibitioner
    }

    fun setTextTypeFace(textTypeFace: Typeface): PoiziToast? {
        poiziToastOptionModel.textTypeFace = textTypeFace
        return poiziToast
    }

    fun setBackgroundColor(@ColorInt backgroundColor: Int): PoiziToast? {
        poiziToastOptionModel.backgroundColor = backgroundColor
        return poiziToast
    }

    fun setGravity(@Gravity gravity: Int): PoiziToast? {
        poiziToastOptionModel.gravity
        return poiziToast
    }

    fun setTextSize(textSize: Int): PoiziToast? {
        poiziToastOptionModel.textSize = textSize
        return poiziToast
    }

    fun setTextColor(@ColorRes textColor: Int): PoiziToast? {
        poiziToastOptionModel.textColor = textColor
        return poiziToast
    }

    fun setIcon(@DrawableRes icon: Int): PoiziToast? {
        poiziToastOptionModel.icon = icon
        return poiziToast
    }

    companion object {
        private var context: Context? = null
        private var poiziToast: PoiziToast? = null

        fun with(context: Context): PoiziToast? {
            poiziToast = null
            PoiziToast(context)
            return poiziToast
        }
    }
}

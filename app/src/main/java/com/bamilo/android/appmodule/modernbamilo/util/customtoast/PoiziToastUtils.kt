package com.bamilo.android.appmodule.modernbamilo.util.customtoast

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import com.bamilo.android.R

/**
 * Created by FarshidAbz.
 * Since 2/21/2017.
 */

internal object PoiziToastUtils {

    fun tint9PatchDrawableFrame(context: Context, @ColorInt tintColor: Int): Drawable {
        val toastDrawable = getDrawable(context, R.drawable.toast_frame) as NinePatchDrawable?
        toastDrawable!!.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.DST_IN)
        return toastDrawable
    }

    fun setBackground(view: View, drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.background = drawable
        else
            view.setBackgroundDrawable(drawable)
    }

    fun getDrawable(context: Context, @DrawableRes id: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            context.getDrawable(id)
        else
            context.resources.getDrawable(id)
    }
}

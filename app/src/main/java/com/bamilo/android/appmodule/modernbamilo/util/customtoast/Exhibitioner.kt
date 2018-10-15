package com.bamilo.android.appmodule.modernbamilo.util.customtoast

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bamilo.android.R

/**
 * Created by FarshidAbz.
 * Since 2/21/2017.
 */

class Exhibitioner internal constructor(private val context: Context, private val poiziToastOptionModel: PoiziToastOptionModel) {

    fun show() {
        val toast = Toast(context)
        val toastLayout = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.poizi_toast_layout, null)

        val imgToastIcon = toastLayout.findViewById(R.id.imgToastIcon) as ImageView
        val tvToastText = toastLayout.findViewById(R.id.tvToastText) as TextView
        val llPoiziToastRoot = toastLayout.findViewById(R.id.llPoiziToastRoot) as LinearLayout

        initLlPoiziToastRoot(llPoiziToastRoot)
        initToastText(tvToastText)
        initToastIcon(imgToastIcon)

        toast.view = toastLayout

        toast.duration = poiziToastOptionModel.duration

        setGravity(toast)

        toast.show()
    }

    private fun setGravity(toast: Toast) {
        val displayMetrics = context.resources.displayMetrics
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP,
                0, 0)
    }

    private fun initLlPoiziToastRoot(llPoiziToastRoot: LinearLayout) {
        val drawableFrame: Drawable?
        if (poiziToastOptionModel.backgroundColor == 0) {
            drawableFrame = PoiziToastUtils.getDrawable(context, R.drawable.toast_frame)
        } else {
            drawableFrame = PoiziToastUtils
                    .tint9PatchDrawableFrame(context, poiziToastOptionModel.backgroundColor)
        }

        PoiziToastUtils.setBackground(llPoiziToastRoot, drawableFrame!!)
    }

    private fun initToastIcon(imgToastIcon: ImageView) {
        if (poiziToastOptionModel.icon <= 0) {
            imgToastIcon.visibility = View.GONE
        } else {
            imgToastIcon.setImageResource(poiziToastOptionModel.icon)
        }
    }

    private fun initToastText(tvToastText: TextView) {
        tvToastText.text = poiziToastOptionModel.message
        tvToastText.setTextColor(poiziToastOptionModel.textColor)
        tvToastText.typeface = poiziToastOptionModel.textTypeFace
        tvToastText.setTextSize(TypedValue.COMPLEX_UNIT_SP, poiziToastOptionModel.textSize.toFloat())
    }
}

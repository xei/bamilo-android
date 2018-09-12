package com.bamilo.android.appmodule.modernbamilo.update

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.openStorePage

/**
 * Created by Farshid
 * since 9/12/2018.
 * contact farshidabazari@gmail.com
 */
class OptionalUpdateBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mTitle: String
    private lateinit var mDescription: String
    private lateinit var mLatestApkUrl: String
    private lateinit var mOnDialogDismissListener: OnDialogDismissListener

    fun setUpdateInfo(title: String, description: String, latestApkUrl: String): OptionalUpdateBottomSheet {
        mTitle = title
        mDescription = description
        mLatestApkUrl = latestApkUrl

        return this
    }

    fun setDismissListener(onDialogDismissListener: OnDialogDismissListener): OptionalUpdateBottomSheet {
        this.mOnDialogDismissListener = onDialogDismissListener

        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.bottomsheet_update, container, false).apply {

            findViewById<TextView>(R.id.bottomsheetUpdate_xeiTextView_title).text = mTitle
            findViewById<TextView>(R.id.bottomsheetUpdate_xeiTextView_description).text = mDescription

            findViewById<Button>(R.id.bottomsheetUpdate_xeiButton_update).setOnClickListener {
                openStorePage(context, mLatestApkUrl)
            }

            findViewById<ImageView>(R.id.bottomsheetUpdate_imageView_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.bottomsheetUpdate_xeiTextView_cancelFlatButton).setOnClickListener {
                dismiss()
            }
        }

    }

}
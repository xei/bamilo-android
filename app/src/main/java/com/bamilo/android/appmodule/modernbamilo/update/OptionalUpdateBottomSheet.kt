package com.bamilo.android.appmodule.modernbamilo.update

import android.content.DialogInterface
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
import com.bamilo.android.framework.service.utils.TextUtils

/**
 * Created by Farshid
 * since 9/12/2018.
 * contact farshidabazari@gmail.com
 */
class OptionalUpdateBottomSheet : BottomSheetDialogFragment() {

    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mLatestApkUrl: String? = null
    private lateinit var mOnDialogDismissListener: OnDialogDismissListener

    fun setUpdateInfo(title: String?, description: String?, latestApkUrl: String?): OptionalUpdateBottomSheet {
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

            if (!TextUtils.isEmpty(mTitle)) {
                findViewById<TextView>(R.id.bottomsheetUpdate_xeiTextView_title).text = mTitle
            }
            if (!TextUtils.isEmpty(mDescription)) {
                findViewById<TextView>(R.id.bottomsheetUpdate_xeiTextView_description).text = mDescription
            }

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

    override fun dismiss() {
        mOnDialogDismissListener.onDismiss()
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface?) {
        mOnDialogDismissListener.onDismiss()
        super.onCancel(dialog)
    }
}
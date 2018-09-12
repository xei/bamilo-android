package com.bamilo.android.appmodule.modernbamilo.update

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.android.R
import com.bamilo.android.databinding.DialogForceUpdateBinding

/**
 * Created by Farshid
 * since 9/12/2018.
 * contact farshidabazari@gmail.com
 */
class ForceUpdateBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DialogForceUpdateBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_force_update, container, false)

        setTitleAndMessageText()
        setupDialogAccordingToUpdateStatus()
        bindButtonsClickListener()

        return binding.root
    }

    private fun setTitleAndMessageText() {
//        if (!TextUtils.isEmpty(title)) {
//            binding.updateDialogTextViewTitle.text = title
//        }
//
//        if (!TextUtils.isEmpty(message)) {
//            binding.updateDialogTextViewMessage.text = message
//        }
    }

    private fun setupDialogAccordingToUpdateStatus() {
//        if (updateStatus == STATE_OPTIONAL_UPDATE) {
//            binding.updateDialogButtonCancel.visibility = View.VISIBLE
//            setCanceledOnTouchOutside(true)
//            setCancelable(true)
//        } else if (updateStatus == STATE_FORCED_UPDATE) {
//            binding.updateDialogButtonCancel.visibility = View.GONE
//            setCanceledOnTouchOutside(false)
//            setCancelable(false)
//        }
    }

    private fun bindButtonsClickListener() {
//        binding.updateDialogButtonCancel.setOnClickListener { dismiss() }
//        binding.updateDialogButtonUpdate.setOnClickListener { gotoStore() }
    }

    private fun gotoStore() {
//        openStorePage(context, storeUrl)
    }
}
package com.bamilo.android.appmodule.modernbamilo.update

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.openStorePage
import com.bamilo.android.databinding.BottomsheetForceUpdateBinding

/**
 * Created by Farshid
 * since 9/12/2018.
 * contact farshidabazari@gmail.com
 */
class ForceUpdateBottomSheet : Fragment() {

    private lateinit var binding: BottomsheetForceUpdateBinding

    private var title: String? = ""
    private var message: String? = ""
    private var storeUrl: String? = ""

    var rootView: View? = null

    companion object {
        fun newInstance(title: String, message: String, storeUrl: String): ForceUpdateBottomSheet {
            val forceUpdateBottomSheet = ForceUpdateBottomSheet()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putString("message", message)
            bundle.putString("storeUrl", storeUrl)

            forceUpdateBottomSheet.arguments = bundle
            return forceUpdateBottomSheet
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_force_update, container, false)

        title = arguments?.getString("title", "")
        message = arguments?.getString("message", "")
        storeUrl = arguments?.getString("storeUrl", "")

        setTitleAndMessageText()
        bindButtonsClickListener()

        return binding.root
    }

    private fun setTitleAndMessageText() {
        if (!TextUtils.isEmpty(title)) {
            binding.updateDialogTextViewTitle.text = title
        }

        if (!TextUtils.isEmpty(message)) {
            binding.updateDialogTextViewMessage.text = message
        }
    }

    private fun bindButtonsClickListener() {
        binding.updateDialogTextViewCancel.setOnClickListener { activity?.finish() }
        binding.updateDialogButtonUpdate.setOnClickListener { gotoStore() }
    }

    private fun gotoStore() {
        openStorePage(context, storeUrl!!)
    }
}
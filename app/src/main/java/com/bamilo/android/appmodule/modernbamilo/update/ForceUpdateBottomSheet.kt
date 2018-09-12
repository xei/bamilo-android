package com.bamilo.android.appmodule.modernbamilo.update

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
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
class ForceUpdateBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetForceUpdateBinding

    private var title: String? = ""
    private var message: String? = ""
    private var storeUrl: String? = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_force_update, container, false)

        title = arguments?.getString("title", "")
        message = arguments?.getString("message", "")
        storeUrl = arguments?.getString("storeUrl", "")

        setTitleAndMessageText()
        bindButtonsClickListener()

        disableDragging()

        return binding.root
    }

    private fun disableDragging() {
        try {
            val mBehaviorField = bottomSheetDialog.javaClass.getDeclaredField("mBehavior")
            mBehaviorField.isAccessible = true
            val behavior = mBehaviorField.get(bottomSheetDialog) as BottomSheetBehavior<*>
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog

        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val view = view
        view!!.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view.measuredHeight

            parent.setBackgroundColor(Color.WHITE)
        }
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
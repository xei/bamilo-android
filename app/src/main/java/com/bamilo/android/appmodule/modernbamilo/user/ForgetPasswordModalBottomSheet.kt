package com.bamilo.android.appmodule.modernbamilo.user

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.util.extension.makeErrorViewScrollable
import com.bamilo.android.appmodule.modernbamilo.util.extension.setErrorTypeface
import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper

private const val INITIAL_HEIGHT = 1070

open class ForgetPasswordModalBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var mCloseButton: ImageView
    private lateinit var mUserIdTextInputLayout: TextInputLayout
    private lateinit var mUserIdEditText: EditText

    private lateinit var mRegisterButton: BamiloActionButton
    private lateinit var mGoToLoginScreenButton: Button
    private var mBottomSheetFrameLayout: FrameLayout? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            mBottomSheetFrameLayout = d.findViewById<View>(android.support.design.R.id.design_bottom_sheet) as FrameLayout?
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_dialog_bottomsheet_forgetpass, container, false)

        findViews(view)
        initTextInputLayouts()
        setOnClickListeners()
        setOnFocusExpand()
        setInitialHeight(view, INITIAL_HEIGHT)

        return view
    }

    private fun findViews(view: View) {
        mCloseButton = view.findViewById(R.id.fragmentDialogBottomsheetForgetPass_imageButton_close)
        mUserIdTextInputLayout = view.findViewById(R.id.fragmentDialogBottomsheetForgetPass_textInputLayout_userId)
        mUserIdEditText = view.findViewById(R.id.fragmentDialogBottomsheetForgetPass_xeiEditText_userId)

        mRegisterButton = view.findViewById(R.id.fragmentDialogBottomsheetForgetPass_bamiloActionButton_register)
        mGoToLoginScreenButton = view.findViewById(R.id.fragmentDialogBottomsheetForgetPass_xeiButton_goToLogin)
    }

    private fun initTextInputLayouts() {
        val errorMsgTypeface = TypeFaceHelper.getInstance(context).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR)

        mUserIdTextInputLayout.setErrorTypeface(errorMsgTypeface)
        mUserIdTextInputLayout.makeErrorViewScrollable(2)
    }

    private fun setOnClickListeners() {
        mCloseButton.setOnClickListener(this)
        mRegisterButton.setOnClickListener(this)
        mGoToLoginScreenButton.setOnClickListener(this)
    }

    private fun setOnFocusExpand() {
        mUserIdEditText.clearFocus()
        mRegisterButton.requestFocus()
        mUserIdEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) expandBottomSheet() }
    }

    private fun setInitialHeight(view: View, height: Int) = view.post { setPeekHeight(height) }

    private fun setPeekHeight(peekHeight: Int) {
        try {
            BottomSheetBehavior.from(mBottomSheetFrameLayout!!).peekHeight = peekHeight
        } catch (ignored: Exception) {}
    }

    private fun expandBottomSheet() {
        try {
            BottomSheetBehavior.from(mBottomSheetFrameLayout!!).state = BottomSheetBehavior.STATE_EXPANDED
        } catch (ignored: Exception) {}
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.fragmentDialogBottomsheetForgetPass_imageButton_close -> dismiss()
            R.id.fragmentDialogBottomsheetForgetPass_bamiloActionButton_register -> onRecoverButtonClicked()
            R.id.fragmentDialogBottomsheetForgetPass_xeiButton_goToLogin -> openRegisterScreen()
        }
    }

    private fun onRecoverButtonClicked() {
        // TODO
    }

    private fun openRegisterScreen() {
        RegisterModalBottomSheet().show(fragmentManager, "register")
        dismiss()
    }

    /******************************************* Legacy Code ***************************************/



    /******************************************* Legacy Code ***************************************/

}
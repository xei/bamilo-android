package com.bamilo.android.appmodule.modernbamilo.authentication.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.TextInputLayout
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.ProductDetailActivity
import com.bamilo.android.appmodule.modernbamilo.authentication.AuthenticationListener
import com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword.ForgetPasswordBottomSheet
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.AuthenticationRepo
import com.bamilo.android.appmodule.modernbamilo.customview.XeiEditText
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents
import com.bamilo.android.appmodule.modernbamilo.user.RegisterModalBottomSheet
import com.bamilo.android.appmodule.modernbamilo.util.customtoast.PoiziToast
import com.bamilo.android.appmodule.modernbamilo.util.dpToPx
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin
import com.bamilo.android.framework.service.pojo.BaseResponse
import com.bamilo.android.framework.service.utils.Constants
import com.bamilo.android.framework.service.utils.CustomerUtils
import com.crashlytics.android.Crashlytics

/**
 * Created by Farshid
 * since 9/26/2018.
 * contact farshidabazari@gmail.com
 */
class LoginDialogBottomSheet : BottomSheetDialogFragment() {

    private lateinit var emailOrPhoneEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var emailOrPhoneTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout

    private lateinit var rootView: ConstraintLayout

    private var authenticationListener: AuthenticationListener? = null

    private var mBottomSheetFrameLayout: FrameLayout? = null

    private lateinit var focus:XeiEditText

    var height = 0

    interface OnLoginListener { fun onLoggedIn() }
    private var mOnLoginListener: OnLoginListener? = null

    companion object {
        @JvmStatic
        fun show(fragmentManager: FragmentManager, bundle: Bundle?, loginListener: OnLoginListener?): LoginDialogBottomSheet {
            val loginDialogBottomSheet = LoginDialogBottomSheet()
            loginDialogBottomSheet.arguments = bundle
            loginDialogBottomSheet.show(fragmentManager, "login")

            loginDialogBottomSheet.mOnLoginListener = loginListener

            return loginDialogBottomSheet
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { d_ ->
            val d = d_ as BottomSheetDialog
            mBottomSheetFrameLayout = d.findViewById<View>(android.support.design.R.id.design_bottom_sheet) as FrameLayout?
        }

        return dialog
    }

    fun setAuthenticationListener(authenticationListener: AuthenticationListener) {
        this.authenticationListener = authenticationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.bottomsheet_login, container, false).apply {
            findViews(this)
            setOnFocusExpand()
            bindViewsClickListener(this)

            passwordEditText.transformationMethod = PasswordTransformationMethod()
        }
    }

    private fun setInitialHeight(view: View) {
        rootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.post {
            context?.let {
                setPeekHeight(dpToPx(it, 400f))
            }
        }
    }

    private fun setPeekHeight(peekHeight: Int) {
        try {
            BottomSheetBehavior.from(mBottomSheetFrameLayout!!).peekHeight = peekHeight
        } catch (ignored: Exception) {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnFocusExpand() {
        emailOrPhoneEditText.clearFocus()
        passwordEditText.clearFocus()
        focus.requestFocus()
        emailOrPhoneEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) expandBottomSheet() }

//        passwordEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) expandBottomSheet() }
    }

    private fun expandBottomSheet() {
        mBottomSheetFrameLayout?.let {
            BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun findViews(view: View?) {
        view?.let {
            emailOrPhoneEditText = it.findViewById(R.id.loginBottomSheet_editText_emailOrPhone)
            passwordEditText = it.findViewById(R.id.loginBottomSheet_editText_password)

            emailOrPhoneTextInputLayout = it.findViewById(R.id.loginBottomSheet_til_emailOrPhone)
            passwordTextInputLayout = it.findViewById(R.id.loginBottomSheet_til_password)

            focus = it.findViewById(R.id.focus)

            rootView = it.findViewById(R.id.loginBottomSheet_relativeLayout_root)
            setInitialHeight(it)
        }
    }

    private fun bindViewsClickListener(view: View?) {
        view?.let { it ->
            it.findViewById<View>(R.id.loginBottomSheet_button_signUp).setOnClickListener { onSignUpClicked() }
            it.findViewById<View>(R.id.loginBottomSheet_bamiloButton_login).setOnClickListener { onLoginClicked() }
            it.findViewById<View>(R.id.loginBottomSheet_textView_forgetPassword).setOnClickListener { onForgetPasswordClicked() }
            it.findViewById<View>(R.id.loginBottomSheet_appImageView_close).setOnClickListener { onCloseClicked() }
        }
    }

    private fun onLoginClicked() {
        if (!checkEmailOrPhoneValidation() || !checkPasswordValidation()) {
            return
        }

        showProgress()
        login()
    }

    private fun showProgress() {
        activity?.let {
            if (it is BaseActivity) {
                it.showProgress()
            } else if (it is ProductDetailActivity) {
                it.showProgressView()
            }
        }
    }

    private fun hideProgress() {
        activity?.let {
            if (it is BaseActivity) {
                it.dismissProgress()
            } else if (it is ProductDetailActivity) {
                it.dismissProgressView()
            }
        }
    }

    private fun login() {
        AuthenticationRepo.login(context,
                emailOrPhoneEditText.text.toString().trim(),
                passwordEditText.text.toString().trim(),
                object : IResponseCallback {
                    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                        hideProgress()
                        baseResponse?.let {
                            if (baseResponse.hadSuccess()) {
                                onLoginSuccessful(it)
                            } else {
                                context?.let { it2 ->
                                    PoiziToast.with(it2)
                                            ?.setGravity(Gravity.TOP)
                                            ?.error(getString(R.string.email_password_invalid), Toast.LENGTH_SHORT)
                                            ?.show()
                                }
                                authenticationListener?.onAuthenticationListener(false)
                            }
                        }
                    }

                    override fun onRequestError(baseResponse: BaseResponse<*>?) {
                        hideProgress()
                        authenticationListener?.onAuthenticationListener(false)
                        baseResponse?.validateMessages?.run {
                            this["identifier"]?.let { emailOrPhoneTextInputLayout.error = it }
                            this["password"]?.let { passwordTextInputLayout.error = it }
                        }
                    }
                })
    }

    private fun onLoginSuccessful(baseResponse: BaseResponse<*>) {
        var isInCheckoutProcess = false

        arguments?.let {
            isInCheckoutProcess = it.getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API)
        }

        val nextStepStruct = baseResponse.contentData as NextStepStruct
        val customer = (nextStepStruct.checkoutStepObject as CheckoutStepLogin).customer

        activity?.let {
            CustomerUtils.setChangePasswordVisibility(it, false)
        }

        TrackerDelegator.trackLoginSuccessful(customer, false, false)
        Crashlytics.setUserEmail(BamiloApplication.CUSTOMER.email)

        EventTracker.login(
                userId = BamiloApplication.CUSTOMER.id.toString(),
                emailAddress = BamiloApplication.CUSTOMER.email,
                phoneNumber = BamiloApplication.CUSTOMER.phoneNumber,
                loginMethod = TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL
        )

        activity?.let {
            if (it is BaseActivity) {
                it.setupDrawerNavigation()

                if (isInCheckoutProcess) {
                    it.onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, null, FragmentController.ADD_TO_BACK_STACK)
                }
            }
        }
        authenticationListener?.onAuthenticationListener(true)

        mOnLoginListener?.onLoggedIn()

        dismiss()
    }

    private fun checkEmailOrPhoneValidation(): Boolean {
        var idValue = emailOrPhoneEditText.text.toString().trim()
        if (TextUtils.isEmpty(idValue)) {
            emailOrPhoneTextInputLayout.error = getString(R.string.insert_email_or_phone_number)
        }

        if (isValidEmail(idValue)) {
            emailOrPhoneTextInputLayout.error = null
            return true
        }

        if (emailOrPhoneEditText.text.toString().trim().startsWith("09") && idValue.length == 11) {
            emailOrPhoneTextInputLayout.error = null
            return true
        }

        emailOrPhoneTextInputLayout.error = getString(R.string.invalid_email_or_phone_number)

        return false
    }

    private fun isValidEmail(email: String): Boolean {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    private fun checkPasswordValidation(): Boolean {
        if (passwordEditText.text.toString().isEmpty()) {
            passwordTextInputLayout.error = getString(R.string.insert_password)
            return false
        }

        if (passwordEditText.text.toString().length < 6) {
            passwordTextInputLayout.error = getString(R.string.invalid_password)
            return false
        }

        passwordTextInputLayout.error = null
        return true
    }

    private fun onSignUpClicked() {
        RegisterModalBottomSheet().show(fragmentManager, "register")
        dismiss()
    }

    private fun onForgetPasswordClicked() {
        ForgetPasswordBottomSheet().show(fragmentManager, "forgetPassword")
        dismiss()
    }

    private fun onCloseClicked() {
        authenticationListener?.onAuthenticationListener(false)
        dismiss()
    }
}
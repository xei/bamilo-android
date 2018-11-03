package com.bamilo.android.appmodule.modernbamilo.authentication.verification

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs
import com.bamilo.android.appmodule.bamiloapp.helpers.session.MobileVerificationHelper
import com.bamilo.android.appmodule.bamiloapp.helpers.session.RegisterHelper
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.ProductDetailActivity
import com.bamilo.android.appmodule.bamiloapp.view.widget.PinEntryInput
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.AuthenticationRepo
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.util.customtoast.PoiziToast
import com.bamilo.android.appmodule.modernbamilo.util.dpToPx
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.framework.service.pojo.BaseResponse
import com.bamilo.android.framework.service.utils.ApiConstants
import com.bamilo.android.framework.service.utils.Constants
import com.bamilo.android.framework.service.utils.CustomerUtils
import com.bamilo.android.framework.service.utils.EventType
import java.util.*

/**
 * Created by Farshid
 * since 10/17/2018.
 * contact farshidabazari@gmail.com
 */

class VerificationFragmentBottomSheet : BottomSheetDialogFragment(), IResponseCallback {
    enum class VerificationType {
        REGISTRATION,
        CHANGE_PHONE,
        FORGET_PASSWORD
    }

    private val MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY = "MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY"
    private val MOBILE_VERIFICATION_DONE = "MOBILE_VERIFICATION_DONE"
    private val MOBILE_VERIFICATION_FAILED = "MOBILE_VERIFICATION_FAILED"

    private var phoneNumber: String? = null
    private var tokenReceiveTime: Long = 0
    private var mTokenCountDownHandler: Handler? = null
    private var mTokenCountDownRunnable: Runnable? = null
    private val tokenResendDelay = (2 * 60 * 1000).toLong()

    private lateinit var verificationCodeSentToPhoneTextView: TextView
    private lateinit var editPhoneTextView: TextView
    private lateinit var tvResendToken: TextView
    private lateinit var tvResendTokenNotice: TextView

    private lateinit var etPin: PinEntryInput

    private lateinit var btnSubmitToken: BamiloActionButton

    private lateinit var rootView: View

    private var mBottomSheetFrameLayout: FrameLayout? = null

    companion object {
        @JvmStatic
        fun newInstance(verificationType: VerificationType,
                        identifier: String,
                        phoneNumber: String = "",
                        nationalCode: String = "",
                        email: String = "",
                        password: String = ""): VerificationFragmentBottomSheet {
            val verificationFragmentBottomSheet = VerificationFragmentBottomSheet()
            val bundle = Bundle()

            bundle.putString("verificationType", verificationType.name)
            bundle.putString("identifier", identifier)
            bundle.putString("phoneNumber", phoneNumber)
            bundle.putString("nationalCode", nationalCode)
            bundle.putString("email", email)
            bundle.putString("password", password)

            verificationFragmentBottomSheet.arguments = bundle

            return verificationFragmentBottomSheet
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_mobile_verification, container, false)
        findViews()
        bindClickListeners()
        phoneNumber = arguments?.getString("phoneNumber")
        verificationCodeSentToPhoneTextView.text =
                String.format(getString(R.string.verification_code_was_sent_to),
                        arguments?.getString("phoneNumber")).persianizeDigitsInString()

        startTimer()
        initPinView()

        setInitialHeight()

        sendVerificationCode(arguments?.getString("phoneNumber"), null)
        return rootView
    }

    private fun findViews() {
        verificationCodeSentToPhoneTextView = rootView.findViewById(R.id.verificationDialog_textView_verifyCodeSentTo)
        editPhoneTextView = rootView.findViewById(R.id.verificationDialog_textView_editPhone)
        tvResendToken = rootView.findViewById(R.id.tvResendToken)
        tvResendTokenNotice = rootView.findViewById(R.id.tvResendTokenNotice)
        etPin = rootView.findViewById(R.id.etPin)
        btnSubmitToken = rootView.findViewById(R.id.btnSubmitToken)
    }

    private fun bindClickListeners() {
        btnSubmitToken.setOnClickListener { submitToken() }
        tvResendToken.setOnClickListener { sendVerificationCode(arguments?.getString("phoneNumber"), null) }
        editPhoneTextView.setOnClickListener { }
    }

    private fun setInitialHeight() = rootView.post {
        context?.let {
            setPeekHeight(dpToPx(it, 400f))
        }
    }

    private fun setPeekHeight(peekHeight: Int) {
        try {
            BottomSheetBehavior.from(mBottomSheetFrameLayout!!).peekHeight = peekHeight
        } catch (ignored: Exception) {
        }
    }

    private fun submitToken() {
        val token = etPin.text!!.toString()
        if (token.length < etPin.maxLength) {
            etPin.requestFocus()
        } else {
            sendVerificationCode(phoneNumber, token)
        }
    }

    private fun initPinView() {
        etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                btnSubmitToken.isEnabled = charSequence.length == etPin.maxLength
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        etPin.setOnPinEnteredListener { str -> sendVerificationCode(phoneNumber, str.toString()) }
    }

    private fun startTimer() {
        mTokenCountDownHandler = Handler()
        mTokenCountDownRunnable = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                if (now - tokenReceiveTime > tokenResendDelay) {
                    tvResendToken.visibility = View.VISIBLE
                    tvResendTokenNotice.visibility = View.GONE
                } else {
                    val remaining = (tokenResendDelay - (now - tokenReceiveTime)) / 1000
                    tvResendTokenNotice.text = String.format(Locale("fa"), "%d:%02d", remaining / 60, remaining % 60)
                    mTokenCountDownHandler?.postDelayed(this, 500)
                }
            }
        }
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

    /**
     * A function to check and request for token!!
     * if token was null function request a new token if not it send token to server to verify phone number
     * */
    private fun sendVerificationCode(phoneNumber: String?, token: String?) {
        val values = ContentValues()
        values.put("phone", phoneNumber)
        if (token != null) {
            values.put("token", token)
        }

        showProgress()
        BamiloApplication.INSTANCE.sendRequest(MobileVerificationHelper(),
                MobileVerificationHelper.createBundle(values), this)
    }

    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
        baseResponse?.successMessages?.let {
            if (it.containsKey(MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY)) {
                hideProgress()
                tokenReceiveTime = System.currentTimeMillis()
                tvResendToken.visibility = View.GONE
                mTokenCountDownHandler?.post(mTokenCountDownRunnable)
            } else if (it.containsKey(MOBILE_VERIFICATION_DONE)) {
                val prefs = context!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, true)
                editor.putString(ConstantsSharedPrefs.KEY_PHONE_NUMBER, phoneNumber)
                editor.apply()

                arguments?.let { arg ->
                    triggerRegister(
                            ContentValues().apply {
                                put("customer[national_id]", arg.getString("nationalCode"))
                                put("customer[email]", arg.getString("email"))
                                put("customer[password]", arg.getString("password"))
                                put("customer[phone]", arg.getString("phoneNumber"))
                            })
                }
            }
            return
        }
    }

    override fun onRequestError(baseResponse: BaseResponse<*>?) {
        hideProgress()
        baseResponse?.let {
            if (!android.text.TextUtils.isEmpty(it.validateMessage)) {
                context?.let { it2 ->
                    PoiziToast.with(it2)
                            ?.setGravity(Gravity.TOP)
                            ?.error(it.validateMessage, Toast.LENGTH_SHORT)
                            ?.show()
                }
                return
            }

            it.errorMessages?.let { em ->
                if (em.containsKey(MOBILE_VERIFICATION_FAILED)) {
                    context?.let { ctx ->
                        PoiziToast.with(ctx)
                                ?.setGravity(Gravity.TOP)
                                ?.error(em.getValue(MOBILE_VERIFICATION_FAILED), Toast.LENGTH_SHORT)
                                ?.show()
                    }
                }
            }
        }
    }

    private fun triggerRegister(values: ContentValues) {
        showProgress()
        BamiloApplication.INSTANCE.sendRequest(RegisterHelper(),
                RegisterHelper.createBundle(ApiConstants.USER_REGISTRATION_API_PATH, values),
                object : IResponseCallback {
                    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                        hideProgress()
                        if (isOnStoppingProcess || activity == null) {
                            return
                        }
                        // Validate event
                        val eventType = baseResponse?.eventType
                        when (eventType) {

                            EventType.REGISTER_ACCOUNT_EVENT -> {
                                CustomerUtils.setChangePasswordVisibility(activity, false)
                                activity?.let {
                                    if (it is BaseActivity) {
                                        it.setupDrawerNavigation()
                                    }
                                }
                                dismiss()
//                                Timer().schedule(2000) {
//                                    login()
//                                }
                            }
                            else -> {
                                dismiss()
                            }
                        }
                    }

                    var isOnStoppingProcess = false

                    override fun onRequestError(baseResponse: BaseResponse<*>?) {
                        hideProgress()
                    }
                })
    }

    private fun login() {
        AuthenticationRepo.autoLogin(context,
                object : IResponseCallback {
                    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
                        hideProgress()
                        baseResponse?.let {
                            if (baseResponse.hadSuccess()) {
                                onLoginSuccessful()
                            }
                        }
                    }

                    override fun onRequestError(baseResponse: BaseResponse<*>?) {
                    }
                })
    }

    private fun onLoginSuccessful() {
        activity?.let {
            CustomerUtils.setChangePasswordVisibility(it, false)
        }

        activity?.let {
            if (it is BaseActivity) {
                it.setupDrawerNavigation()
            }
        }
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mTokenCountDownHandler = null
        mTokenCountDownRunnable = null
    }
}
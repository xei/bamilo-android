package com.bamilo.android.appmodule.modernbamilo.user

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.TextInputLayout
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants
import com.bamilo.android.appmodule.bamiloapp.controllers.LogOut
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper
import com.bamilo.android.appmodule.bamiloapp.helpers.session.RegisterHelper
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel
import com.bamilo.android.appmodule.bamiloapp.utils.MessagesUtils
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.util.extension.makeErrorViewScrollable
import com.bamilo.android.appmodule.modernbamilo.util.extension.setErrorTypeface
import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper
import com.bamilo.android.framework.service.pojo.BaseResponse
import com.bamilo.android.framework.service.rest.errors.ErrorCode
import com.bamilo.android.framework.service.utils.*
import java.util.regex.Pattern

private const val INITIAL_HEIGHT = 2064

open class RegisterModalBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var mCloseButton: ImageView
    private lateinit var mUserIdTextInputLayout: TextInputLayout
    private lateinit var mUserIdEditText: EditText
    private lateinit var mPasswordTextInputLayout: TextInputLayout
    private lateinit var mPasswordEditText: EditText

    // TODO: temp
    private lateinit var mMobileNoTextInputLayout: TextInputLayout
    private lateinit var mMobileNoEditText: EditText
    private lateinit var mNationalIdTextInputLayout: TextInputLayout
    private lateinit var mNationalIdEditText: EditText

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
        val view =  inflater.inflate(R.layout.fragment_dialog_bottomsheet_register, container, false)

        findViews(view)
        initTextInputLayouts()
        setOnClickListeners()
        setOnFocusExpand()
        setInitialHeight(view, INITIAL_HEIGHT)

        return view
    }

    private fun findViews(view: View) {
        mCloseButton = view.findViewById(R.id.fragmentDialogBottomsheetRegister_imageButton_close)
        mUserIdTextInputLayout = view.findViewById(R.id.fragmentDialogBottomsheetRegister_textInputLayout_userId)
        mUserIdEditText = view.findViewById(R.id.fragmentDialogBottomsheetRegister_xeiEditText_userId)
        mPasswordTextInputLayout = view.findViewById(R.id.fragmentDialogBottomsheetRegister_textInputLayout_password)
        mPasswordEditText = view.findViewById(R.id.fragmentDialogBottomsheetRegister_xeiEditText_password)
        mPasswordEditText.transformationMethod = PasswordTransformationMethod()

        // TODO: temp
        mMobileNoTextInputLayout = view.findViewById(R.id.fragmentDialogBottomsheetRegister_textInputLayout_mobileNo)
        mMobileNoEditText = view.findViewById(R.id.fragmentDialogBottomsheetRegister_xeiEditText_mobileNo)
        mNationalIdTextInputLayout = view.findViewById(R.id.fragmentDialogBottomsheetRegister_textInputLayout_nationalId)
        mNationalIdEditText = view.findViewById(R.id.fragmentDialogBottomsheetRegister_xeiEditText_nationalId)

        mRegisterButton = view.findViewById(R.id.fragmentDialogBottomsheetRegister_bamiloActionButton_register)
        mGoToLoginScreenButton = view.findViewById(R.id.fragmentDialogBottomsheetRegister_xeiButton_goToLogin)
    }

    private fun initTextInputLayouts() {
        val errorMsgTypeface = TypeFaceHelper.getInstance(context).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR)

        mUserIdTextInputLayout.setErrorTypeface(errorMsgTypeface)
        mPasswordTextInputLayout.setErrorTypeface(errorMsgTypeface)
        //TODO: temp
        mMobileNoTextInputLayout.setErrorTypeface(errorMsgTypeface)
        mNationalIdTextInputLayout.setErrorTypeface(errorMsgTypeface)

        mUserIdTextInputLayout.makeErrorViewScrollable(2)
        mPasswordTextInputLayout.makeErrorViewScrollable(2)
        // TODO: temp
        mMobileNoTextInputLayout.makeErrorViewScrollable(2)
        mNationalIdTextInputLayout.makeErrorViewScrollable(2)
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
        mPasswordEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) expandBottomSheet() }
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
            R.id.fragmentDialogBottomsheetRegister_imageButton_close -> dismiss()
            R.id.fragmentDialogBottomsheetRegister_bamiloActionButton_register -> onRegisterButtonClicked()
            R.id.fragmentDialogBottomsheetRegister_xeiButton_goToLogin -> openLoginScreen()
        }
    }

    private fun onRegisterButtonClicked() {
        if (isInputDataValid()) {
            register()
        }
    }

    private fun openLoginScreen() {
        // TODO: open login bottomsheet
        dismiss()
    }

    /******************************************* Legacy Code ***************************************/
    private fun isInputDataValid() : Boolean {
        var result = validateField(context, getString(R.string.national_id), mNationalIdTextInputLayout, mNationalIdEditText.text.toString(), true, 10, 10, getString(R.string.normal_string_regex), "")
        result = validateField(context, getString(R.string.email_address), mUserIdTextInputLayout, mUserIdEditText.text.toString(), true, 0, 0, getString(R.string.email_regex), resources.getString(R.string.error_invalid_email)) && result
        result = validateField(context, getString(R.string.password), mPasswordTextInputLayout, mPasswordEditText.text.toString(), true, 6, 0, null, "") && result
        result = validateField(context, getString(R.string.mobile_number), mMobileNoTextInputLayout, mMobileNoEditText.text.toString(), true, 0, 0, getString(R.string.cellphone_regex), "") && result
        return result
    }

    private fun validateField(context: Context?, label: String, til: TextInputLayout, text: String, isRequired: Boolean, min: Int, max: Int, regex: String?, errorMessage: String): Boolean {
        var errorMessage = errorMessage
        var result = true
        til.error = null
        // Case empty
        if (isRequired && android.text.TextUtils.isEmpty(text)) {
            errorMessage = context!!.getString(R.string.error_isrequired)
            til.error = errorMessage
            result = false
        } else if (min > 0 && text.length < min) {
            til.error = label + " " + String.format(context!!.resources.getString(R.string.form_textminlen), min)
            result = false
        } else if (max > 0 && text.length > max) {
            til.error = label + " " + String.format(context!!.resources.getString(R.string.form_textmaxlen), max)
            result = false
        } else if (regex != null) {

            val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            if (TextUtils.isEmpty(errorMessage)) {
                errorMessage = context!!.getString(R.string.error_invalid_value)
            }

            val matcher = pattern.matcher(text)
            result = matcher.matches()
            if (!result) {
                til.error = errorMessage
            }
        }// Case no match regex
        // Case too long
        // Case too short
        return result
    }

    private fun register() = triggerRegister(ApiConstants.USER_REGISTRATION_API_PATH, ContentValues().apply {
            put("customer[national_id]", mNationalIdEditText.text.toString())
            put("customer[email]", mUserIdEditText.text.toString())
            put("customer[password]", mPasswordEditText.text.toString())
            put("customer[phone]", mMobileNoEditText.text.toString())
        })

    private fun triggerRegister(endpoint: String, values: ContentValues) =
    // Show progress
    // TODO: use farshid functions
//            showActivityProgress()
    // Request
    BamiloApplication.INSTANCE.sendRequest(RegisterHelper(), RegisterHelper.createBundle(endpoint, values), object: IResponseCallback {
        override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
            // Validate fragment visibility
            if (isOnStoppingProcess || activity == null) {
                return
            }
            handleSuccessEvent(baseResponse)
            // Validate event
            val eventType = baseResponse?.eventType
            when (eventType) {

                EventType.REGISTER_ACCOUNT_EVENT -> {
                    if (activity != null) {
                        (activity as BaseActivity).dismissProgress()
                    }
                    if (baseResponse.successMessages != null && baseResponse.successMessages!!.containsKey("CUSTOMER_REGISTRATION_STEP_1_VALIDATED")) {
                        navigateToVerificationFragment()
                    } else {
                        // Tracking
                        var customerId = SimpleEventModel.NO_VALUE
                        var customerEmail: String? = ""
                        if (BamiloApplication.CUSTOMER != null) {
                            customerId = BamiloApplication.CUSTOMER.id.toLong()
                            customerEmail = BamiloApplication.CUSTOMER.email
                        }
                        val authEventModel = MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_SUCCESS,
                                Constants.LOGIN_METHOD_EMAIL, customerId,
                                MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, if (customerEmail != null) EmailHelper.getHost(customerEmail) else "",
                                        true))
                        TrackerManager.trackEvent(context, EventConstants.Signup, authEventModel)
                        //                TrackerManager.trackEvent(getBaseActivity(), EmarsysEventConstants.SignUp, EmarsysEventFactory.signup("email", EmailHelper.getHost(BamiloApplication.CUSTOMER.getEmail()), true));
                        // Notify user
                        (activity as BaseActivity).showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login))
                        // Finish
                        activity!!.onBackPressed()
                        // Set facebook login
                        CustomerUtils.setChangePasswordVisibility(activity, false)
                        (activity as BaseActivity).setupDrawerNavigation()
                    }
                }
                else -> {
                    if (activity != null) {
                        (activity as BaseActivity).dismissProgress()
                    }
                }
            }
        }

        var isOnStoppingProcess = false

        override fun onRequestError(baseResponse: BaseResponse<*>?) {
            if (activity != null) {
                (activity as BaseActivity).dismissProgress()
            }
            // Validate fragment visibility
            if (isOnStoppingProcess) {
                return
            }
            // Validate error o super
            if (handleErrorEvent(baseResponse)) {
                return
            }
            // Validate event
            val eventType = baseResponse?.eventType
            when (eventType) {

                EventType.REGISTER_ACCOUNT_EVENT -> {
                    if (activity != null) {
                        (activity as BaseActivity).dismissProgress()
                    }
                    // Tracking
                    val authEventModel = MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                            Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                            MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false))
                    TrackerManager.trackEvent(context, EventConstants.Signup, authEventModel)

                    // Validate and show errors
//                    showFragmentContentContainer()
                    // Show validate messages
                    showValidateMessages(baseResponse)
                    (activity as BaseActivity).extraTabLayout.visibility = View.VISIBLE
                }

                else -> {
                }
            }
        }
    })

    fun handleSuccessEvent(baseResponse: BaseResponse<*>?): Boolean {
        // Validate event
        val eventType = baseResponse?.eventType

        when (eventType) {
            EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT, EventType.ADD_PRODUCT_BUNDLE -> {
                (activity as BaseActivity).updateCartInfo()
                return true
            }
            EventType.GET_SHOPPING_CART_ITEMS_EVENT, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT -> {
                (activity as BaseActivity).updateCartInfo()
                return true
            }
            EventType.GUEST_LOGIN_EVENT, EventType.LOGIN_EVENT, EventType.AUTO_LOGIN_EVENT, EventType.FORGET_PASSWORD_EVENT, EventType.REMOVE_PRODUCT_FROM_WISH_LIST, EventType.ADD_PRODUCT_TO_WISH_LIST, EventType.ADD_VOUCHER, EventType.REMOVE_VOUCHER, EventType.SUBMIT_FORM -> {
                if (baseResponse.eventTask == EventTask.ACTION_TASK) {
                    showWarningMessage(WarningFactory.SUCCESS_MESSAGE, baseResponse.successMessage, eventType)
                }
                return true
            }
            else -> {
            }
        }
        return false
    }

    fun handleErrorEvent(response: BaseResponse<*>?): Boolean {
        // Validate priority
        if (!response!!.isPriority) {
            return false
        }
        // Hide keyboard if error screen shows
        if ((activity as BaseActivity) != null) {
            (activity as BaseActivity).hideKeyboard()
        }
        // Validate error code
        val errorCode = response.error.code
        // Case network error
        return if (ErrorCode.isNetworkError(errorCode)) {
            handleNetworkError(errorCode, response.eventTask)
        } else {
            handleRequestError(errorCode, response)
        }// Case request error
    }

    private fun showWarningMessage(@WarningFactory.WarningErrorType warningFact: Int, message: String?, eventType: EventType?) {
        if (TextUtils.isNotEmpty(message)) {
            (activity as BaseActivity).showWarningMessage(warningFact, message)
        } else {
            val id = MessagesUtils.getMessageId(eventType, true)
            if (id > 0) {
                (activity as BaseActivity).showWarningMessage(warningFact, (activity as BaseActivity).getString(id))
            }
        }
    }

    private fun handleNetworkError(errorCode: Int, eventTask: EventTask): Boolean {
        var result = true
        when (errorCode) {
            ErrorCode.HTTP_STATUS, ErrorCode.IO, ErrorCode.CONNECT_ERROR, ErrorCode.TIME_OUT -> if (eventTask == EventTask.ACTION_TASK) {
                (activity as BaseActivity).showWarningMessage(WarningFactory.ERROR_MESSAGE,
                        (activity as BaseActivity).getString(R.string.network_timeout_error))
                if ((activity as BaseActivity) != null) {
                    (activity as BaseActivity).dismissProgress()
                }
//                showFragmentContentContainer()
            } else {
//                showFragmentNetworkErrorRetry()
            }
            ErrorCode.NO_CONNECTIVITY ->
                // Show no network layout
                if (eventTask == EventTask.ACTION_TASK) {
                    (activity as BaseActivity).showWarningMessage(WarningFactory.ERROR_MESSAGE,
                            (activity as BaseActivity).getString(R.string.no_internet_access_warning_title))
                    if ((activity as BaseActivity) != null) {
                        (activity as BaseActivity).dismissProgress()
                    }
//                    showFragmentContentContainer()
                } else {
//                    showFragmentNoNetworkRetry()
                }
            ErrorCode.SSL, ErrorCode.SERVER_IN_MAINTENANCE, ErrorCode.SERVER_OVERLOAD -> if (eventTask == EventTask.ACTION_TASK) {
                (activity as BaseActivity).showWarningMessage(WarningFactory.ERROR_MESSAGE,
                        (activity as BaseActivity).getString(R.string.server_in_maintenance_warning))
                if ((activity as BaseActivity) != null) {
                    (activity as BaseActivity).dismissProgress()
                }
//                showFragmentContentContainer()
            } else {
//                showFragmentServerMaintenanceRetry()
            }
            else -> result = false
        }
        return result
    }

    private fun handleRequestError(errorCode: Int, response: BaseResponse<*>): Boolean {
        when (errorCode) {
            ErrorCode.ERROR_PARSING_SERVER_DATA -> {
//                showFragmentServerMaintenanceRetry()
                return true
            }
            ErrorCode.CUSTOMER_NOT_LOGGED_IN -> {
                // Clean all customer data
                LogOut.cleanCustomerData(activity as BaseActivity)
                // Goto login
                (activity as BaseActivity).onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE,
                        FragmentController.ADD_TO_BACK_STACK)
                return true
            }
            else -> {
                // Show warning messages
                handleErrorTaskEvent(response.errorMessage, response.eventTask,
                        response.eventType)
                return false
            }
        }
    }

    private fun handleErrorTaskEvent(errorMessage: String, eventTask: EventTask,
                             eventType: EventType) {
        if (eventTask == EventTask.ACTION_TASK) {
            when (eventType) {
                // Case form submission
                EventType.REVIEW_RATING_PRODUCT_EVENT, EventType.LOGIN_EVENT, EventType.GUEST_LOGIN_EVENT, EventType.REGISTER_ACCOUNT_EVENT, EventType.EDIT_USER_DATA_EVENT, EventType.CHANGE_PASSWORD_EVENT, EventType.FORGET_PASSWORD_EVENT, EventType.CREATE_ADDRESS_EVENT, EventType.EDIT_ADDRESS_EVENT, EventType.SET_MULTI_STEP_SHIPPING, EventType.SET_MULTI_STEP_PAYMENT, EventType.SUBMIT_FORM -> {
                    // If the error message is empty used the showFormValidateMessages(form)
                    if (TextUtils.isEmpty(errorMessage)) {
                        return
                    }
                    showWarningMessage(WarningFactory.ERROR_MESSAGE, errorMessage, eventType)
                }
                // Case other tasks

                else -> showWarningMessage(WarningFactory.ERROR_MESSAGE, errorMessage, eventType)
            }
        }
    }

    private fun showValidateMessages(baseResponse: BaseResponse<*>) {
        val map = baseResponse.validateMessages
        mUserIdTextInputLayout.error = null
        mMobileNoTextInputLayout.error = null
        mNationalIdTextInputLayout.error = null
        mPasswordTextInputLayout.error = null

        if (CollectionUtils.isNotEmpty(map)) {
            for (key in map!!.keys) {
                when (key.toString()) {
                    "national_id" -> mNationalIdTextInputLayout.error = map[key].toString()
                    "email" -> mUserIdTextInputLayout.error = map[key].toString()
                    "password" -> mPasswordTextInputLayout.error = map[key].toString()
                    "phone" -> mMobileNoTextInputLayout.error = map[key].toString()
                }// TODO: 8/28/18 farshid
                //                        HoloFontLoader.applyDefaultFont(tilNationalId);
                // TODO: 8/28/18 farshid
                //                        HoloFontLoader.applyDefaultFont(tilEmail);
                // TODO: 8/28/18 farshid
                //                        HoloFontLoader.applyDefaultFont(tilPassword);
                // TODO: 8/28/18 farshid
                //                        HoloFontLoader.applyDefaultFont(tilPhoneNumber);
            }
        }
    }

    private var isInCheckoutProcess: Boolean = false
    private var phoneVerificationOnGoing: Boolean = false

    private fun navigateToVerificationFragment() {
//        showGhostFragmentContentContainer()
//        showFragmentLoading()
        phoneVerificationOnGoing = true
        val phoneNumber = mMobileNoEditText.text.toString()
        val args = Bundle()
        args.putString(ConstantsIntentExtra.PHONE_NUMBER, phoneNumber)
        (activity as BaseActivity).onSwitchFragment(FragmentType.MOBILE_VERIFICATION, args, false)

        val prefs = context!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false)
        editor.apply()

        dismiss()
    }


    /******************************************* Legacy Code ***************************************/

}
package com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.ProductDetailActivity
import com.bamilo.android.appmodule.modernbamilo.authentication.login.LoginDialogBottomSheet
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.AuthenticationRepo
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.response.ForgetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.authentication.verification.VerificationFragmentBottomSheet
import com.bamilo.android.appmodule.modernbamilo.util.customtoast.PoiziToast
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.bamilo.android.framework.service.utils.TextUtils
import kotlinx.android.synthetic.main.bottomsheet_forget_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordBottomSheet : BottomSheetDialogFragment() {

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.bottomsheet_forget_password, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindClickListener()
    }

    private fun bindClickListener() {
        forgetPasswordBottomSheet_appImageView_close.setOnClickListener { dismiss() }
        forgetPasswordBottomSheet_bamiloButton_forgetPassword.setOnClickListener { recoverPassword() }
        forgetPasswordBottomSheet_button_signUp.setOnClickListener { openLoginScreen() }
    }

    private fun recoverPassword() {
        val identifier = forgetPasswordBottomSheet_editText_emailOrPhone.text.toString()
        if (!identifier.isEmpty()) {
            showProgress()
            context?.let { ctx ->
                AuthenticationRepo.forgetPasswordRequest(ctx,
                        identifier,
                        object : Callback<ResponseWrapper<ForgetPasswordRequestModel>> {
                            override fun onFailure(call: Call<ResponseWrapper<ForgetPasswordRequestModel>>, t: Throwable) {
                                hideProgress()

                                PoiziToast
                                        .with(ctx)
                                        ?.error(getString(R.string.error_forgotpassword_title),
                                                Toast.LENGTH_SHORT)
                                        ?.show()
                            }

                            override fun onResponse(call: Call<ResponseWrapper<ForgetPasswordRequestModel>>,
                                                    response: Response<ResponseWrapper<ForgetPasswordRequestModel>>) {
                                response.body()?.run {
                                    if (success) {

                                        if (metadata.nextStepVerification) {
                                            // The user is using the phone number
                                            showVerificationView()

                                            dismiss()
                                        } else {
                                            // The user is using the email address
                                            hideProgress()

                                            PoiziToast
                                                    .with(ctx)
                                                    ?.success(getString(R.string.forgotten_password_sub_text),
                                                            Toast.LENGTH_SHORT)
                                                    ?.show()

                                            dismiss()
                                        }

                                    } else {
                                        hideProgress()

                                        messages.validate?.run {
                                            get(0)["message"]?.let {
                                                PoiziToast
                                                        .with(ctx)
                                                        ?.error(it,
                                                                Toast.LENGTH_SHORT)
                                                        ?.show()
                                            }
                                        }
                                    }
                                }
                            }

                        })
            }
        } else {
            forgetPasswordBottomSheet_til_emailOrPhone.error = "ایمیل یا شماره موبایل را وارد کنید"
        }
    }

    private fun showVerificationView() {
        VerificationFragmentBottomSheet.newInstance(
                VerificationFragmentBottomSheet.VerificationType.FORGET_PASSWORD,
                forgetPasswordBottomSheet_editText_emailOrPhone.text.toString())
                .show(fragmentManager, "verifyPhone")
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

    private fun openLoginScreen() {
        fragmentManager?.let {
            LoginDialogBottomSheet.show(it, null, null)
        }

        dismiss()
    }
}
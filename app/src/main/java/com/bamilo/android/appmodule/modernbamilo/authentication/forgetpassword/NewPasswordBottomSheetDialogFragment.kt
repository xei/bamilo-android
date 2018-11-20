package com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.AuthenticationRepo
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.response.ForgetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.util.customtoast.PoiziToast
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import kotlinx.android.synthetic.main.bottomsheet_forget_password.*
import kotlinx.android.synthetic.main.bottomsheet_new_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mSignUpButton: BamiloActionButton

    companion object {
        @JvmStatic
        fun newInstance(identifier: String): NewPasswordBottomSheetDialogFragment {
            return NewPasswordBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("identifier", identifier)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.bottomsheet_new_password, container, false)
        mSignUpButton = view.findViewById(R.id.newPasswordBottomSheet_bamiloButton_newPassword)
        mSignUpButton.setOnClickListener {changePasswordClicked()}
        return view
    }

    private fun changePasswordClicked() {
        if (isInputValid())
            if (!TextUtils.isEmpty(newPasswordBottomSheet_editText_newPassword.text)) {
                AuthenticationRepo.resetPassword(context!!,
                        arguments?.getString("identifier")!!, newPasswordBottomSheet_editText_newPassword.text.toString(),
                        object : Callback<ResponseWrapper<ForgetPasswordRequestModel>> {

                            override fun onFailure(call: Call<ResponseWrapper<ForgetPasswordRequestModel>>, t: Throwable) {
                                context?.let {
                                    PoiziToast.with(it)
                                            ?.setGravity(Gravity.TOP)
                                            ?.success(it.getString(R.string.error_forgotpassword_title), Toast.LENGTH_SHORT)
                                            ?.show()
                                }
                            }

                            override fun onResponse(call: Call<ResponseWrapper<ForgetPasswordRequestModel>>,
                                                    response: Response<ResponseWrapper<ForgetPasswordRequestModel>>) {

                                if (response.isSuccessful) {
                                    context?.let {
                                        PoiziToast.with(it)
                                                ?.setGravity(Gravity.TOP)
                                                ?.success(it.getString(R.string.password_changed), Toast.LENGTH_SHORT)
                                                ?.show()
                                    }
                                    dismiss()
                                }

                            }

                        })
            }
    }

    private fun isInputValid(): Boolean {
        if (newPasswordBottomSheet_editText_newPassword.text.toString().isEmpty()) {
            newPasswordBottomSheet_til_newPassword.error = getString(R.string.insert_password)
            return false
        }
        return true
    }
}
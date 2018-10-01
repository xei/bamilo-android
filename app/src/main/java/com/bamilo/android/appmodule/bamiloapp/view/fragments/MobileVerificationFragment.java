package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.MobileVerificationHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.view.widget.PinEntryInput;
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.util.EnumSet;
import java.util.Locale;

/**
 * Created by mohsen on 2/5/18.
 */

public class MobileVerificationFragment extends BaseFragment implements IResponseCallback {
    private static final String MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY = "MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY";
    private static final String MOBILE_VERIFICATION_DONE = "MOBILE_VERIFICATION_DONE";
    private static final String MOBILE_VERIFICATION_FAILED = "MOBILE_VERIFICATION_FAILED";

    private String phoneNumber;
    private long tokenReceiveTime;
    private Handler mTokenCountDownHandler;
    private Runnable mTokenCountDownRunnable;
    private long tokenResendDelay = 2 * 60 * 1000;
    private TextView tvResendToken;
    private TextView tvResendTokenNotice;

    private String fragmentTagToPopFromBackStack;


    public MobileVerificationFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.LOGIN_OUT,
                R.layout.fragment_mobile_verification,
                R.string.fragment_phone_verification_title,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        phoneNumber = args.getString(ConstantsIntentExtra.PHONE_NUMBER);
        if (phoneNumber == null) {
            getBaseActivity().onBackPressed();
        }
        fragmentTagToPopFromBackStack = args.getString(ConstantsIntentExtra.TAG_BACK_FRAGMENT, null);
        mTokenCountDownHandler = new Handler();
        mTokenCountDownRunnable = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (tvResendToken != null) {
                    if (now - tokenReceiveTime > tokenResendDelay) {
                        tvResendToken.setVisibility(View.VISIBLE);
                        tvResendTokenNotice.setText(R.string.verification_token_receive_notice);
                    } else {
                        long remaining = (tokenResendDelay - (now - tokenReceiveTime)) / 1000;
                        tvResendTokenNotice.setText(String.format(new Locale("fa"), "%s %d:%02d", getString(R.string.verification_resend_token_after),
                                remaining / 60, remaining % 60));
                        mTokenCountDownHandler.postDelayed(this, 500);
                    }
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(view);

        TextView tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);

        tvPhoneNumber.setText( getString(R.string.verifyPhoneNoScreen_subtitle, StringExtKt.persianizeNumberString(phoneNumber)));

        final BamiloActionButton btnSubmitToken = view.findViewById(R.id.btnSubmitToken);

        final PinEntryInput etPin = view.findViewById(R.id.etPin);

        final int tokenMaxLength = etPin.getMaxLength();

        tvResendTokenNotice = view.findViewById(R.id.tvResendTokenNotice);
        tvResendTokenNotice.setText(null);

        tvResendToken = view.findViewById(R.id.tvResendToken);
        tvResendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerRequestForToken(phoneNumber);
            }
        });

        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == tokenMaxLength) {
                    btnSubmitToken.setEnabled(true);
                } else {
                    btnSubmitToken.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etPin.setOnPinEnteredListener(new PinEntryInput.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                triggerMobileVerification(phoneNumber, str.toString());
            }
        });
        btnSubmitToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = etPin.getText().toString();
                if (token.length() < tokenMaxLength) {
                    etPin.requestFocus();
                } else {
                    triggerMobileVerification(phoneNumber, token);
                }
            }
        });

        triggerRequestForToken(phoneNumber);
    }

    private void triggerRequestForToken(String phoneNumber) {
        triggerMobileVerification(phoneNumber, null);
    }

    private void triggerMobileVerification(String phoneNumber, String token) {
        ContentValues values = new ContentValues();
        values.put("phone", phoneNumber);
        if (token != null) {
            values.put("token", token);
        }
        triggerContentEvent(new MobileVerificationHelper(),
                MobileVerificationHelper.createBundle(values), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        if (baseResponse.getSuccessMessages() != null) {
            if (baseResponse.getSuccessMessages().containsKey(MOBILE_VERIFICATION_CODE_SENT_SUCCESSFULLY)) {
                tokenReceiveTime = System.currentTimeMillis();
                tvResendToken.setVisibility(View.GONE);
                mTokenCountDownHandler.post(mTokenCountDownRunnable);
            } else if (baseResponse.getSuccessMessages().containsKey(MOBILE_VERIFICATION_DONE)) {
                SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, true);
                editor.putString(ConstantsSharedPrefs.KEY_PHONE_NUMBER, phoneNumber);
                editor.apply();
                if (fragmentTagToPopFromBackStack != null) {
                    getBaseActivity().popBackStackUntilTag(fragmentTagToPopFromBackStack);
                }
                getBaseActivity().onBackPressed();
                return;
            }
        }
        showFragmentContentContainer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTokenCountDownHandler.removeCallbacks(mTokenCountDownRunnable);
        mTokenCountDownHandler = null;
        mTokenCountDownRunnable = null;
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        if (TextUtils.isNotEmpty(baseResponse.getValidateMessage())) {
            showWarningErrorMessage(baseResponse.getValidateMessage());
        } else {
            super.handleErrorEvent(baseResponse);
        }
        showFragmentContentContainer();
        if (!(baseResponse.getErrorMessages() != null &&
                baseResponse.getErrorMessages().containsKey(MOBILE_VERIFICATION_FAILED))) {
            getBaseActivity().onBackPressed();
        }
    }
}

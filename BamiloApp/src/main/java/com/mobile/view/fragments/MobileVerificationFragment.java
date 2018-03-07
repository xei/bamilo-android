package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.helpers.session.MobileVerificationHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import com.mobile.view.widget.PinEntryInput;

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
                IntConstants.ACTION_BAR_NO_TITLE,
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
        HoloFontLoader.applyDefaultFont(view);

        TextView tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText(TextUtils.makeDigitsFarsi(phoneNumber));

        final Button btnSubmitToken = (Button) view.findViewById(R.id.btnSubmitToken);

        final PinEntryInput etPin = (PinEntryInput) view.findViewById(R.id.etPin);

        final int tokenMaxLength = etPin.getMaxLength();

        tvResendTokenNotice = (TextView) view.findViewById(R.id.tvResendTokenNotice);
        tvResendTokenNotice.setText(null);

        tvResendToken = (TextView) view.findViewById(R.id.tvResendToken);
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
        getBaseActivity().hideKeyboard();
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
        super.handleErrorEvent(baseResponse);
        showFragmentContentContainer();
        if (!(baseResponse.getErrorMessages() != null &&
                baseResponse.getErrorMessages().containsKey(MOBILE_VERIFICATION_FAILED))) {
            getBaseActivity().onBackPressed();
        }
    }
}

package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mohsen on 3/6/18.
 */

public class ChangePhoneNumberFragment extends BaseFragment {

    public ChangePhoneNumberFragment() {
        super(EnumSet.of(MyMenuItem.CLOSE_BUTTON),
                NavigationAction.MY_ACCOUNT_USER_DATA,
                R.layout.fragment_change_phone_number,
                R.string.fragment_change_phone_title,
                ADJUST_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(view);

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        final EditText etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPhoneNumber.requestFocus();

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(etPhoneNumber, InputMethodManager.SHOW_IMPLICIT);

        final TextInputLayout tilPhoneNumber = view.findViewById(R.id.tilPhoneNumber);
        view.findViewById(R.id.btnNextToVerification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = TextUtils.makeDigitsEnglish(etPhoneNumber.getText().toString());
                if (validatePhoneNumber(phoneNumber)) {
                    SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
                    editor.apply();

                    Bundle args = new Bundle();
                    args.putString(ConstantsIntentExtra.PHONE_NUMBER, phoneNumber);
                    args.putString(ConstantsIntentExtra.TAG_BACK_FRAGMENT, FragmentType.CHANGE_PHONE_NUMBER_FRAGMENT.toString());
                    getBaseActivity().onSwitchFragment(FragmentType.MOBILE_VERIFICATION, args, false);
                } else {
                    tilPhoneNumber.setError(getString(R.string.enter_correct_phone_number));
                    // TODO: 8/28/18 farshid
//                    HoloFontLoader.applyDefaultFont(tilPhoneNumber);
                }
            }
        });
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(getString(R.string.cellphone_regex), Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}

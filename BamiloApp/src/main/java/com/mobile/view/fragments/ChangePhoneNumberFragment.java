package com.mobile.view.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.Constants;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

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
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HoloFontLoader.applyDefaultFont(view);

        final EditText etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        final TextInputLayout tilPhoneNumber = (TextInputLayout) view.findViewById(R.id.tilPhoneNumber);
        view.findViewById(R.id.btnNextToVerification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = etPhoneNumber.getText().toString();
                if (validatePhoneNumber(phoneNumber)) {
                    SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
                    editor.putString(ConstantsSharedPrefs.KEY_PHONE_NUMBER, phoneNumber);
                    editor.apply();

                    Bundle args = new Bundle();
                    args.putString(ConstantsIntentExtra.PHONE_NUMBER, phoneNumber);
                    getBaseActivity().onSwitchFragment(FragmentType.MOBILE_VERIFICATION, args, false);
                } else {
                    tilPhoneNumber.setError(getString(R.string.enter_correct_phone_number));
                    HoloFontLoader.applyDefaultFont(tilPhoneNumber);
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

package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bamilo.apicore.di.modules.ProfileModule;
import com.bamilo.apicore.presentation.ProfilePresenter;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.UserProfileResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.bamilo.apicore.view.ProfileView;
import com.mobile.app.BamiloApplication;
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.objects.customer.Customer;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.JalaliCalendar;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.PersianDatePickerDialogHelper;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mohsen on 2/17/18.
 */

public class EditProfileFragment extends BaseFragment implements ProfileView, PersianDatePickerDialogHelper.OnDateSelectedListener {

    private static final String GENDER_MALE = "male", GENDER_FEMALE = "female";
    private static final String BIRTHDAY_DELIMITER = "-";
    private static final String PERSIAN_DATE_PATTERN = "%02d/%02d/%02d";
    private static final String SERVER_DATE_PATTERN = "%d-%d-%d";
    private static final String FIELD_NATIONAL_ID = "national_id",
            FIELD_FIRST_NAME = "first_name", FIELD_LAST_NAME = "last_name", FIELD_CARD_NUMBER = "card_number";

    @Inject
    ProfilePresenter presenter;

    private EditText etFirstName, etLastName, etEmail, etNationalId;
    private MaskedEditText metCardNumber;
    private TextView tvBirthday, tvPhoneNumber, tvWarningMessage, tvPhoneNumberError, tvCardNumberError;
    private TextInputLayout tilNationalId, tilFirstName, tilLastName;
    private Spinner spinnerGender;
    NestedScrollView nsvEditProfileForm;

    PersianDatePickerDialogHelper persianDatePicker;
    private boolean phoneVerificationOnGoing;
    private UserProfileResponse userProfileResponse;
    private int BIRTHDAY_UPPER_BOUND_DIFF = -10, BIRTHDAY_LOWER_BOUND_DIFF = -70;
    private ArrayList<String> gendersList;
    private String requestBirthday;

    public EditProfileFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_USER_DATA,
                R.layout.fragment_edit_profile,
                R.string.myaccount_userdata,
                ADJUST_CONTENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // inject dependencies by dagger2
        BamiloApplication.getComponent().plus(new ProfileModule(this)).inject(this);

        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        tilFirstName = (TextInputLayout) view.findViewById(R.id.tilFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        tilLastName = (TextInputLayout) view.findViewById(R.id.tilLastName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        tvWarningMessage = (TextView) view.findViewById(R.id.tvWarningMessage);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvPhoneNumberError = (TextView) view.findViewById(R.id.tvPhoneNumberError);
        metCardNumber = (MaskedEditText) view.findViewById(R.id.metCardNumber);
        tvCardNumberError = (TextView) view.findViewById(R.id.tvCardNumberError);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            metCardNumber.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            metCardNumber.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
        etNationalId = (EditText) view.findViewById(R.id.etNationalId);
        tilNationalId = (TextInputLayout) view.findViewById(R.id.tilNationalId);

        spinnerGender = (Spinner) view.findViewById(R.id.spinnerGender);
        gendersList = new ArrayList<>();
        gendersList.add(getString(R.string.gender_male));
        gendersList.add(getString(R.string.gender_female));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, gendersList);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(getString(R.string.choose_prompt));
        spinnerGender.setAdapter(promptAdapter);

        nsvEditProfileForm = (NestedScrollView) view.findViewById(R.id.nsvEditProfileForm);

        view.findViewById(R.id.rlBirthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (persianDatePicker == null) {
                    Calendar now = Calendar.getInstance(Locale.US);
                    now.add(Calendar.YEAR, BIRTHDAY_UPPER_BOUND_DIFF);
                    JalaliCalendar.YearMonthDate defaultDate =
                            JalaliCalendar.gregorianToJalali(new JalaliCalendar
                                    .YearMonthDate(now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)));
                    persianDatePicker = PersianDatePickerDialogHelper
                            .newInstance(getContext(),
                                    defaultDate,
                                    defaultDate.getYear(),
                                    defaultDate.getYear() + BIRTHDAY_LOWER_BOUND_DIFF);
                    persianDatePicker.setOnDateSelectedListener(EditProfileFragment.this);
                }

                JalaliCalendar.YearMonthDate selectedDate = null;
                String birthday = userProfileResponse.getUserProfile().getBirthday();
                    /* Prevent app crash if the server is sending an inconsistent data */
                try {
                    String[] birthdayParts = birthday.split(BIRTHDAY_DELIMITER);
                    if (birthdayParts.length == 3) {
                        selectedDate = new JalaliCalendar.YearMonthDate(Integer.valueOf(birthdayParts[0]),
                                Integer.valueOf(birthdayParts[1]) - 1,
                                Integer.valueOf(birthdayParts[2]));
                    }
                } catch (Exception ignore) {
                }
                if (selectedDate != null) {
                    persianDatePicker.setSelectedDate(selectedDate);
                }
                persianDatePicker.showDialog();
            }
        });
        view.findViewById(R.id.rlPhoneNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPhoneVerificationProcess();
            }
        });
        if (userProfileResponse == null) {
            presenter.loadUserProfile(NetworkConnectivity.isConnected(getContext()));
        } else {
            if (phoneVerificationOnGoing) {
                SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
                boolean verified = prefs.getBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
                if (verified) {
                    String phoneNumber = prefs.getString(ConstantsSharedPrefs.KEY_PHONE_NUMBER, "");
                    prefs
                            .edit()
                            .putString(ConstantsSharedPrefs.KEY_PHONE_NUMBER, "")
                            .apply();
                    if (TextUtils.isNotEmpty(phoneNumber)) {
                        userProfileResponse.getUserProfile().setPhone(phoneNumber);
                        userProfileResponse.getUserProfileMetaData().setWarningMessage(null);
                    }
                }
            }
            performUserProfile(userProfileResponse);
        }

        view.findViewById(R.id.btnSubmitProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideErrors();
                String cardNumber = metCardNumber.getRawText();
                String phoneNumber = tvPhoneNumber.getText().toString();
                String nationalId = etNationalId.getText().toString();
                boolean validated = validateNationalId(nationalId) &&
                        validateCardNumber(cardNumber) &&
                        validatePhoneNumber(phoneNumber);
                if (validated) {
                    UserProfile userProfile = userProfileResponse.getUserProfile();

                    userProfile.setNationalId(nationalId);
                    if (requestBirthday != null) {
                        userProfile.setBirthday(requestBirthday);
                    }
                    userProfile.setFirstName(etFirstName.getText().toString());
                    userProfile.setLastName(etLastName.getText().toString());
                    userProfile.setPhone(phoneNumber);

                    String genderItem = (String) spinnerGender.getSelectedItem();
                    int indexOfGender = gendersList.indexOf(genderItem);
                    if (indexOfGender != -1) {
                        String gender;
                        if (indexOfGender == 0) {
                            gender = GENDER_MALE;
                        } else {
                            gender = GENDER_FEMALE;
                        }
                        userProfile.setGender(gender);
                    }

                    userProfile.setCardNumber(cardNumber);
                    getBaseActivity().hideKeyboard();
                    hideErrors();
                    presenter.submitProfile(NetworkConnectivity.isConnected(getContext()), userProfile);
                }
            }
        });
        HoloFontLoader.applyDefaultFont(view);
    }

    @Override
    public void onPause() {
        hideErrors();
        super.onPause();
    }

    private void hideErrors() {
        tvPhoneNumberError.setVisibility(View.GONE);
        tvCardNumberError.setVisibility(View.GONE);
        tilNationalId.setError(null);
    }

    private void navigateToPhoneVerificationProcess() {
        phoneVerificationOnGoing = true;
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
        editor.apply();
        getBaseActivity().onSwitchFragment(FragmentType.CHANGE_PHONE_NUMBER_FRAGMENT, null, true);
    }

    private boolean validateCardNumber(String cardNumber) {
        if (TextUtils.isNotEmpty(cardNumber) && cardNumber.length() != 16) {
            tvCardNumberError.setText(getString(R.string.card_number_isnt_valid));
            tvCardNumberError.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        String regexPattern = getString(R.string.cellphone_regex);

        Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(phoneNumber);
        boolean result = matcher.matches();
        if (!result) {
            tvPhoneNumberError.setVisibility(View.VISIBLE);
        } else {
            tvPhoneNumberError.setVisibility(View.GONE);
        }
        return result;
    }

    private boolean validateNationalId(String nationalId) {
        if (TextUtils.isNotEmpty(nationalId) && nationalId.length() == 10) {
            return true;
        }
        tilNationalId.setError(getString(R.string.address_national_id_length_error));
        HoloFontLoader.applyDefaultFont(tilNationalId);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.destroy();
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        presenter.loadUserProfile(NetworkConnectivity.isConnected(getContext()));
    }


    @Override
    public void performUserProfile(UserProfileResponse userProfileResponse) {
        if (userProfileResponse != null) {
            this.userProfileResponse = userProfileResponse;
            UserProfile userProfile = userProfileResponse.getUserProfile();
            etFirstName.setText(userProfile.getFirstName() != null ? userProfile.getFirstName().trim() : "");
            etLastName.setText(userProfile.getLastName() != null ? userProfile.getLastName().trim() : "");
            etEmail.setText(userProfile.getEmail());
            if (TextUtils.isNotEmpty(userProfile.getPhone())) {
                tvPhoneNumber.setText(userProfile.getPhone());
                tvPhoneNumber.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_1));
            }
            metCardNumber.setText(userProfile.getCardNumber());
            if (TextUtils.isNotEmpty(userProfile.getBirthday())) {
                String birthday = userProfile.getBirthday();
                String[] birthdayParts = birthday.split(BIRTHDAY_DELIMITER);
                if (birthdayParts.length == 3) {
                    /* Prevent app crash if the server is sending an inconsistent data */
                    try {
                        birthday = String.format(Locale.US, PERSIAN_DATE_PATTERN,
                                Integer.valueOf(birthdayParts[0]),
                                Integer.valueOf(birthdayParts[1]),
                                Integer.valueOf(birthdayParts[2]));
                        tvBirthday.setText(birthday);
                        tvBirthday.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_1));
                    } catch (Exception ignore) {
                    }
                }
            }
            if (TextUtils.isNotEmpty(userProfileResponse.getWarningMessage())) {
                tvWarningMessage.setVisibility(View.VISIBLE);
                tvWarningMessage.setText(userProfileResponse.getWarningMessage());
            }
            if (TextUtils.isNotEmpty(userProfile.getGender())) {
                /* The first position is place holder title */
                int position = 1;
                if (userProfile.getGender().equals(GENDER_FEMALE)) {
                    position = 2;
                }
                spinnerGender.setSelection(position);
            }
            etNationalId.setText(userProfile.getNationalId());
            storeCustomerDate(userProfile);
        } else {
            showFragmentErrorRetry();
        }
    }

    private void storeCustomerDate(UserProfile userProfile) {
        Customer customer = BamiloApplication.CUSTOMER;
        if (customer == null) {
            customer = new Customer();
            BamiloApplication.CUSTOMER = customer;
        }
        customer.setEmail(userProfile.getEmail());
        customer.setGender(userProfile.getGender());
        customer.setNationalId(userProfile.getNationalId());
        customer.setPhoneNumber(userProfile.getPhone());
        customer.setFirstName(userProfile.getFirstName());
        customer.setLastName(userProfile.getLastName());
    }

    @Override
    public void onProfileSubmitted(UserProfileResponse response) {
        storeCustomerDate(response.getUserProfile());
        showWarningSuccessMessage(getString(R.string.edit_profile_submitted_successfully));
        getBaseActivity().setupDrawerNavigation();
        getBaseActivity().onBackPressed();
    }

    @Override
    public void showServerError(EventType eventType, ServerResponse response) {
        super.showServerError(eventType, response);
        if (eventType == EventType.EDIT_USER_DATA_EVENT) {
            if (response != null && response.getMessages() != null &&
                    CollectionUtils.isNotEmpty(response.getMessages().getValidateErrors())) {
                List<ServerResponse.ServerValidateError> validateErrors = response.getMessages().getValidateErrors();
                for (ServerResponse.ServerValidateError error : validateErrors) {
                    String field = error.getField();
                    if (field.equals(FIELD_NATIONAL_ID)) {
                        tilNationalId.setError(error.getMessage());
                        HoloFontLoader.applyDefaultFont(tilNationalId);
                    } else if (field.equals(FIELD_FIRST_NAME)) {
                        tilFirstName.setError(error.getMessage());
                        HoloFontLoader.applyDefaultFont(tilFirstName);
                    } else if (field.equals(FIELD_LAST_NAME)) {
                        tilLastName.setError(error.getMessage());
                        HoloFontLoader.applyDefaultFont(tilLastName);
                    } else if (field.equals(FIELD_CARD_NUMBER)) {
                        tvCardNumberError.setText(error.getMessage());
                        tvCardNumberError.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        requestBirthday = String.format(Locale.US, SERVER_DATE_PATTERN, year, month, day);
        userProfileResponse.getUserProfile().setBirthday(requestBirthday);
        tvBirthday.setText(String.format(Locale.US, PERSIAN_DATE_PATTERN, year, month, day));
        tvBirthday.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_1));
    }
}

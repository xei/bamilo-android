package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bamilo.apicore.di.modules.ProfileModule;
import com.bamilo.apicore.presentation.ProfilePresenter;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.bamilo.apicore.view.ProfileView;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

import javax.inject.Inject;

import br.com.sapereaude.maskedEditText.MaskedEditText;

/**
 * Created by mohsen on 2/17/18.
 */

public class EditProfileFragment extends BaseFragment implements ProfileView {

    @Inject
    ProfilePresenter presenter;

    private EditText etFirstName, etLastName, etEmail, etPhoneNumber;
    private MaskedEditText metCardNumber;

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
        HoloFontLoader.applyDefaultFont(view);

        // inject dependencies by dagger2
        BamiloApplication.getComponent().plus(new ProfileModule(this)).inject(this);

        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        metCardNumber = (MaskedEditText) view.findViewById(R.id.metCardNumber);

        presenter.loadUserProfile(NetworkConnectivity.isConnected(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.destroy();
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
    }

    @Override
    public void performUserProfile(UserProfile userProfile) {
        if (userProfile != null) {
            etFirstName.setText(userProfile.getFirstName() != null ? userProfile.getFirstName().trim() : "");
            etLastName.setText(userProfile.getLastName() != null ? userProfile.getLastName().trim() : "");
            etEmail.setText(userProfile.getEmail());
            etPhoneNumber.setText(userProfile.getPhone());
            metCardNumber.setText(userProfile.getCardNumber());
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onProfileSubmitted(ServerResponse response) {

    }
}

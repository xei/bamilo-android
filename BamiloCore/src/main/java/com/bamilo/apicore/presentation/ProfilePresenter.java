package com.bamilo.apicore.presentation;

import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.bamilo.apicore.view.ProfileView;

/**
 * Created by mohsen on 2/27/18.
 */

public interface ProfilePresenter extends BasePresenter<ProfileView> {
    void loadUserProfile(boolean isConnected);

    void submitProfile(boolean isConnected, UserProfile userProfile);
}

package com.bamilo.android.core.presentation;

import com.bamilo.android.core.service.model.data.profile.UserProfile;
import com.bamilo.android.core.view.ProfileView;

/**
 * Created by mohsen on 2/27/18.
 */

public interface ProfilePresenter extends BasePresenter<ProfileView> {
    void loadUserProfile(boolean isConnected);

    void submitProfile(boolean isConnected, UserProfile userProfile);
}

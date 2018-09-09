package com.bamilo.android.core.view;

import com.bamilo.android.core.service.model.UserProfileResponse;

/**
 * Created by mohsen on 2/27/18.
 */

public interface ProfileView extends BaseView {
    void performUserProfile(UserProfileResponse userProfileResponse);

    void onProfileSubmitted(UserProfileResponse response);
}

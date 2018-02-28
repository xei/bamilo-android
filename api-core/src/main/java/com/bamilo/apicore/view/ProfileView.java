package com.bamilo.apicore.view;

import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;

/**
 * Created by mohsen on 2/27/18.
 */

public interface ProfileView extends BaseView {
    void performUserProfile(UserProfile userProfile);

    void onProfileSubmitted(ServerResponse response);
}

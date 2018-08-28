package com.bamilo.android.core.interaction;

import com.bamilo.android.core.service.model.UserProfileResponse;
import com.bamilo.android.core.service.model.data.profile.UserProfile;

import rx.Observable;

/**
 * Created by mohsen on 2/18/18.
 */

public interface ProfileInteractor extends BaseInteractor {
    Observable<UserProfileResponse> loadUserProfile();

    Observable<UserProfileResponse> submitProfile(UserProfile userProfile);
}

package com.bamilo.apicore.interaction;

import com.bamilo.apicore.service.model.UserProfileResponse;

import rx.Observable;

/**
 * Created by mohsen on 2/18/18.
 */

public interface ProfileInteractor extends BaseInteractor {
    Observable<UserProfileResponse> loadUserProfile();
}

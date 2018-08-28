package com.bamilo.android.core.interaction;

import com.bamilo.android.core.service.model.HomeResponse;

import rx.Observable;

public interface HomeInteractor extends BaseInteractor {

    Observable<HomeResponse> loadHome();
}

package com.bamilo.apicore.interaction;

import com.bamilo.apicore.service.model.HomeResponse;

import rx.Observable;

public interface HomeInteractor extends BaseInteractor {

    Observable<HomeResponse> loadHome();
}

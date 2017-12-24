package com.bamilo.apicore.service;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Observable;

/**
 * Created on 12/19/2017.
 * All the api endpoints of Bamilo MobAPI are implemented here
 */

public interface BamiloApiService {

    @GET("main/home")
    @Headers("Content-Type: application/json")
    Observable<JsonObject> loadHome();

}

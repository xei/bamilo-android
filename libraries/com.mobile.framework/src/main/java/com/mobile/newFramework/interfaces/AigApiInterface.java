package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.pojo.BaseResponse;

import retrofit.Callback;
import retrofit.http.GET;


public interface AigApiInterface {

    @GET("/availablecountries")
    void getAvailableCountries(Callback<BaseResponse> callback);

    @GET("/getconfigurations")
    void getCountryConfigurations(Callback<BaseResponse> callback);

}

package com.bamilo.apicore.service;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created on 12/19/2017.
 * All the api endpoints of Bamilo MobAPI are implemented here
 */

public interface BamiloApiService {

    @GET("main/home")
    @Headers("Content-Type: application/json")
    Observable<JsonObject> loadHome();

    @GET("search/find/category/{category-name}/maxitems/{max-items}/page/{page}/{filters}")
    @Headers("Content-Type: application/json")
    Observable<JsonObject> loadCategoryCatalog(@Path("category-name") String category, @Path("filters") String filters, @Path("page") int page, @Path("max-items") int maxItems);

    @GET("search/find/maxitems/{max-items}/page/{page}/hash/{hash}")
    @Headers("Content-Type: application/json")
    Observable<JsonObject> loadHashCatalog(@Path("hash") String hash, @Path("filters") String filters, @Path("page") int page, @Path("max-items") int maxItems);

    @GET("search/find/maxitems/{max-items}/page/{page}/q/{query}")
    @Headers("Content-Type: application/json")
    Observable<JsonObject> loadSearchCatalog(@Path("query") String query, @Path("filters") String filters, @Path("page") int page, @Path("max-items") int maxItems);

}

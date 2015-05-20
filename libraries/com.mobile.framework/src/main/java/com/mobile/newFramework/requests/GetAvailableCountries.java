package com.mobile.newFramework.requests;

import android.content.Context;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigRestAdapter;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by spereira on 5/19/15.
 */
public class GetAvailableCountries extends BaseRequest {

    String BASE_URL = "https://www.jumia.com/mobapi/";

    public GetAvailableCountries(Context context) {
        super(context);
        AigApiInterface service = AigRestAdapter.getRestAdapter(context, BASE_URL).create(AigApiInterface.class);
        service.getAvailableCountries(this);
    }

    @Override
    public void success(BaseResponse baseResponse, Response response) {
        super.success(baseResponse, response);
        System.out.println("GET SUCCESS: " + response.getBody() + " " + baseResponse.success);
    }

    @Override
    public void failure(RetrofitError error) {
        super.failure(error);
        System.out.println("GET ERROR: " + error.getBody());
    }
}

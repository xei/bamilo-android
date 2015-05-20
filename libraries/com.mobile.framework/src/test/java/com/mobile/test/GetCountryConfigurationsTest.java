package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.GetCountryConfigurations;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetCountryConfigurationsTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        GetCountryConfigurations getCountryConfigurations = new GetCountryConfigurations(IS_AUTOMATED_TEST);
        getCountryConfigurations.setCallBack(new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse countries, Response response) {
                System.out.println("TEST SUCCESS: " + response.getBody() + " " + countries.success);
                // tests returned then countdown semaphore
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("TEST ERROR: " + error.getBody());
                // tests returned then countdown semaphore
                mCountDownLatch.countDown();
            }
        });

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}

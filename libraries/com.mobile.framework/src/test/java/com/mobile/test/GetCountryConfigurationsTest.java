package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequestBundle;
import com.mobile.newFramework.requests.configs.GetCountryConfigurations;

public class GetCountryConfigurationsTest extends BaseTestCase {

    BaseRequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        requestBundle = new BaseRequestBundle.Builder()
                .setUrl("https://www.jumia.ci/mobapi/v1.7/main/getconfigurations")
                .setCache(EventType.GET_COUNTRY_CONFIGURATIONS.cacheTime)
                .setData(null)
                .setPriority(true)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetCountryConfigurations(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}

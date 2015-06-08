package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.configs.GetImageResolutions;
import com.mobile.newFramework.utils.EventType;

public class GetImageResolutionsTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.ci/mobapi/v1.7/main/imageresolutions")
                .setCache(EventType.GET_RESOLUTIONS.cacheTime)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetImageResolutions(requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}

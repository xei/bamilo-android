package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.campaign.GetCampaign;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;

public class GetCampaignTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("campaign_slug", "deals-of-the-day");
        requestBundle = new RequestBundle.Builder()
                .setUrl("http://www.jumia.com.ng/mobapi/v1.7/campaign/get/")
                .setCache(EventType.GET_API_INFO.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetCampaign(requestBundle, this).execute();
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

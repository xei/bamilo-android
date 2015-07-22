package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;

public class GetCatalogFilteredTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Filters
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "1");
        data.put("maxitems", "24");
        data.put("sort", "newest");
        data.put("dir", "desc");
        // Bundle
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.com.ng/mobapi/v1.7/wedding/")
//                .setUrl("https://www.jumia.ci/mobapi/v1.7/search/?q=buogogiogio&sort=popularity&maxitems=24&page=1") //To test featured box
                .setCache(EventType.GET_PRODUCTS_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        //new GetCatalogFiltered(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCatalogFiltered);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        Print.d("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}

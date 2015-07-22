package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.Section;
import com.mobile.newFramework.objects.configs.Sections;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

public class GetApiInformationTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.ci/mobapi/v1.7/main/md5/")
                .setCache(EventType.GET_API_INFO.cacheTime)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getApiInformation);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        Print.d("############# MD5 SECTIONS #############");
        Sections sections = ((ApiInformation) response.getMetadata().getData()).getSections();
        assertNotNull(sections);
        for (Section section : sections) {
            assertNotNull(section);
            Print.d("SECTION: " + section.getName() + " " + section.getMd5());
        }
        Print.d("######################################");
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

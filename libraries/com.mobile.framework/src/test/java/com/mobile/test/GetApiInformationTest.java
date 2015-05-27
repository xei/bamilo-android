package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.objects.ApiInformation;
import com.mobile.newFramework.objects.Section;
import com.mobile.newFramework.objects.Sections;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.configs.GetApiInformation;

public class GetApiInformationTest extends BaseTestCase {

    RequestBundle requestBundle;

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
        System.out.println("TEST REQUEST");
        new GetApiInformation(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        System.out.println("############# MD5 SECTIONS #############");
        Sections sections = ((ApiInformation) response.metadata.getData()).getSections();
        assertNotNull(sections);
        for (Section section : sections) {
            assertNotNull(section);
            System.out.println("SECTION: " + section.getName() + " " + section.getMd5());
        }
        System.out.println("######################################");
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

package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.configs.GetAvailableCountries;

public class GetAvailableCountriesTest extends BaseTestCase {

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://cld.pt/dl/download/40e5154f-fbe5-4b4d-abad-bef636d089e5/jtmobapi_040215")
                //.setUrl("https://www.jumia.com/mobapi/availablecountries/")
                .setCache(EventType.GET_GLOBAL_CONFIGURATIONS.cacheTime)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetAvailableCountries(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        System.out.println("############# COUNTRIES #############");
        AvailableCountries countries = (AvailableCountries) response.metadata.getData();
        assertNotNull(countries);
        for (CountryObject country : countries) {
            assertNotNull(country);
            System.out.println(country.toString());
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

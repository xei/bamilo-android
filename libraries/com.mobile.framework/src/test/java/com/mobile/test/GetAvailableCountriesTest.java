package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.configs.GetAvailableCountries;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

public class GetAvailableCountriesTest extends BaseTestCase {

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
        Print.d("TEST REQUEST");
        new GetAvailableCountries(requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        Print.d("############# COUNTRIES #############");
        AvailableCountries countries = (AvailableCountries) response.getMetadata().getData();
        assertNotNull(countries);
        for (CountryObject country : countries) {
            assertNotNull(country);
            Print.d(country.toString());
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

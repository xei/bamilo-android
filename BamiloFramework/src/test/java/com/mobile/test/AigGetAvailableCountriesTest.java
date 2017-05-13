package com.mobile.test;

import com.mobile.service.objects.configs.AvailableCountries;
import com.mobile.service.objects.configs.CountryObject;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

public class AigGetAvailableCountriesTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_GLOBAL_CONFIGURATIONS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getAvailableCountries;
    }

    @Override
    public String getUrl() {
        //return "https://jumia.com/mobapi/availablecountries/";
        return "http://shareptcmobile.eu.pn/jtmobapi/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = null;

        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());
        AvailableCountries availableCountries = (AvailableCountries) response.getMetadata().getData();
        assertNotNull("Available countries is null",availableCountries);
        for (CountryObject country : availableCountries){
            assertNotNull("Country Name is null",country.getCountryName());
            assertNotNull("Country Flag is null",country.getCountryFlag());
            assertNotNull("Country Iso is null",country.getCountryIso());
            assertNotNull("Country Url is null",country.getCountryUrl());
            assertNotNull("Country is Live is null",country.isCountryIsLive());
        }



        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

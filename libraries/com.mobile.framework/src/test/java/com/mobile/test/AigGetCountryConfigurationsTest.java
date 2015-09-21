package com.mobile.test;


import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetCountryConfigurationsTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_COUNTRY_CONFIGURATIONS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getCountryConfigurations;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/main/getconfigurations";
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

        CountryConfigs country = (CountryConfigs) response.getMetadata().getData();

        assertNotNull("Country is null", country);
        assertNotNull("Country Currency Symbol is null", country.getCurrencySymbol());
        assertNotNull("Country CS Email is null", country.getCsEmail());
        assertNotNull("Country Currency Iso is null", country.getCurrencyIso());
        assertNotNull("Country Decimal Separator is null", country.getDecimalsSep());
//        assertNotNull("Country Language Name is null", country.getLangName());

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

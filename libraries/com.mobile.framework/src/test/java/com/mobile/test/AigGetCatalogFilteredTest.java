package com.mobile.test;

import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetCatalogFilteredTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCTS_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getCatalogFiltered;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/wedding/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "1");
        data.put("maxitems", "24");
        data.put("sort", "newest");
        data.put("dir", "desc");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        Catalog catalog = (Catalog) response.getMetadata().getData();

        assertNotNull("Catalog is null", catalog.getCatalogPage());
        assertNotNull("Catalog Page is null", catalog.getCatalogPage());
        assertNotNull("Catalog Page Name is null", catalog.getCatalogPage().getName());
        //assertNotNull("Catalog featured box is null", catalog.getFeaturedBox());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

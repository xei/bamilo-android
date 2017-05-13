package com.mobile.test;

import com.mobile.service.objects.category.Categories;
import com.mobile.service.objects.category.Category;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetCategoriesPaginatedTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_CATEGORIES_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getCategoriesPaginated;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/catalog/categories/";
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

        Categories categories = (Categories) response.getMetadata().getData();
        assertNotNull("Categories is null", categories);
        for (Category category : categories){
            assertNotNull("Category is null", category);
            assertNotNull("Category Name is null", category.getName());
        }
        //assertNotNull("Catalog featured box is null", catalog.getFeaturedBox());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

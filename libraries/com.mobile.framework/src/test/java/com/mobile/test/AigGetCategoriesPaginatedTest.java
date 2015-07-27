package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.category.Category;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

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
        return "http://www.jumia.com.ng/mobapi/v1.7/catalog/categories/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("paginate", "1");
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

package com.mobile.test;


import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AigGetFormsIndexTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.INIT_FORMS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getFormsIndex;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/forms/index/";
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
        FormsIndex form = (FormsIndex) response.getMetadata().getData();
        assertNotNull("Form is null", form);
        Iterator iterator = form.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            FormData form_data = (FormData) pair.getValue();
            assertNotNull("Form Data is null", form_data);
            //assertNotNull("Form Data ID is null", form_data.getId());
            assertNotNull("Form Data URL is null", form_data.getUrl());
        }

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

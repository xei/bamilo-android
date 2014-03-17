package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.GetLoginFormHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class GetLoginFormTests extends FrameworkServiceTests {
    private static String TAG=GetLoginFormTests.class.getSimpleName();
    private Boolean processed=false;
    
    
    @SmallTest
    public void testGetLoginForm() throws Throwable {
        Log.i(TAG,"STARTING LOGIN TEST ");
        sendRequest(new GetLoginFormHelper(), new IResponseCallback() {
            
            @Override
            public void onRequestError(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
                processed = true;
            }
            
            @Override
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
                processed = true;
            }
        });
        while (!processed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

}

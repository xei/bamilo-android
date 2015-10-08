//package com.mobile.test;
//
//import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
//import com.mobile.newFramework.pojo.BaseResponse;
//import com.mobile.newFramework.rest.interfaces.AigApiInterface;
//import com.mobile.newFramework.utils.EventType;
//import com.mobile.newFramework.utils.output.Print;
//import com.mobile.test.suites.AigMobApiNigeriaTestSuite;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class AigGetNativeCheckoutAvailabilityTest extends AigTestCase {
//
//    @Override
//    public EventType getEventType() {
//        return EventType.NATIVE_CHECKOUT_AVAILABLE;
//    }
//
//    @Override
//    public String getAigInterfaceName() {
//        return AigApiInterface.getNativeCheckoutAvailable;
//    }
//
//    @Override
//    public String getUrl() {
//        return AigMobApiNigeriaTestSuite.HOST+"/main/getconfig/module/configuration/key/native_checkout_mobile_api/";
//    }
//
//    @Override
//    public Map<String, String> getData() {
//        HashMap<String, String> data = null;
//
//        return data;
//    }
//
//    @Override
//    public void testResponse(BaseResponse response) {
//        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
//        assertTrue("Success is true", response.hadSuccess());
//
//        SuperNativeCheckoutAvailability checkoutAvailability = (SuperNativeCheckoutAvailability) response.getMetadata().getData();
//
//        assertNotNull("Native Checkout is null", checkoutAvailability);
//        assertNotNull("Native Checkout Available is null", checkoutAvailability.isAvailable());
//        //assertFalse("Success is false", response.hadSuccess());
//        //Assert.fail("Success is false");
//    }
//}

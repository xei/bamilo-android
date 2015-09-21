package com.mobile.test;

import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.Order;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetOrdersListTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_MY_ORDERS_LIST_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getOrdersList;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/order/list/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("per_page", "18");
        data.put("page", "1");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        MyOrder superOrder = (MyOrder) response.getMetadata().getData();

        assertNotNull("Orders List is null", superOrder);
        assertNotNull("Orders List Nr Pages is null", superOrder.getNumPages());

        for (Order order : superOrder.getOrders()){
            assertNotNull("Order is null", order);
            assertNotNull("Order Number is null", order.getmOrderNumber());
            assertNotNull("Order Total is null", order.getmOrderTotal());
            assertNotNull("Order Date is null", order.getmDate());
        }

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}

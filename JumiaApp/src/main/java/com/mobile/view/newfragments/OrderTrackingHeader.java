package com.mobile.view.newfragments;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.view.R;

import java.util.ArrayList;

import static com.mobile.view.R.id.product;
import static com.mobile.view.newfragments.OrderStateType.PROGRESS_ITEM_ACTIVE;

/**
 * Created by User on 3/12/2017.
 */

public class OrderTrackingHeader {
    public ArrayList<OrderStateStep> steps;
    private Context context;
    private ViewGroup parent;

    public OrderTrackingHeader(Context context, ViewGroup parent)
    {
        this.context = context;
        this.parent = parent;
        steps = new ArrayList<>();
    }

    public void addStep(OrderStateStep step, int placement)
    {
        steps.add(placement-1, step);
    }

    public void createHeader(Context context)
    {
        OrderStatus orderStatus = getOrderStatusFromOrderProducts(null);

        OrderStateStep step = new OrderStateStep("ثبت سفارش", 1, R.drawable.order_list_grey, R.drawable.order_list_green,
                R.drawable.order_list_white, 0);
        addStep(step, 1);

        step = new OrderStateStep("در حال تامین", 2, R.drawable.order_package_grey, R.drawable.order_package_green,
                R.drawable.order_package_white, 0);
        addStep(step, 2);

        step = new OrderStateStep("ارسال شد", 3, R.drawable.order_truck_grey, R.drawable.order_truck_green,
                R.drawable.order_truck_white, R.drawable.order_truck_red);
        addStep(step, 3);

        steps.get(0).setType(OrderStateType.PROGRESS_ITEM_PENDING);
        steps.get(1).setType(OrderStateType.PROGRESS_ITEM_PENDING);
        steps.get(2).setType(OrderStateType.PROGRESS_ITEM_PENDING);

        switch (orderStatus) {
            case ORDER_STATUS_NEW_ORDER: {
                steps.get(0).setType(OrderStateType.PROGRESS_ITEM_ACTIVE);
            }
            break;

            case ORDER_STATUS_REGISTERED: {
                steps.get(0).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(1).setType(OrderStateType.PROGRESS_ITEM_ACTIVE);
            }
            break;

            case ORDER_STATUS_IN_PROGRESS: {
                steps.get(0).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(1).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(2).setType(OrderStateType.PROGRESS_ITEM_ACTIVE);
            }
            break;

            case ORDER_STATUS_DELIVERED: {
                steps.get(0).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(1).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(2).setType(OrderStateType.PROGRESS_ITEM_DONE);
                steps.get(2).setLabel("تحویل شد");
            }
            break;

            case ORDER_STATUS_CANCELLED: {
                steps.get(0).setType(OrderStateType.PROGRESS_ITEM_PENDING);
                steps.get(1).setType(OrderStateType.PROGRESS_ITEM_PENDING);
                steps.get(2).setType(OrderStateType.PROGRESS_ITEM_ERROR);
                steps.get(2).setLabel("لغو شد");
            }
            break;

            default:
                break;
        }


        View step1 = parent.findViewById(R.id.order_step1);
        steps.get(0).getStep(context, step1);

        View step2 = parent.findViewById(R.id.order_step2);
        steps.get(1).getStep(context, step2);

        View step3 = parent.findViewById(R.id.order_step3);
        steps.get(2).getStep(context, step3);

        TextView step1Title = (TextView) parent.findViewById(R.id.order_step1_title);
        step1Title.setText(steps.get(0).getLabel());
        step1Title.setTextColor(context.getResources().getColor(steps.get(0).getLabelColor()));
        step1Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, steps.get(0).getLabelSize());


        TextView step2Title = (TextView) parent.findViewById(R.id.order_step2_title);
        step2Title.setText(steps.get(1).getLabel());
        step2Title.setTextColor(context.getResources().getColor(steps.get(1).getLabelColor()));
        step2Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, steps.get(1).getLabelSize());

        TextView step3Title = (TextView) parent.findViewById(R.id.order_step3_title);
        step3Title.setText(steps.get(2).getLabel());
        step3Title.setTextColor(context.getResources().getColor(steps.get(2).getLabelColor()));
        step3Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, steps.get(2).getLabelSize());




    }

    private OrderStatus getOrderItemStatus(String status)
    {
        if(status.contains("سفارش جدید")) { //0
            return OrderStatus.ORDER_STATUS_NEW_ORDER;
            } else if(status.contains("در حال پردازش")) { //1
                return OrderStatus.ORDER_STATUS_REGISTERED;
            } else if(status.contains("ارسال شد")) { //2
                return OrderStatus.ORDER_STATUS_IN_PROGRESS;
            } else if(status.contains("تحویل داده شد")) { //3
                return OrderStatus.ORDER_STATUS_DELIVERED;
            } else if(status.contains("لغو شده")) { //4
                return OrderStatus.ORDER_STATUS_CANCELLED;
            }

        return OrderStatus.ORDER_STATUS_NONE;
    }

    private OrderStatus getOrderStatusFromOrderProducts(ArrayList<OrderTrackerItem> products) {
        int currentOrderStep = 99;
        if (products == null)
        {
            return OrderStatus.ORDER_STATUS_NONE;
        }
        for(OrderTrackerItem product : products) {
            currentOrderStep = Math.min(currentOrderStep, getOrderItemStatus(product.getStatus()).getValue());
        }
        return OrderStatus.fromInt(currentOrderStep);
    }
}

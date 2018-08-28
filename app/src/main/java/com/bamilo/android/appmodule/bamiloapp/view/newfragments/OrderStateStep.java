package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bamilo.android.R;


/**
 * Created by User on 3/12/2017.
 */

public class OrderStateStep {

    private OrderStateType type;
    private String label;
    private int pending_icon;
    private int active_icon;
    private int done_icon;
    private int error_icon;
    private int placement;

    OrderStateStep(String label, int placement, int pending_icon, int active_icon, int done_icon, int error_icon) {
        this.label = label;
        this.placement = placement;
        this.pending_icon = pending_icon;
        this.active_icon = active_icon;
        this.done_icon = done_icon;
        this.error_icon = error_icon;
    }

    public int getIcon() {
        int result = pending_icon;
        switch (type) {
            case PROGRESS_ITEM_ACTIVE:
                result = active_icon;
                break;
            case PROGRESS_ITEM_DONE:
                result = done_icon;
                break;
            case PROGRESS_ITEM_ERROR:
                result = error_icon;
                break;
            case PROGRESS_ITEM_PENDING:
                result = pending_icon;
                break;
        }
        return result;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getLabelColor() {
        int result = R.color.grey_pending;
        switch (type) {
            case PROGRESS_ITEM_ACTIVE:
                result = R.color.checkout_circle_green;
                break;
            case PROGRESS_ITEM_DONE:
                result = R.color.checkout_circle_green;
                break;
            case PROGRESS_ITEM_ERROR:
                result = R.color.red_error;
                break;
            case PROGRESS_ITEM_PENDING:
                result = R.color.grey_pending;
                break;
        }
        return result;
    }

    public int getLabelSize() {
        int result = 10;
        switch (type) {
            case PROGRESS_ITEM_ACTIVE:
                result = 12;
                break;
            case PROGRESS_ITEM_ERROR:
                result = 12;
                break;
            case PROGRESS_ITEM_DONE:
                result = 10;
                break;
            case PROGRESS_ITEM_PENDING:
                result = 10;
                break;
        }
        return result;
    }

    public int getBackground() {
        int result = R.drawable.new_order_steps_pending;
        switch (type) {
            case PROGRESS_ITEM_ACTIVE:
                result = R.drawable.new_order_steps_active;
                break;
            case PROGRESS_ITEM_DONE:
                result = R.drawable.new_order_steps_done;
                break;
            case PROGRESS_ITEM_ERROR:
                result = R.drawable.new_order_steps_error;
                break;
            case PROGRESS_ITEM_PENDING:
                result = R.drawable.new_order_steps_pending;
                break;
        }
        return result;
    }

    public void setType(OrderStateType type) {
        this.type = type;
    }

    public View getStep(Context context, View view) {

        ((ImageView) view.findViewById(R.id.icon)).setImageResource(getIcon());
        view.findViewById(R.id.background).setBackgroundResource(getBackground());


        return view;

    }


}

enum OrderStateType {
    PROGRESS_ITEM_PENDING(0),
    PROGRESS_ITEM_ACTIVE(1),
    PROGRESS_ITEM_DONE(2),
    PROGRESS_ITEM_ERROR(3);


    private final int value;

    private OrderStateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum OrderStatus {
    ORDER_STATUS_NONE(-1),
    ORDER_STATUS_NEW_ORDER(0),
    ORDER_STATUS_REGISTERED(1),
    ORDER_STATUS_IN_PROGRESS(2),
    ORDER_STATUS_DELIVERED(3),
    ORDER_STATUS_CANCELLED(4);

    private final int value;

    private OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OrderStatus fromInt(int i) {
        OrderStatus enumVal = OrderStatus.values()[i + 1];

        return enumVal;
    }

}
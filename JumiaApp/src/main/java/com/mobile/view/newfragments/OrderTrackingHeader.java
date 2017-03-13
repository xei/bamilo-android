package com.mobile.view.newfragments;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by User on 3/12/2017.
 */

public class OrderTrackingHeader {
    public ArrayList<OrderStateStep> steps;

    public OrderTrackingHeader()
    {
        steps = new ArrayList<>();
    }

    public void addStep(OrderStateStep step, int placement)
    {
        steps.add(placement-1, step);
    }

    public View createHeader(Context context, RelativeLayout holder)
    {
        for (OrderStateStep step: steps) {
            View stepView = step.getStep(context, holder);
            holder.addView(stepView);
        }




        return holder;
    }
}

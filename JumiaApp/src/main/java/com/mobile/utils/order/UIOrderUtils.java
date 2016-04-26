package com.mobile.utils.order;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

/**
 * Created by spereira on 4/26/16.
 */
public class UIOrderUtils {

    @NonNull
    public static View createOrderItem(@NonNull Context context, @NonNull OrderTrackerItem item, @NonNull ViewGroup parent) {
        // Get view
        View view = LayoutInflater.from(context).inflate(R.layout._def_order_return_step_item, parent, false);
        // Set view
        ImageView image = (ImageView) view.findViewById(R.id.image_view);
        View progress = view.findViewById(R.id.image_loading_progress);
        RocketImageLoader.instance.loadImage(item.getImageUrl(), image, progress, R.drawable.no_image_large);
        ((TextView) view.findViewById(R.id.order_return_item_text_brand)).setText(item.getBrandName());
        ((TextView) view.findViewById(R.id.order_return_item_text_name)).setText(item.getName());
        ((TextView) view.findViewById(R.id.order_return_item_text_order)).setText("Order: XXXXX");
        ((TextView) view.findViewById(R.id.order_return_item_text_quantity)).setText(context.getString(R.string.qty_placeholder, item.getQuantity()));
        view.findViewById(R.id.order_return_item_text_return_quantity);
        return view;
    }

}

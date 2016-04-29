package com.mobile.utils.order;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.orders.OrderActions;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.pojo.ICustomView;
import com.mobile.utils.dialogfragments.DialogQuantityListFragment;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.lang.ref.WeakReference;

/**
 * Class used to represent the base of teasers.
 */
public class ReturnOrderViewHolder implements ICustomView {

    public final static int MIN_RETURN_QUANTITY = 1;

    public final View itemView;

    public ReturnOrderViewHolder(@NonNull Context context, @NonNull String mOrder, @NonNull OrderTrackerItem item) {
        // Get view
        View view = View.inflate(context, R.layout._def_order_return_step_item, null);
        // Save view
        itemView = view;
        // Image
        ImageView image = (ImageView) view.findViewById(R.id.image_view);
        View progress = view.findViewById(R.id.image_loading_progress);
        RocketImageLoader.instance.loadImage(item.getImageUrl(), image, progress, R.drawable.no_image_large);
        // Brand
        ((TextView) view.findViewById(R.id.order_return_item_text_brand)).setText(item.getBrandName());
        // Name
        ((TextView) view.findViewById(R.id.order_return_item_text_name)).setText(item.getName());
        // Order quantity
        ((TextView) view.findViewById(R.id.order_return_item_text_quantity)).setText(context.getString(R.string.quantity_placeholder, item.getQuantity()));
        // Order
        ((TextView) view.findViewById(R.id.order_return_item_text_order)).setText(context.getString(R.string.order_number_placeholder, mOrder));
    }

    /**
     * Get the quantity from view holder.
     *
     * @return String - 1 or value from text view
     */
    public String getQuantityFromReturnViewHolder() {
        String quantity = String.valueOf(MIN_RETURN_QUANTITY);
        if (itemView != null) {
            TextView textView = (TextView) itemView.findViewById(R.id.order_return_item_button_quantity);
            quantity = textView.getText().toString();
        }
        return quantity;
    }

    /**
     * Load the saved quantity
     */
    public void setQuantityInReturnViewHolder(@Nullable String value) {
        if (itemView != null && TextUtils.isNotEmpty(value)) {
            ((TextView) itemView.findViewById(R.id.order_return_item_button_quantity)).setText(value);
        }
    }

    /**
     * Set the quantity button.
     */
    @SuppressWarnings("deprecation")
    public void showQuantityButton(@NonNull final WeakReference<BaseActivity> weakActivity, final OrderActions action) {
        // Show quantity views
        TextView text = (TextView) itemView.findViewById(R.id.order_return_item_text_return_quantity);
        final TextView button = (TextView) itemView.findViewById(R.id.order_return_item_button_quantity);
        UIUtils.showOrHideViews(View.VISIBLE, button, text);
        // Set min
        button.setText(String.valueOf(MIN_RETURN_QUANTITY));
        // Get max items to return
        final int max = action != null ? action.getReturnableQuantity() : MIN_RETURN_QUANTITY;
        // Validate max
        button.setEnabled(max > 1);
        if (button.isEnabled()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickQuantityButton(weakActivity, button, max);
                }
            });
        } else {
            if (DeviceInfoHelper.isPosJellyBean()) {
                button.setBackground(null);
            } else {
                button.setBackgroundDrawable(null);
            }
        }
    }

    /**
     * Process the click on quantity button from an item
     */
    private void onClickQuantityButton(@NonNull WeakReference<BaseActivity> weakActivity, @NonNull final TextView button, int max) { // TODO
        // Get current value from view
        int current = Integer.valueOf(button.getText().toString());
        // Create dialog with max and current value
        DialogQuantityListFragment.newInstance(weakActivity, R.string.choose_quantity, max, current)
                .addOnItemClickListener(new DialogQuantityListFragment.OnDialogListListener() {
                    @Override
                    public void onDialogListItemSelect(AdapterView<?> adapterView, View view, int position, long id) {
                        // Save the selected value
                        String quantity = adapterView.getAdapter().getItem(position).toString();
                        button.setText(quantity);
                    }

                    @Override
                    public void onDismiss() {
                        // ...
                    }
                })
                .show();
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    @Nullable
    public String save() {
        return getQuantityFromReturnViewHolder();
    }

    @Override
    public void load(@Nullable String value) {
        setQuantityInReturnViewHolder(value);
    }

    @Override
    public View getView() {
        return itemView;
    }
}
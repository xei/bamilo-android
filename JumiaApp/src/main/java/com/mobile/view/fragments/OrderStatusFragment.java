package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.checkout.GetOrderStatusHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ProductListViewHolder;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used ...
 * @author spereira
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = OrderStatusFragment.class.getSimpleName();

    private String mOrderNumber;
    private ViewGroup mInfoView;
    private ViewGroup mPaymentView;
    private ViewGroup mShippingView;
    private ViewGroup mBillingView;
    private ViewGroup mOrderItems;

    /**
     * Get instance
     */
    public static OrderStatusFragment getInstance(Bundle bundle) {
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        orderStatusFragment.setArguments(bundle);
        return orderStatusFragment;
    }

    /**
     * Empty constructor
     */
    public OrderStatusFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyOrders,
                R.layout._def_order_status_fragment,
                R.string.order_status_label,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data
        savedInstanceState = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }

        mOrderNumber = "300065329";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get title view
        ((TextView) view.findViewById(R.id.order_status_title)).setText(getString(R.string.order_number, mOrderNumber));
        // Get info view
        mInfoView = (ViewGroup) view.findViewById(R.id.order_status_info);
        // Get payment container
        mPaymentView = (ViewGroup) view.findViewById(R.id.order_status_payment);
        // Get shipping address container
        mShippingView = (ViewGroup) view.findViewById(R.id.order_status_address_shipping);
        // Get billing address container
        mBillingView = (ViewGroup) view.findViewById(R.id.order_status_address_billing);
        // Get order items container
        mOrderItems = (ViewGroup) view.findViewById(R.id.order_status_items);

        // Validate the sate
        if (TextUtils.isNotEmpty(mOrderNumber)) {
            triggerOrder(mOrderNumber);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE SATE");
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * ###### LAYOUTS ######
     */

    private void showOrderStatus(@NonNull OrderStatus orderStatus) {
        // Set info
        int size = orderStatus.getTotalProducts();
        String text = getResources().getQuantityString(R.plurals.numberOfItems, size, size);
        ((TextView) mInfoView.findViewById(R.id.order_status_number_of_items)).setText(text);
        ((TextView) mInfoView.findViewById(R.id.order_status_total)).setText(CurrencyFormatter.formatCurrency(orderStatus.getTotal()));
        // Set payment
        ((TextView) mPaymentView.findViewById(R.id.order_status_payment_type)).setText(orderStatus.getPaymentMethod());
        ((TextView) mPaymentView.findViewById(R.id.order_status_payment_name)).setText(orderStatus.getPaymentMethod());
        // Set shipping
        showAddress(mShippingView, getString(R.string.shipping), orderStatus.getShippingAddress());
        // Set billing
        showAddress(mBillingView, getString(R.string.billing), orderStatus.getBillingAddress());
        // Set items
        showOrderItems(mOrderItems, orderStatus.getItems());
        // Show container
        showFragmentContentContainer();
    }

    private void showAddress(@NonNull ViewGroup view, @Nullable String title, @Nullable Address address) {
        if (address != null) {
            ((TextView) view.findViewById(R.id.order_status_address_item_title)).setText(title);
            String name = getString(R.string.first_and_second, address.getFirstName(), address.getLastName());
            ((TextView) view.findViewById(R.id.order_status_address_item_name)).setText(name);
            ((TextView) view.findViewById(R.id.order_status_address_item_street)).setText(address.getAddress());
            ((TextView) view.findViewById(R.id.order_status_address_item_region)).setText(address.getCity());
            ((TextView) view.findViewById(R.id.order_status_address_item_postcode)).setText(address.getPostcode());
            ((TextView) view.findViewById(R.id.order_status_address_item_phone)).setText(address.getPhone());
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void showOrderItems(@NonNull ViewGroup group, @Nullable ArrayList<OrderTrackerItem> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            LayoutInflater inflater = LayoutInflater.from(group.getContext());
            for (OrderTrackerItem item : items) {
                // Create new layout item
                ProductListViewHolder holder = new ProductListViewHolder(inflater.inflate(R.layout.gen_product_list, group, false));
                // Set name
                holder.name.setText(item.getName());
                // Set brand
                holder.brand.setText(item.getBrand());
                // Set is new image
                holder.recent.setSelected(item.isNew());
                // Set image
                RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
                // Add to parent
                group.addView(holder.itemView);
            }
        }
    }

    /*
     * ###### TRIGGERS ######
     */

    private void triggerOrder(String orderNr) {
        triggerContentEvent(new GetOrderStatusHelper(), GetOrderStatusHelper.createBundle(orderNr), this);
    }

    /*
     * ###### RESPONSES ######
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        // Get order status
        OrderStatus order = (OrderStatus) baseResponse.getMetadata().getData();
        if(order != null) {
            // Show order
            showOrderStatus(order);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Specific errors
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        // Validate generic errors
        if (!super.handleErrorEvent(baseResponse)) {
            // Show retry
            showFragmentErrorRetry();
        }
    }
}

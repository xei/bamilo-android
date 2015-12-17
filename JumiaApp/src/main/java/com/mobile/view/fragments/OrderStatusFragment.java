package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.checkout.GetOrderStatusHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.OrderedProductViewHolder;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show the order status.
 * @author spereira
 */
public class OrderStatusFragment extends BaseFragment implements IResponseCallback {

    public static final String TAG = OrderStatusFragment.class.getSimpleName();

    private String mOrderNumber;
    private ViewGroup mInfoView;
    private ViewGroup mPaymentView;
    private ViewGroup mShippingView;
    private ViewGroup mBillingView;
    private ViewGroup mOrderItems;
    private String mOrderDate;

    /**
     * Get instance
     */
    public static OrderStatusFragment getInstance(Bundle bundle) {
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        orderStatusFragment.setArguments(bundle);
        return orderStatusFragment;
    }

    /**
     * Constructor as nested fragment, called from {@link MyOrdersFragment#}.
     */
    public static OrderStatusFragment getNestedInstance(Bundle bundle) {
        OrderStatusFragment orderStatusFragment = getInstance(bundle);
        orderStatusFragment.isNestedFragment = true;
        return orderStatusFragment;
    }

    /**
     * Empty constructor
     */
    public OrderStatusFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.order_status_fragment,
                R.string.order_status_label,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data
        savedInstanceState = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
            mOrderDate = savedInstanceState.getString(ConstantsIntentExtra.ARG_2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Set title view
        ((TextView) view.findViewById(R.id.order_status_title_nr)).setText(getString(R.string.order_number, mOrderNumber));
        ((TextView) view.findViewById(R.id.order_status_title_date)).setText(mOrderDate);
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
        // Validate state
        onValidateState();
    }

    private void onValidateState() {
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
        outState.putString(ConstantsIntentExtra.ARG_1, mOrderNumber);
        outState.putString(ConstantsIntentExtra.ARG_2, mOrderDate);
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
        showOrderInfo(mInfoView, orderStatus);
        // Set payment
        showPayment(mPaymentView, orderStatus);
        // Set shipping
        showAddress(mShippingView, getString(R.string.shipping), orderStatus.getShippingAddress());
        // Set billing
        showAddress(mBillingView, getString(R.string.billing), orderStatus.getBillingAddress());
        // Set items
        showOrderItems(mOrderItems, orderStatus.getItems());
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Show order info
     */
    private void showOrderInfo(@NonNull ViewGroup group, @NonNull OrderStatus orderStatus) {
        int size = orderStatus.getTotalProducts();
        String text = getResources().getQuantityString(R.plurals.numberOfItems, size, size);
        ((TextView) group.findViewById(R.id.order_status_number_of_items)).setText(text);
        ((TextView) group.findViewById(R.id.order_status_total)).setText(CurrencyFormatter.formatCurrency(orderStatus.getTotal()));
    }

    /**
     * Show payment info
     */
    private void showPayment(@NonNull ViewGroup group, @NonNull OrderStatus orderStatus) {
        ((TextView) group.findViewById(R.id.order_status_payment_type)).setText(orderStatus.getPaymentType());
        ((TextView) group.findViewById(R.id.order_status_payment_name)).setText(orderStatus.getPaymentName());
    }

    /**
     * Show Address
     */
    private void showAddress(@NonNull ViewGroup view, @Nullable String title, @Nullable Address address) {
        if (address != null) {
            ((TextView) view.findViewById(R.id.order_status_address_item_title)).setText(title);
            String name = getString(R.string.first_and_second_placeholders, address.getFirstName(), address.getLastName());
            ((TextView) view.findViewById(R.id.order_status_address_item_name)).setText(name);
            ((TextView) view.findViewById(R.id.order_status_address_item_street)).setText(address.getAddress());
            ((TextView) view.findViewById(R.id.order_status_address_item_region)).setText(address.getCity());
            if(TextUtils.isNotEmpty(address.getPostcode())) {
                TextView postCode = (TextView) view.findViewById(R.id.order_status_address_item_postcode);
                postCode.setText(address.getPostcode());
                postCode.setVisibility(View.VISIBLE);
            }
            ((TextView) view.findViewById(R.id.order_status_address_item_phone)).setText(address.getPhone());
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Show items
     */
    private void showOrderItems(@NonNull ViewGroup group, @Nullable ArrayList<OrderTrackerItem> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            LayoutInflater inflater = LayoutInflater.from(group.getContext());
            for (OrderTrackerItem item : items) {
                // Create new layout item
                OrderedProductViewHolder holder = new OrderedProductViewHolder(inflater.inflate(R.layout.gen_order_list, group, false));
                // Set image
                RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
                // Set name
                holder.name.setText(item.getName());
                // Set brand
                holder.brand.setText(item.getBrand());
                // Set quantity
                holder.quantity.setText(getString(R.string.qty_placeholder, item.getQuantity()));
                // Set price
                ProductUtils.setPriceRules(item, holder.price, holder.discount);
                // Set delivery
                holder.delivery.setText(item.getDelivery());
                // Set order status
                String status = String.format(getContext().getString(R.string.order_status_date), item.getStatus(), item.getUpdateDate());
                holder.status.setText(Html.fromHtml(status));
                // Set reorder button
                holder.reorder.setTag(R.id.target_simple_sku, item.getSku());
                holder.reorder.setOnClickListener(this);
                // Add to parent
                group.addView(holder.itemView);
            }
        }
    }

    /*
     * ###### LISTENERS ######
     */

    @Override
    public void onClick(View view) {
        // Case reorder
        if(view.getId() == R.id.order_status_item_button_reorder)  onClickReOrder(view);
        // Case default
        else super.onClick(view);
    }

    private void onClickReOrder(View view) {
        // Get sku from view
        String simpleSku = (String) view.getTag(R.id.target_simple_sku);
        // Validate sku
        if(TextUtils.isNotEmpty(simpleSku)) {
            triggerAddItemToCart(simpleSku.split("-")[0],  simpleSku);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onValidateState();
    }

    /*
     * ###### TRIGGERS ######
     */

    private void triggerAddItemToCart(String sku, String simpleSKU) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku, simpleSKU), this);
    }

    private void triggerOrder(String orderNr) {
        EventTask task = isNestedFragment ? EventTask.ACTION_TASK : EventTask.NORMAL_TASK;
        triggerContentEvent(new GetOrderStatusHelper(), GetOrderStatusHelper.createBundle(orderNr, task), this);
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
        // Handle success
        super.handleSuccessEvent(baseResponse);
        // Validate
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                hideActivityProgress();
                break;
            case TRACK_ORDER_EVENT:
                // Get order status
                OrderStatus order = (OrderStatus) baseResponse.getContentData();
                if (order != null) {
                    showOrderStatus(order);
                } else {
                    showFragmentErrorRetry();
                }
                break;
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
        // Validate generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event type
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                hideActivityProgress();
                break;
            case TRACK_ORDER_EVENT:
                showFragmentErrorRetry();
                break;
        }

    }
}

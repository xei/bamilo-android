package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.checkout.GetOrderStatusHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.orders.OrderReturn;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.utils.ui.OrderedProductViewHolder;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show the order status.
 * @author spereira
 */
public class OrderStatusFragment extends BaseFragmentSwitcher implements IResponseCallback {

    public static final String TAG = OrderStatusFragment.class.getSimpleName();

    private OrderStatus mOrder;

    private String mOrderNumber;
    private ViewGroup mInfoView;
    private ViewGroup mPaymentView;
    private ViewGroup mShippingView;
    private ViewGroup mBillingView;
    private ViewGroup mOrderItems;
    private String mOrderDate;

    /**
     * Constructor as nested fragment, called from {@link MyOrdersFragment#}.
     */
    public static OrderStatusFragment getNestedInstance(Bundle bundle) {
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        orderStatusFragment.setArguments(bundle);
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
            mOrder = savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
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
        // Get return items container
        mReturnItemsButton = view.findViewById(R.id.return_selected_button);
        mReturnItemsContainer = view.findViewById(R.id.return_button_container);
        mReturnItemsButton.setOnClickListener(this);

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
        outState.putParcelable(ConstantsIntentExtra.DATA, mOrder);
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
            String name = getString(R.string.first_space_second_placeholder, address.getFirstName(), address.getLastName());
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

            //
            if(displayReturnSelected()){ // Check whether there is more then 2 items with action online return type
                UIUtils.setVisibility(mReturnItemsContainer, true);
            } else {
                UIUtils.setVisibility(mReturnItemsContainer, false);
            }

            for (final OrderTrackerItem item : items) {
                // Create new layout item
                final OrderedProductViewHolder holder = new OrderedProductViewHolder(inflater.inflate(R.layout.gen_order_list, group, false));
                if(item.isEligibleToReturn() && CollectionUtils.isNotEmpty(item.getOrderActions())){
                    UIUtils.setVisibility(holder.returnOrder, true);
                    if(item.getOrderActions().get(IntConstants.DEFAULT_POSITION).isCallToReturn()){
                        holder.returnOrder.setText(getString(R.string.call_return_label));
                    } else {
                        holder.returnOrder.setText(getString(R.string.return_label));
                        if(displayReturnSelected()){
                            UIUtils.setVisibility(holder.orderCheckbox, true);
                            holder.orderCheckbox.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.orderCheckbox.setChecked(item.isCheckedForAction());
                                }
                            });
                            holder.orderCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    item.setCheckedForAction(isChecked);
                                }
                            });
                        }

                    }

                    holder.returnOrder.setTag(R.id.target_simple_sku, item.getSku());
                    holder.returnOrder.setOnClickListener(this);

                }

                // Set image
                RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
                // Set name
                holder.name.setText(item.getName());
                // Set brand
                holder.brand.setText(item.getBrandName());
                // Set quantity
                holder.quantity.setText(getString(R.string.qty_placeholder, item.getQuantity()));
                // Set price
                UIProductUtils.setPriceRules(item, holder.price, holder.discount);
                // Set delivery
                holder.delivery.setText(item.getDelivery());

                if(CollectionUtils.isNotEmpty(item.getOrderReturns())){
                    String orderReturns = "";
                    for (OrderReturn orderReturn : item.getOrderReturns() ) {
                        orderReturns+=String.format(getString(R.string.items_returned_on),
                                orderReturn.getQuantity(), orderReturn.getDate());
                    }

                    holder.itemReturns.setText(orderReturns);
                    holder.itemReturnsLabel.setVisibility(View.VISIBLE);
                    holder.itemReturns.setVisibility(View.VISIBLE);
                }

                // Set order status
                String status = String.format(getContext().getString(R.string.order_status_date), item.getStatus(), item.getUpdateDate());
                holder.status.setText(Html.fromHtml(status));
                // Set reorder button
                holder.reorder.setTag(R.id.target_simple_sku, item.getSku());
                holder.reorder.setOnClickListener(this);
                //View set tag
                holder.itemView.setTag(R.id.target_simple_sku, item.getSku());
                holder.itemView.setOnClickListener(this);
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
        if(view.getId() == R.id.order_status_item_button_reorder){
            onClickReOrder(view);
        }
        //case order item
        else if(view.getId() == R.id.order_list_item){
            goToProductDetails(view);
        }
        //case return item
        else if(view.getId() == R.id.order_status_item_button_return){
            onClickReturn(view);
        }
        //case return all selected items
        else if(view.getId() == R.id.return_selected_button){
            onClickReturnSelected();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickReturnSelected() {
        if (!validateReturnAllSelected()) {
            // TODO : Get target link
            new UISwitcher(getBaseActivity(), FragmentType.ORDER_RETURN_CONDITIONS)
                    .addContentId(TargetLink.getIdFromTargetLink("static_page::terms_mobile"))
                    .noBackStack()
                    .run();
        } else {
            showWarningErrorMessage(getString(R.string.warning_no_items_selected));
        }
    }

    /**
     * Validate if there is any item selected.
     */
    private boolean validateReturnAllSelected(){
        for (OrderTrackerItem item  : mOrder.getItems()) {
            if(item.isCheckedForAction()){
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether there is more then 2 items with action online return type
      */
    private boolean displayReturnSelected(){
        int count = 0;
        for (OrderTrackerItem item  : mOrder.getItems()) {
            if (CollectionUtils.isNotEmpty(item.getOrderActions()) && !item.getOrderActions().get(IntConstants.DEFAULT_POSITION).isCallToReturn()) {
                if(count++ > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private OrderTrackerItem getOrderItem(final String sku){
        for (OrderTrackerItem  item : mOrder.getItems()) {
            if(TextUtils.equals(sku, item.getSku())){
               return item;
            }
        }
        return null;
    }


    /**
     * Open PDV
     */
    private void goToProductDetails(final View view) {
        String sku = TargetLink.getSkuFromSimple((String) view.getTag(R.id.target_simple_sku));
        if (TextUtils.isNotEmpty(sku)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            showUnexpectedErrorWarning();
        }
    }

    /**
     * Re-order
     */
    private void onClickReOrder(final View view) {
        // Get sku from view
        String simpleSku = (String) view.getTag(R.id.target_simple_sku);
        // Validate sku
        if(TextUtils.isNotEmpty(simpleSku)) {
            triggerAddItemToCart(simpleSku);
        }
    }

    private void onClickReturn(final View view){
        // Get sku from view
        String simpleSku = (String) view.getTag(R.id.target_simple_sku);
        // Validate sku
        if(TextUtils.isNotEmpty(simpleSku)) {
            final OrderTrackerItem item = getOrderItem(simpleSku);
           if(item.getOrderActions().get(IntConstants.DEFAULT_POSITION).isCallToReturn()){
                // Go To Next Call to return step
               Bundle bundle = new Bundle();
               bundle.putParcelable(ConstantsIntentExtra.DATA, item);
               bundle.putString(ConstantsIntentExtra.ARG_1, mOrder.getId());
               getBaseActivity().onSwitchFragment(FragmentType.ORDER_RETURN_CALL, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else {
                // Go To Next return step
            }
        }

    }

    @Override
    protected void onClickRetryButton(final View view) {
        super.onClickRetryButton(view);
        onValidateState();
    }

    /*
     * ###### TRIGGERS ######
     */

    private void triggerAddItemToCart(String simpleSKU) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(simpleSKU), this);
    }

    private void triggerOrder(String orderNr) {
        EventTask task = isNestedFragment ? EventTask.ACTION_TASK : EventTask.NORMAL_TASK;
        triggerContentEvent(new GetOrderStatusHelper(), GetOrderStatusHelper.createBundle(orderNr, task), this);
    }

    private void checkState(OrderStatus order){
        if(mOrder != null && order != null && TextUtils.equals(mOrder.getId(),order.getId())){
            for (int i = 0; i < mOrder.getItems().size(); i++){
                final OrderTrackerItem itemSaved = mOrder.getItems().get(i);
                final OrderTrackerItem itemNew = order.getItems().get(i);
                if(TextUtils.equals(itemSaved.getSku(), itemNew.getSku())){
                    itemNew.setCheckedForAction(itemSaved.isCheckedForAction());
                }
            }
        }
        mOrder = order;
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
                checkState(order);
                if (mOrder != null) {
                    showOrderStatus(mOrder);
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

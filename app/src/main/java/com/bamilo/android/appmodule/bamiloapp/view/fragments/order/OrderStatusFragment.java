package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetOrderStatusHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.orders.OrderActions;
import com.bamilo.android.framework.service.objects.orders.OrderStatus;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.PersianDateTimeConverter;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragmentAutoState;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.OrderTrackingHeader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;

/**
 * Class used to show the order status.
 *
 * @author spereira
 */
public class OrderStatusFragment extends BaseFragmentAutoState implements IResponseCallback {

    public static final String TAG = OrderStatusFragment.class.getSimpleName();

    private OrderStatus mOrder;

    private String mOrderNumber;
    private ViewGroup mInfoView;
    private ViewGroup mPaymentView;
    private ViewGroup mShippingView;
    private ViewGroup mBillingView;
    private ViewGroup mOrderItems;
    private ViewGroup mHeaderView;
    private View mReturnItemsButton;
    private View mReturnItemsContainer;
    private String mOrderDate;

    private HashSet<Integer> mSelectedItemsToReturn = new HashSet<>();

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
        // Get data
        savedInstanceState = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
            mOrderDate = savedInstanceState.getString(ConstantsIntentExtra.ARG_2);
            mOrder = savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
            ArrayList<Integer> selectedItems = savedInstanceState.getIntegerArrayList(ConstantsIntentExtra.ARG_3);
            if (CollectionUtils.isNotEmpty(selectedItems)) {
                mSelectedItemsToReturn = new HashSet<>(selectedItems);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeaderView = view.findViewById(R.id.order_tracking_header);
        mInfoView = view.findViewById(R.id.order_status_info);
        mOrderItems = view.findViewById(R.id.order_details);

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConstantsIntentExtra.ARG_1, mOrderNumber);
        outState.putString(ConstantsIntentExtra.ARG_2, mOrderDate);
        outState.putParcelable(ConstantsIntentExtra.DATA, mOrder);
        outState.putIntegerArrayList(ConstantsIntentExtra.ARG_3, new ArrayList<>(mSelectedItemsToReturn));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * ###### LAYOUTS ######
     */

    private void showOrderStatus(@NonNull OrderStatus orderStatus) {
        showHeader(mHeaderView, orderStatus);
        // Set info
        showOrderInfo(mInfoView, orderStatus);
        // Set payment
        showOrderItems(mOrderItems, orderStatus.getItems());
        // Show container
        showFragmentContentContainer();
    }

    private void showHeader(ViewGroup mHeaderView, OrderStatus orderStatus) {
        OrderTrackingHeader mOrderTrackingHeader = new OrderTrackingHeader(getContext(), mHeaderView);
        mOrderTrackingHeader.createHeader(getContext(), orderStatus.getItems());

    }

    /**
     * Show order info
     */
    private void showOrderInfo(@NonNull ViewGroup group, @NonNull OrderStatus orderStatus) {

        ((TextView) group.findViewById(R.id.order_status_number_value)).setText(orderStatus.getId());

        String date = orderStatus.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(Locale.US);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        PersianDateTimeConverter pd = PersianDateTimeConverter.valueOf(new GregorianCalendar(year, month, day, 0, 0, 0));

        int quantity = 0;
        for (OrderTrackerItem item : orderStatus.getItems()) {
            quantity += Integer.parseInt(item.getQuantity());
        }

        ((TextView) group.findViewById(R.id.order_status_date_value)).setText(pd.toString());
        ((TextView) group.findViewById(R.id.order_status_total_value)).setText(CurrencyFormatter.formatCurrency(orderStatus.getTotal()));
        ((TextView) group.findViewById(R.id.order_status_quantity_value)).setText(quantity + " عدد");
        ((TextView) group.findViewById(R.id.order_status_payment_value)).setText(orderStatus.getPaymentName());
        ((TextView) group.findViewById(R.id.order_status_address_value)).setText(orderStatus.getShippingAddress().getAddressString());
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
            if (TextUtils.isNotEmpty(address.getPostcode())) {
                TextView postCode = view.findViewById(R.id.order_status_address_item_postcode);
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

            for (int i = 0; i < items.size(); i++) {
                final OrderTrackerItem item = items.get(i);
                // Create new layout item
                View view = inflater.inflate(R.layout.order_list_item, group, false);
                ((TextView) view.findViewById(R.id.order_item_state)).setText(item.getStatus());
                ((TextView) view.findViewById(R.id.order_item_name)).setText(item.getName());
                ((TextView) view.findViewById(R.id.order_item_quantity)).setText("تعداد: " + item.getQuantity());
                ((TextView) view.findViewById(R.id.order_item_price)).setText(CurrencyFormatter.formatCurrency(item.getPrice()));
                ImageManager.getInstance().loadImage(item.getImageUrl(),
                        view.findViewById(R.id.order_item_image), null, R.drawable.no_image_large, false);
                // Add to parent
                group.addView(view);
            }
        }
    }

    /*
     * ###### LISTENERS ######
     */

    @Override
    public void onClick(View view) {
        // Case reorder
        if (view.getId() == R.id.order_status_item_button_reorder) {
            onClickReOrder(view);
        }
        //case order item
        else if (view.getId() == R.id.order_list_item) {
            goToProductDetails(view);
        }
        //case return item
        else if (view.getId() == R.id.order_status_item_button_return) {
            onClickReturn(view);
        }
        //case return all selected items
        else if (view.getId() == R.id.return_selected_button) {
            onClickReturnMultiSelected();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickReturnMultiSelected() {
        // Validate
        if (CollectionUtils.isNotEmpty(mSelectedItemsToReturn) && CollectionUtils.isNotEmpty(mOrder.getItems())) {
            ArrayList<OrderTrackerItem> items = new ArrayList<>();
            // Get target link from order
            String page = null;
            // Save selected items
            for (Integer position : mSelectedItemsToReturn) {
                // Save item
                OrderTrackerItem item = mOrder.getItems().get(position);
                items.add(item);
                // Save target
                OrderActions action = item.getDefaultOrderAction();
                // Get page conditions
                if (TextUtils.isEmpty(page) && action != null && TextUtils.isNotEmpty(action.getTarget())) {
                    page = TargetLink.getIdFromTargetLink(action.getTarget());
                }
            }
            // Goto order return conditions
            goToReturnConditionsStep(mOrderNumber, page, items);
        } else {
            showWarningErrorMessage(getString(R.string.warning_no_items_selected));
        }
    }

    /**
     * Check whether there is more then 2 items with action online return type
     */
    private boolean displayReturnSelected() {
        int count = 0;
        for (OrderTrackerItem item : mOrder.getItems()) {
            if (CollectionUtils.isNotEmpty(item.getOrderActions()) && !item.getOrderActions().get(IntConstants.DEFAULT_POSITION).isCallToReturn()) {
                if (++count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private OrderTrackerItem getOrderItem(final String sku) {
        for (OrderTrackerItem item : mOrder.getItems()) {
            if (TextUtils.equals(sku, item.getSku())) {
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
        if (TextUtils.isNotEmpty(simpleSku)) {
            triggerAddItemToCart(simpleSku);
        }
    }

    private void onClickReturn(final View view) {
        // Get sku from view
        String simpleSku = (String) view.getTag(R.id.target_simple_sku);
        // Validate sku
        if (TextUtils.isNotEmpty(simpleSku)) {
            OrderTrackerItem item = getOrderItem(simpleSku);
            // Validate order and action
            if (item != null && item.getDefaultOrderAction() != null) {
                // Get action
                OrderActions action = item.getDefaultOrderAction();
                // Case action: call to return
                if (action.isCallToReturn()) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ConstantsIntentExtra.DATA, item);
                    bundle.putString(ConstantsIntentExtra.ARG_1, mOrder.getId());
                    getBaseActivity().onSwitchFragment(FragmentType.ORDER_RETURN_CALL, bundle, FragmentController.ADD_TO_BACK_STACK);
                }
                // Case action: return
                else if (TextUtils.isNotEmpty(action.getTarget())) {
                    // Get target link from Order
                    String page = TargetLink.getIdFromTargetLink(action.getTarget());
                    // Goto order return conditions
                    ArrayList<OrderTrackerItem> items = new ArrayList<>();
                    items.add(item);
                    goToReturnConditionsStep(mOrderNumber, page, items);
                } else {
                    showUnexpectedErrorWarning();
                }
            } else {
                showUnexpectedErrorWarning();
            }
        }
    }

    private void goToReturnConditionsStep(String order, String page, ArrayList<OrderTrackerItem> items) {
        onSwitchTo(FragmentType.ORDER_RETURN_CONDITIONS)
                .addId(page)
                .addArray(items)
                .add(ConstantsIntentExtra.ARG_1, order)
                .noBackStack()
                .run();
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

    private void checkState(OrderStatus order) {
        if (mOrder != null && order != null && TextUtils.equals(mOrder.getId(), order.getId())) {
            for (int i = 0; i < mOrder.getItems().size(); i++) {
                final OrderTrackerItem itemSaved = mOrder.getItems().get(i);
                final OrderTrackerItem itemNew = order.getItems().get(i);
                if (TextUtils.equals(itemSaved.getSku(), itemNew.getSku())) {
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
            return;
        }
        // Handle success
        super.handleSuccessEvent(baseResponse);
        // Validate
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
            return;
        }
        // Validate generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event type
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

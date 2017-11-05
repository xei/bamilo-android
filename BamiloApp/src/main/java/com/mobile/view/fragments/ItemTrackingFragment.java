package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.adapters.ItemTrackingListAdapter;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.checkout.GetOrderStatusHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.objects.orders.PackageItem;
import com.mobile.service.objects.orders.PackagedOrder;
import com.mobile.service.objects.product.pojo.ProductComplete;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created on 10/28/2017.
 */

public class ItemTrackingFragment extends BaseFragment implements IResponseCallback, ItemTrackingListAdapter.OnItemTrackingListClickListener {

    private RecyclerView rvItemsList;
    private SwipeRefreshLayout srlItemTrackingStatus;
    private String orderNumber;
    private PackagedOrder packagedOrder;
    private ItemTrackingListAdapter mAdapter;

    public ItemTrackingFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.fragment_item_tracking,
                R.string.order_status_label,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        orderNumber = args.getString(ConstantsIntentExtra.ORDER_NUMBER);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvItemsList = (RecyclerView) view.findViewById(R.id.rvItemsList);
        rvItemsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        srlItemTrackingStatus = (SwipeRefreshLayout) view.findViewById(R.id.srlItemTrackingStatus);
        srlItemTrackingStatus.setColorSchemeResources(R.color.appBar);
        srlItemTrackingStatus.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderInfo(orderNumber);
            }
        });
        if (mAdapter == null) {
            loadOrderInfo(orderNumber);
        } else {
            mAdapter.setOnItemTrackingListClickListener(this);
            rvItemsList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadOrderInfo(String orderNumber) {
        EventTask task = isNestedFragment ? EventTask.ACTION_TASK : EventTask.NORMAL_TASK;
        srlItemTrackingStatus.setRefreshing(true);
        triggerContentEventNoLoading(new GetOrderStatusHelper(), GetOrderStatusHelper.createBundle(orderNumber, task), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onClickRetryButton(View view) {
        loadOrderInfo(orderNumber);
    }

    @Override
    protected void onClickContinueButton() {
        loadOrderInfo(orderNumber);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        hideActivityProgress();
        showFragmentContentContainer();
        srlItemTrackingStatus.setRefreshing(false);
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
            case TRACK_ORDER_EVENT:
                // Get order status
                PackagedOrder order = (PackagedOrder) baseResponse.getContentData();
                this.packagedOrder = order;
                if (packagedOrder != null) {
                    showOrderStatus(packagedOrder);
                } else {
                    showFragmentErrorRetry();
                }
                break;
            case GET_PRODUCT_DETAIL:
                ProductComplete product = (ProductComplete) baseResponse.getContentData();

                Bundle bundle = new Bundle();
                bundle.putParcelable(ConstantsIntentExtra.PRODUCT, product);
                bundle.putInt(ConstantsIntentExtra.PRODUCT_INFO_POS, R.string.rat_rev);
                bundle.putString(ConstantsIntentExtra.FLAG_1, product.getBrandName());
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_INFO, bundle, false);
                break;
        }
    }

    private void showOrderStatus(PackagedOrder packagedOrder) {
        mAdapter = new ItemTrackingListAdapter(packagedOrder);
        mAdapter.setOnItemTrackingListClickListener(this);
        rvItemsList.setAdapter(mAdapter);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        srlItemTrackingStatus.setRefreshing(false);
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

                break;
            case TRACK_ORDER_EVENT:
                showFragmentErrorRetry();
                break;
        }
    }

    @Override
    public void onReviewButtonClicked(View v, PackageItem item) {
        triggerLoadProduct(item.getSku());
    }

    @Override
    public void onProductThumbClickListener(View v, PackageItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, item.getSku());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, String.format("%s %s", item.getBrand(), item.getName()));
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        // Goto PDV
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void triggerLoadProduct(String sku) {
        triggerContentEvent(new GetProductHelper(), GetProductHelper.createBundle(sku, null), this);
        //recommendManager = new RecommendManager();
        //sendRecommend();
    }
}

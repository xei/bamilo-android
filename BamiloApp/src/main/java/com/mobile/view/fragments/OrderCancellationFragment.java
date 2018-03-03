package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bamilo.apicore.di.modules.OrderCancellationModule;
import com.bamilo.apicore.presentation.OrderCancellationPresenter;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.apicore.service.model.data.ordercancellation.CancellationRequestBody;
import com.bamilo.apicore.view.OrderCancellationView;
import com.google.gson.Gson;
import com.mobile.adapters.OrderCancellationListAdapter;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mohsen on 1/28/18.
 */

public class OrderCancellationFragment extends BaseFragment implements OrderCancellationView {
    private CompleteOrder completeOrder;
    private String selectedItemId;

    @Inject
    Gson mGson;

    @Inject
    OrderCancellationPresenter presenter;
    private CancellationRequestBody cancellationRequestBody;

    public OrderCancellationFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.fragment_order_cancellation,
                R.string.submit_cancellation_request_title,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Inject Dependencies by Dagger
         * **/
        BamiloApplication
                .getComponent()
                .plus(new OrderCancellationModule(this))
                .inject(this);

        Bundle args = getArguments();
        String completeOrderJson = args.getString(ConstantsIntentExtra.COMPLETE_ORDER);
        if (completeOrderJson == null) {
            getBaseActivity().onBackPressed();
        }
        completeOrder = mGson.fromJson(completeOrderJson, CompleteOrder.class);
        selectedItemId = args.getString(ConstantsIntentExtra.CANCELING_ORDER_ITEM_ID, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvCancellingItems = (RecyclerView) view.findViewById(R.id.rvItemsList);
        rvCancellingItems.setNestedScrollingEnabled(false);
        rvCancellingItems.setLayoutManager(new LinearLayoutManager(getContext()));
        final OrderCancellationListAdapter cancellationAdapter = new OrderCancellationListAdapter(completeOrder, selectedItemId);
        rvCancellingItems.setAdapter(cancellationAdapter);

        TextView tvRefundMessage, tvNoticeMessage;
        tvRefundMessage = (TextView) view.findViewById(R.id.tvRefundMessage);
        if (TextUtils.isNotEmpty(completeOrder.getCancellation().getRefundMessage())) {
            tvRefundMessage.setVisibility(View.VISIBLE);
            tvRefundMessage.setText(completeOrder.getCancellation().getRefundMessage());
        }

        tvNoticeMessage = (TextView) view.findViewById(R.id.tvNoticeMessage);
        if (TextUtils.isNotEmpty(completeOrder.getCancellation().getNoticeMessage())) {
            tvNoticeMessage.setVisibility(View.VISIBLE);
            tvNoticeMessage.setText(completeOrder.getCancellation().getNoticeMessage());
        }

        final EditText etDescription = (EditText) view.findViewById(R.id.etDescription);
        HoloFontLoader.applyDefaultFont(etDescription);

        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmitCancellation);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> cancelingItemsIds = cancellationAdapter.getCancelingItemsList();
                if (cancelingItemsIds.isEmpty()) {
                    showWarningErrorMessage(getString(R.string.order_cancellation_you_should_select_one_item));
                } else {
                    cancellationRequestBody = new CancellationRequestBody();
                    cancellationRequestBody.setOrderNumber(completeOrder.getOrderNumber());
                    String description = etDescription.getText().toString();
                    if (!description.isEmpty()) {
                        cancellationRequestBody.setDescription(description);
                    }
                    boolean ready = true;
                    List<CancellationRequestBody.Item> cancelingItems = new ArrayList<>();
                    int itemsWithoutReason = 0;
                    for (String itemId : cancelingItemsIds) {
                        String reasonId = cancellationAdapter.getSelectedItemCancellationReason(itemId);
                        if (reasonId == null) {
                            cancellationAdapter.notifyItemToSelectCancellationReason(itemId);
                            itemsWithoutReason++;
                            ready = false;
                        } else {
                            CancellationRequestBody.Item item = new CancellationRequestBody.Item();
                            item.setReasonId(reasonId);
                            item.setQuantity(cancellationAdapter.getCancelingItemQuantity(itemId));
                            item.setSimpleSku(cancellationAdapter.getPackageItemByItemId(itemId).getSimpleSku());
                            item.setItemId(itemId);
                            cancelingItems.add(item);
                        }
                    }
                    cancellationRequestBody.setItems(cancelingItems);
                    if (ready) {
                        presenter.submitCancellationRequest(NetworkConnectivity.isConnected(getContext()), cancellationRequestBody);
                    } else {
                        showWarningErrorMessage(getResources().getQuantityString(R.plurals.order_cancellation_please_select_cancellation_reason, itemsWithoutReason));
                    }
                }
            }
        });
    }

    @Override
    public void navigateToCancellationSuccessPage() {
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.COMPLETE_ORDER, mGson.toJson(completeOrder));
        args.putString(ConstantsIntentExtra.ORDER_CANCELLATION_REQUEST_BODY, mGson.toJson(cancellationRequestBody));

        getBaseActivity().onSwitchFragment(FragmentType.ORDER_CANCELLATION_SUCCESS, args, false);
    }

    @Override
    public void showMessage(EventType eventType, String message) {
        showWarningErrorMessage(message);
    }

    @Override
    public void showOfflineMessage(EventType eventType) {
        showNoNetworkWarning();
    }

    @Override
    public void showConnectionError(EventType eventType) {
        showUnexpectedErrorWarning();
    }

    @Override
    public void showServerError(EventType eventType, ServerResponse response) {
        if (response.getMessages() != null && CollectionUtils.isNotEmpty(response.getMessages().getErrors())) {
            showWarningErrorMessage(response.getMessages().getErrors().get(0).getMessage());
        } else {
            showUnexpectedErrorWarning();
        }
    }

    @Override
    public void toggleProgress(EventType eventType, boolean show) {
        if (show) {
            showFragmentLoading();
        } else {
            showFragmentContentContainer();
        }
    }

    @Override
    public void showRetry(EventType eventType) {
        showUnexpectedErrorWarning();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}

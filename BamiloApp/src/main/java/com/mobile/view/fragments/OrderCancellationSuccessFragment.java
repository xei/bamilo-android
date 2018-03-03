package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.apicore.service.model.data.itemtracking.CompleteOrder;
import com.bamilo.apicore.service.model.data.itemtracking.Package;
import com.bamilo.apicore.service.model.data.itemtracking.PackageItem;
import com.bamilo.apicore.service.model.data.ordercancellation.CancellationRequestBody;
import com.google.gson.Gson;
import com.mobile.adapters.OrderCancellationSuccessListAdapter;
import com.mobile.app.BamiloApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by mohsen on 1/31/18.
 */

public class OrderCancellationSuccessFragment extends BaseFragment {
    CompleteOrder completeOrder;
    CancellationRequestBody cancellationRequestBody;

    @Inject
    Gson mGson;

    public OrderCancellationSuccessFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ORDERS,
                R.layout.fragment_order_cancellation_success,
                R.string.order_status_label,
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
                .inject(this);

        Bundle args = getArguments();
        String completeOrderJson = args.getString(ConstantsIntentExtra.COMPLETE_ORDER);
        String requestBody = args.getString(ConstantsIntentExtra.ORDER_CANCELLATION_REQUEST_BODY);
        if (completeOrderJson == null || requestBody == null) {
            getBaseActivity().onBackPressed();
        }
        completeOrder = mGson.fromJson(completeOrderJson, CompleteOrder.class);
        cancellationRequestBody = mGson.fromJson(requestBody, CancellationRequestBody.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvCanceledItems = (RecyclerView) view.findViewById(R.id.rvCanceledItems);
        rvCanceledItems.setLayoutManager(new LinearLayoutManager(getContext()));
        List<OrderCancellationSuccessListAdapter.CanceledProductItem> productItems =
                extractCanceledItems(completeOrder, cancellationRequestBody);
        OrderCancellationSuccessListAdapter adapter = new OrderCancellationSuccessListAdapter(productItems);
        rvCanceledItems.setAdapter(adapter);
    }

    private List<OrderCancellationSuccessListAdapter.CanceledProductItem> extractCanceledItems(CompleteOrder completeOrder, CancellationRequestBody cancellationRequestBody) {
        List<OrderCancellationSuccessListAdapter.CanceledProductItem> productItems = new ArrayList<>();
        Map<String, Long> canceledItemsMap = new HashMap<>();
        for (CancellationRequestBody.Item cReqItem : cancellationRequestBody.getItems()) {
            canceledItemsMap.put(cReqItem.getItemId(), (long) cReqItem.getQuantity());
        }

        for (Package pkg : completeOrder.getPackages()) {
            for (PackageItem packageItem : pkg.getPackageItems()) {
                if (canceledItemsMap.containsKey(packageItem.getId())) {
                    OrderCancellationSuccessListAdapter.CanceledProductItem productItem =
                            new OrderCancellationSuccessListAdapter.CanceledProductItem();
                    productItem.setImage(packageItem.getImage());
                    productItem.setName(packageItem.getName());
                    productItem.setQuantity(canceledItemsMap.get(packageItem.getId()));
                    productItem.setSku(packageItem.getSku());
                    productItems.add(productItem);
                }
            }
        }
        return productItems;
    }


}

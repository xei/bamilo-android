package com.mobile.view.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.emarsys.predict.RecommendedItem;
import com.mobile.adapters.RecommendGridAdapter;
import com.mobile.adapters.RecommendItemsDiffUtilCallback;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBamiloFragment extends BaseFragment implements RecommendListCompletionHandler, RecommendGridAdapter.OnRecommendItemClickListener {

    private static final int RECOMMEND_LIST_COLUMN_COUNT = 2;
    private RecyclerView rvRecommendedItemsList;
    private SwipeRefreshLayout srlRecommendItemsList;
    private RecommendGridAdapter recommendGridAdapter;
    private List<Item> recommendListItems;
    private Map<String, List<Item>> recommendItemsMap;
    private String selectedCategory = null;
    private boolean useDiffUtil = true;

    public MyBamiloFragment() {
        super(true, R.layout.fragment_my_bamilo);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRecommendedItemsList = (RecyclerView) view.findViewById(R.id.rvRecommendedItemsList);
        srlRecommendItemsList = (SwipeRefreshLayout) view.findViewById(R.id.srlRecommendItemsList);
        srlRecommendItemsList.setNestedScrollingEnabled(true);
        srlRecommendItemsList.setClipToPadding(false);
        srlRecommendItemsList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                useDiffUtil = false;
                recommendItemsMap = new HashMap<>();
                requestForRecommendLists();
            }
        });
        rvRecommendedItemsList.setClipToPadding(false);
        getBaseActivity().enableSearchBar(true, rvRecommendedItemsList);
        srlRecommendItemsList.setProgressViewOffset(false, rvRecommendedItemsList.getPaddingTop(), srlRecommendItemsList.getProgressViewEndOffset());
        srlRecommendItemsList.setColorSchemeResources(R.color.appBar);
        rvRecommendedItemsList.setLayoutManager(new GridLayoutManager(getContext(), RECOMMEND_LIST_COLUMN_COUNT));
        recommendListItems = new ArrayList<>();
        recommendGridAdapter = new RecommendGridAdapter(recommendListItems, this);
        rvRecommendedItemsList.setAdapter(recommendGridAdapter);
        if (recommendItemsMap == null) {
            useDiffUtil = false;
            recommendItemsMap = new HashMap<>();
            requestForRecommendLists();
        } else {
            updateUi();
        }
    }

    private void requestForRecommendLists() {
        srlRecommendItemsList.setRefreshing(true);
        new RecommendManager().getEmarsysHomes(this, RecommendManager.createHomeExcludeItemListsMap(null));
    }

    @Override
    public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {
        srlRecommendItemsList.setRefreshing(false);
        List<Item> itemDataList = new ArrayList<>();
        for (RecommendedItem r : data) {
            itemDataList.add(new Item(r));
        }
        if (recommendItemsMap.get(category) == null) {
            recommendItemsMap.put(category, itemDataList);
        } else {
            recommendItemsMap.get(category).addAll(itemDataList);
        }
        updateUi();
    }

    private void updateUi() {
        List<Item> itemsToShow = new ArrayList<>();
        if (selectedCategory == null) {
            for (String key : recommendItemsMap.keySet()) {
                List<Item> tempList = recommendItemsMap.get(key);
                if (tempList != null) {
                    itemsToShow.addAll(tempList);
                }
            }
        } else {
            itemsToShow = recommendItemsMap.get(selectedCategory);
        }
        RecommendItemsDiffUtilCallback callback = new RecommendItemsDiffUtilCallback(itemsToShow, recommendListItems);
        if (useDiffUtil) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            recommendListItems.clear();
            recommendListItems.addAll(itemsToShow);
            result.dispatchUpdatesTo(recommendGridAdapter);
        } else {
            recommendListItems.clear();
            recommendListItems.addAll(itemsToShow);
            recommendGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRecommendItemClicked(View v, Item item, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, item.getItemID());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, String.format("%s %s", item.getBrand(), item.getTitle()));
        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        // Goto PDV
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
}

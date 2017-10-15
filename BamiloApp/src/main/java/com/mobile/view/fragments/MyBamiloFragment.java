package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.emarsys.predict.Error;
import com.emarsys.predict.RecommendedItem;
import com.mobile.adapters.ErrorItemRecyclerAdapter;
import com.mobile.adapters.RecommendGridAdapter;
import com.mobile.adapters.RecommendItemsDiffUtilCallback;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.util.ArrayList;
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
    private int recommendListScrollPosition;
    private boolean loadInProgress;
    private int scrolledAmount = 0;

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
                refreshRecommends();
            }
        });
        rvRecommendedItemsList.setClipToPadding(false);
        getBaseActivity().enableSearchBar(true, rvRecommendedItemsList);
        srlRecommendItemsList.setProgressViewOffset(false, rvRecommendedItemsList.getPaddingTop(), srlRecommendItemsList.getProgressViewEndOffset());
        srlRecommendItemsList.setColorSchemeResources(R.color.appBar);
        recommendListItems = new ArrayList<>();
        recommendGridAdapter = new RecommendGridAdapter(recommendListItems, this);
        recommendGridAdapter.setCategoryDropdownTitle(selectedCategory);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), RECOMMEND_LIST_COLUMN_COUNT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // determine that how many spans does the item need
                int span = 1;
                switch (recommendGridAdapter.getItemViewType(position)) {
                    case RecommendGridAdapter.TYPE_CATEGORIES_DROP_DOWN:
                        span = RECOMMEND_LIST_COLUMN_COUNT;
                        break;
                    case RecommendGridAdapter.TYPE_PRODUCT:
                        span = 1;
                        break;
                }
                return span;
            }
        });
        rvRecommendedItemsList.setLayoutManager(layoutManager);
        rvRecommendedItemsList.setAdapter(recommendGridAdapter);
        recommendGridAdapter.setOnCategoryDropdownClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
                List<String> items = new ArrayList<String>();
                items.add(getString(R.string.all_categories));
                items.addAll(recommendItemsMap.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.popup_menu_item, items) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.popup_menu_item, parent, false);
                        }
                        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
                        tv.setText(getItem(position));
                        if ((selectedCategory == null && position == 0)
                                || (getItem(position) != null && getItem(position).equals(selectedCategory))) {
                            convertView.findViewById(R.id.imgCheck).setVisibility(View.VISIBLE);
                        } else {
                            convertView.findViewById(R.id.imgCheck).setVisibility(View.INVISIBLE);
                        }
                        return convertView;
                    }
                };

                listPopupWindow.setAnchorView(v.findViewById(R.id.menuAnchor));
                listPopupWindow.setAdapter(adapter);
                listPopupWindow.setWidth(v.getWidth());
                listPopupWindow.setModal(true);
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        if (position == 0) {
                            selectedCategory = null;
                        } else {
                            List<String> categoryList = new ArrayList<String>(recommendItemsMap.keySet());
                            selectedCategory = categoryList.get(position - 1);
                        }
                        recommendGridAdapter
                                .setCategoryDropdownTitle(selectedCategory == null ? getString(R.string.all_categories) : selectedCategory);
                        updateUi();
                    }
                });
                listPopupWindow.show();
            }
        });
        if (recommendItemsMap == null) {
            recommendListScrollPosition = 0;
            useDiffUtil = false;
            recommendItemsMap = new HashMap<>();
            requestForRecommendLists();
        } else {
            updateUi();
        }
        rvRecommendedItemsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledAmount += dy;
                if (((GridLayoutManager) rvRecommendedItemsList.getLayoutManager()).findFirstVisibleItemPosition() == 0) {
                    if (rvRecommendedItemsList != null) {
                        getBaseActivity().syncSearchBarState(0);
                    }
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (rvRecommendedItemsList != null) {
                getBaseActivity().syncSearchBarState(scrolledAmount);
            }
        }
    }

    private void refreshRecommends() {
        useDiffUtil = false;
        recommendListScrollPosition = 0;
        recommendItemsMap = new HashMap<>();
        requestForRecommendLists();
    }

    @Override
    public void onPause() {
        super.onPause();
        recommendListScrollPosition = ((GridLayoutManager) rvRecommendedItemsList.getLayoutManager()).findFirstVisibleItemPosition();
    }

    @Override
    public void onStart() {
        super.onStart();
        srlRecommendItemsList.setRefreshing(loadInProgress);
        rvRecommendedItemsList.post(new Runnable() {
            @Override
            public void run() {
                if (recommendListScrollPosition == 0) {
                    ((GridLayoutManager) rvRecommendedItemsList.getLayoutManager()).scrollToPositionWithOffset(recommendListScrollPosition, rvRecommendedItemsList.getPaddingTop());
                } else {
                    rvRecommendedItemsList.getLayoutManager().scrollToPosition(recommendListScrollPosition);
                }
            }
        });
    }

    private void requestForRecommendLists() {
        loadInProgress = true;
        srlRecommendItemsList.setRefreshing(loadInProgress);
        new RecommendManager().getEmarsysHomes(this, new RecommendManager.EmarsysErrorCallback() {
            @Override
            public void onEmarsysRecommendError(Error error) {
                loadInProgress = false;
                srlRecommendItemsList.setRefreshing(loadInProgress);
                srlRecommendItemsList.setEnabled(false);
                rvRecommendedItemsList.setAdapter(new ErrorItemRecyclerAdapter(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        srlRecommendItemsList.setEnabled(true);
                        recommendListItems.clear();
                        recommendGridAdapter.notifyDataSetChanged();
                        rvRecommendedItemsList.setAdapter(recommendGridAdapter);
                        refreshRecommends();
                    }
                }, ErrorLayoutFactory.NO_NETWORK_LAYOUT));
            }
        }, RecommendManager.createHomeExcludeItemListsMap(null));
    }

    @Override
    public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {
        loadInProgress = false;
        String CATEGORY_DELIMITER = ">";
        String temp[] = category.split(CATEGORY_DELIMITER);
        category = temp[temp.length - 1];
        srlRecommendItemsList.setRefreshing(loadInProgress);
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
        if (itemsToShow == null) {
            return;
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

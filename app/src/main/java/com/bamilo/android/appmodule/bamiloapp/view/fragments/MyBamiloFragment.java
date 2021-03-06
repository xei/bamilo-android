//package com.bamilo.android.appmodule.bamiloapp.view.fragments;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Looper;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.util.DiffUtil;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.ListPopupWindow;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import com.bamilo.android.R;
//import com.bamilo.android.appmodule.bamiloapp.adapters.RecommendGridAdapter;
//import com.bamilo.android.appmodule.bamiloapp.adapters.RecommendItemsDiffUtilCallback;
//import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
//import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
//import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
//import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
//import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
//import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.Item;
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendManager;
//import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
//import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
//import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
//import com.bamilo.android.appmodule.modernbamilo.util.storage.SharedPreferencesHelperKt;
//import com.bamilo.android.framework.service.tracking.TrackingPage;
//import com.emarsys.predict.Error;
//import com.emarsys.predict.RecommendedItem;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MyBamiloFragment extends BaseFragment implements RecommendListCompletionHandler,
//        RecommendGridAdapter.OnRecommendItemClickListener {
//
//    private static final int RECOMMEND_LIST_COLUMN_COUNT = 2;
//    private static final String TRACKER_SCREEN_NAME = "MyBamilo";
//    private static final String TRACKER_LOGIC = "Home";
//    private static final int SMOOTH_SCROLL_LIMIT = 35;
//    private static final int BACK_TO_TOP_FAB_VISIBILITY_LIMIT = 17;
//    private static final int HOME_PAGES_COUNT = 6;
//    FloatingActionButton fabBackToTop;
//    private RecyclerView rvRecommendedItemsList;
//    private SwipeRefreshLayout srlRecommendItemsList;
//    private RecommendGridAdapter recommendGridAdapter;
//    private List<Item> recommendListItems;
//    private List<Item> allItemsShuffled;
//    private Map<String, List<Item>> recommendItemsMap;
//    private String selectedCategory = null;
//    private boolean useDiffUtil = true;
//    private int recommendListScrollPosition;
//    private boolean loadInProgress;
//    private int scrolledAmount = 0;
//    private int requestCompletionCount = 0;
//    private boolean isFragmentVisibleToUser = false;
//    private Error currentError;
//    private boolean screenTracked = false, timingTracked = false;
//
//    public MyBamiloFragment() {
//        super(true, R.layout.fragment_my_bamilo);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        fabBackToTop = view.findViewById(R.id.fabBackToTop);
//        fabBackToTop.setOnClickListener(v -> {
//            if (rvRecommendedItemsList != null && rvRecommendedItemsList
//                    .getAdapter() instanceof RecommendGridAdapter) {
//                GridLayoutManager glm = (GridLayoutManager) rvRecommendedItemsList
//                        .getLayoutManager();
//                int lastPosition = glm.findLastVisibleItemPosition();
//                if (lastPosition > SMOOTH_SCROLL_LIMIT) {
//                    rvRecommendedItemsList.scrollToPosition(SMOOTH_SCROLL_LIMIT);
//                }
//                rvRecommendedItemsList.smoothScrollToPosition(0);
//                getBaseActivity().syncSearchBarState(0);
//            }
//        });
//        rvRecommendedItemsList = view.findViewById(R.id.rvRecommendedItemsList);
//        srlRecommendItemsList = view.findViewById(R.id.srlRecommendItemsList);
//        srlRecommendItemsList.setNestedScrollingEnabled(true);
//        srlRecommendItemsList.setClipToPadding(false);
//        srlRecommendItemsList.setOnRefreshListener(this::refreshRecommends);
//        rvRecommendedItemsList.setClipToPadding(false);
//        getBaseActivity().enableSearchBar(true, rvRecommendedItemsList);
//        rvRecommendedItemsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (isContextExpired() || isOutOfUiThread()) {
//                    return;
//                }
//
//                if (isFragmentVisibleToUser) {
//                    getBaseActivity().onSearchBarScrolled(dy);
//                }
//                if (dy != 0) {
//                    if (dy < 0 && ((GridLayoutManager) recyclerView.getLayoutManager())
//                            .findLastVisibleItemPosition() >
//                            BACK_TO_TOP_FAB_VISIBILITY_LIMIT) {
//                        fabBackToTop.show();
//                    } else {
//                        fabBackToTop.hide();
//                    }
//                }
//            }
//        });
//        srlRecommendItemsList.setProgressViewOffset(false, rvRecommendedItemsList.getPaddingTop(),
//                srlRecommendItemsList.getProgressViewEndOffset());
//        srlRecommendItemsList.setColorSchemeResources(R.color.appBar);
//        recommendListItems = new ArrayList<>();
//        recommendGridAdapter = new RecommendGridAdapter(recommendListItems, this);
//        recommendGridAdapter.setCategoryDropdownTitle(selectedCategory);
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),
//                RECOMMEND_LIST_COLUMN_COUNT);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                // determine that how many spans does the item need
//                int span = 1;
//                switch (recommendGridAdapter.getItemViewType(position)) {
//                    case RecommendGridAdapter.TYPE_CATEGORIES_DROP_DOWN:
//                        span = RECOMMEND_LIST_COLUMN_COUNT;
//                        break;
//                    case RecommendGridAdapter.TYPE_PRODUCT:
//                        span = 1;
//                        break;
//                }
//                return span;
//            }
//        });
//        rvRecommendedItemsList.setLayoutManager(layoutManager);
//        rvRecommendedItemsList.setAdapter(recommendGridAdapter);
//        recommendGridAdapter.setOnCategoryDropdownClickListener(v -> {
//            if (isContextExpired() || isOutOfUiThread()) {
//                return;
//            }
//
//            final ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
//            List<String> items = new ArrayList<>();
//            items.add(getString(R.string.all_categories));
//            items.addAll(recommendItemsMap.keySet());
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                    R.layout.popup_menu_item, items) {
//                @NonNull
//                @Override
//                public View getView(int position, @Nullable View convertView,
//                        @NonNull ViewGroup parent) {
//                    if (convertView == null) {
//                        convertView = LayoutInflater.from(getContext())
//                                .inflate(R.layout.popup_menu_item, parent, false);
//                    }
//                    TextView tv = convertView.findViewById(android.R.id.text1);
//                    tv.setText(getItem(position));
//                    if ((selectedCategory == null && position == 0)
//                            || (getItem(position) != null && getItem(position)
//                            .equals(selectedCategory))) {
//                        convertView.findViewById(R.id.imgCheck).setVisibility(View.VISIBLE);
//                    } else {
//                        convertView.findViewById(R.id.imgCheck).setVisibility(View.INVISIBLE);
//                    }
//                    return convertView;
//                }
//            };
//
//            listPopupWindow.setAnchorView(v.findViewById(R.id.menuAnchor));
//            listPopupWindow.setAdapter(adapter);
//            listPopupWindow.setWidth(v.getWidth());
//            listPopupWindow.setModal(true);
//            listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
//                listPopupWindow.dismiss();
//                if (position == 0) {
//                    selectedCategory = null;
//                } else {
//                    List<String> categoryList = new ArrayList<>(
//                            recommendItemsMap.keySet());
//                    selectedCategory = categoryList.get(position - 1);
//                }
//                recommendGridAdapter
//                        .setCategoryDropdownTitle(selectedCategory == null ? getString(
//                                R.string.all_categories) : selectedCategory);
//                updateUi();
//            });
//            listPopupWindow.show();
//        });
//        if (recommendItemsMap == null) {
//            recommendListScrollPosition = 0;
//            useDiffUtil = false;
//            recommendItemsMap = new HashMap<>();
//            requestForRecommendLists();
//        } else {
//            updateUi();
//        }
//        rvRecommendedItemsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (isContextExpired() || isOutOfUiThread()) {
//                    return;
//                }
//
//                scrolledAmount += dy;
//                if (((GridLayoutManager) rvRecommendedItemsList.getLayoutManager())
//                        .findFirstVisibleItemPosition() == 0) {
//                    if (rvRecommendedItemsList != null) {
//                        getBaseActivity().syncSearchBarState(0);
//                    }
//                }
//            }
//        });
//    }
//
//    private boolean isContextExpired() {
//        return getContext() == null || getActivity() == null;
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if (isContextExpired() || isOutOfUiThread()) {
//            return;
//        }
//
//        isFragmentVisibleToUser = isVisibleToUser;
//        if (isVisibleToUser) {
//            if (rvRecommendedItemsList != null) {
//                if (!screenTracked) {
//                    BaseScreenModel screenModel = new BaseScreenModel(
//                            getString(TrackingPage.MY_BAMILO.getName()),
//                            getString(R.string.gaScreen), "", getLoadTime());
//                    TrackerManager.trackScreen(getContext(), screenModel, false);
//                    screenTracked = true;
//                }
//                getBaseActivity().syncSearchBarState(scrolledAmount);
//            }
//        }
//    }
//
//    private void refreshRecommends() {
//        useDiffUtil = false;
//        allItemsShuffled = null;
//        recommendListScrollPosition = 0;
//        requestCompletionCount = 0;
//        recommendItemsMap = new HashMap<>();
//        requestForRecommendLists();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        recommendListScrollPosition = ((GridLayoutManager) rvRecommendedItemsList
//                .getLayoutManager()).findFirstVisibleItemPosition();
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void onStart() {
//        super.onStart();
//        srlRecommendItemsList.setRefreshing(loadInProgress);
//        rvRecommendedItemsList.post(() -> {
//
//            if (isContextExpired() || isOutOfUiThread()) {
//                return;
//            }
//
//            if (recommendListScrollPosition == 0) {
//                ((GridLayoutManager) rvRecommendedItemsList.getLayoutManager())
//                        .scrollToPositionWithOffset(recommendListScrollPosition,
//                                rvRecommendedItemsList.getPaddingTop());
//            } else {
//                rvRecommendedItemsList.getLayoutManager()
//                        .scrollToPosition(recommendListScrollPosition);
//            }
//            if (recommendListScrollPosition >= BACK_TO_TOP_FAB_VISIBILITY_LIMIT) {
//                fabBackToTop.setVisibility(View.VISIBLE);
//            } else {
//                fabBackToTop.setVisibility(View.INVISIBLE);
//            }
//        });
//        if (currentError != null) {
//            handleError(currentError);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//    }
//
//    private void requestForRecommendLists() {
//        loadInProgress = true;
//        srlRecommendItemsList.setRefreshing(loadInProgress);
//        new RecommendManager().getEmarsysHomes(this, error -> {
//            currentError = error;
//            handleError(error);
//        }, RecommendManager.createHomeExcludeItemListsMap((List<String>[]) null), HOME_PAGES_COUNT);
//    }
//
//    private void handleError(Error error) {
//        if (isContextExpired() || isOutOfUiThread()) {
//            return;
//        }
//
//        loadInProgress = false;
//        fabBackToTop.hide();
//        getBaseActivity().syncSearchBarState(0);
//        Throwable cause = error.getCause();
//        while (true) {
//            if (cause == null || cause instanceof java.net.SocketTimeoutException) {
//                break;
//            } else {
//                cause = cause.getCause();
//            }
//        }
//        if (cause != null && cause instanceof java.net.SocketTimeoutException) {
//            showFragmentNetworkErrorRetry();
//        } else {
//            showFragmentNoNetworkRetry();
//        }
//    }
//
//
//    @Override
//    protected void onClickRetryButton(View view) {
////        showFragmentContentContainer();
//        srlRecommendItemsList.setEnabled(true);
//        recommendListItems.clear();
//        recommendGridAdapter.notifyDataSetChanged();
//        rvRecommendedItemsList.setAdapter(recommendGridAdapter);
//        refreshRecommends();
//    }
//
//    @Override
//    public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {
//        if (isContextExpired() || isOutOfUiThread()) {
//            return;
//        }
//
//        /*I check the view to know if the fragment is attached to the activity*/
//        if (!timingTracked && rvRecommendedItemsList != null) {
//            BaseScreenModel screenModel = new BaseScreenModel(
//                    getString(TrackingPage.MY_BAMILO.getName()), getString(R.string.gaScreen), "",
//                    getLoadTime());
//            TrackerManager.trackScreenTiming(getContext(), screenModel);
//            timingTracked = true;
//        }
//        currentError = null;
//        showFragmentContentContainer();
//        loadInProgress = false;
//        requestCompletionCount++;
//        String CATEGORY_DELIMITER = ">";
//        String temp[] = category.split(CATEGORY_DELIMITER);
//        category = temp[0];
//        srlRecommendItemsList.setRefreshing(loadInProgress);
//        List<Item> itemDataList = new ArrayList<>();
//        for (RecommendedItem r : data) {
//            itemDataList.add(new Item(r));
//        }
//        if (recommendItemsMap.get(category) == null) {
//            recommendItemsMap.put(category, itemDataList);
//        } else {
//            recommendItemsMap.get(category).addAll(itemDataList);
//        }
//        if (requestCompletionCount == HOME_PAGES_COUNT) {
//            updateUi();
//        }
//    }
//
//    private void updateUi() {
//        if (isContextExpired() || isOutOfUiThread()) {
//            return;
//        }
//
//        List<Item> itemsToShow;
//        if (selectedCategory == null) {
//            if (allItemsShuffled == null) {
//                allItemsShuffled = new ArrayList<>();
//                for (String key : recommendItemsMap.keySet()) {
//                    List<Item> tempList = recommendItemsMap.get(key);
//                    if (tempList != null) {
//                        allItemsShuffled.addAll(tempList);
//                    }
//                }
//                Collections.shuffle(allItemsShuffled);
//            }
//            itemsToShow = new ArrayList<>(allItemsShuffled);
//        } else {
//            itemsToShow = recommendItemsMap.get(selectedCategory);
//        }
//        if (itemsToShow == null) {
//            return;
//        }
//        RecommendItemsDiffUtilCallback callback = new RecommendItemsDiffUtilCallback(itemsToShow,
//                recommendListItems);
//        if (useDiffUtil) {
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
//            recommendListItems.clear();
//            recommendListItems.addAll(itemsToShow);
//            result.dispatchUpdatesTo(recommendGridAdapter);
//        } else {
//            recommendListItems.clear();
//            recommendListItems.addAll(itemsToShow);
//            recommendGridAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onRecommendItemClicked(View v, Item item, int position) {
//        SimpleEventModel sem = new SimpleEventModel();
//        sem.category = CategoryConstants.EMARSYS;
//        sem.action = EventActionKeys.CLICK;
//        sem.label = String.format("%s-%s", TRACKER_SCREEN_NAME, TRACKER_LOGIC);
//        sem.value = SimpleEventModel.NO_VALUE;
//
//        if (isContextExpired() || isOutOfUiThread()) {
//            return;
//        }
//
//        TrackerManager.trackEvent(getContext(), EventConstants.RecommendationTapped, sem);
//
//        SharedPreferencesHelperKt
//                .setHomePageItemsPurchaseTrack(v.getContext(), sem.category, sem.label, true);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.CONTENT_ID, item.getItemID());
//        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE,
//                String.format("%s %s", item.getBrand(), item.getTitle()));
//        bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
//        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
//        // Goto PDV
//        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
//                FragmentController.ADD_TO_BACK_STACK);
//    }
//
//    private boolean isOutOfUiThread() {
//        return Looper.getMainLooper().getThread() != Thread.currentThread();
//    }
//}

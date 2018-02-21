package com.mobile.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalSpaceItemDecoration;
import com.mobile.controllers.SearchDropDownAdapter;
import com.mobile.helpers.search.GetSearchSuggestionsHelper;
import com.mobile.helpers.search.SuggestionsStruct;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.service.database.SearchRecentQueriesTableHelper;
import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show recent searches
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class RecentSearchFragment extends BaseFragment implements IResponseCallback, OnProductViewHolderClickListener {
    
    private final static String TAG = RecentSearchFragment.class.getSimpleName();

    private Context mContext;

    private SearchDropDownAdapter mRecentSearchesAdapter;
    
    private ArrayList<Suggestion> mRecentSearches;
    
    private RecyclerView mRecentSearchesList;

    private TextView mClearAllButton;

    /**
     * Empty constructor
     */
    public RecentSearchFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.RECENT_SEARCHES,
                R.layout._def_recent_searches_fragment,
                R.string.recent_searches,
                ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        setAppContentLayout(view);
        init();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /**
     * ########### LAYOUT ########### 
     */
    
    private void init() {
        Print.d(TAG, "INIT");
        mContext = getBaseActivity();
        // Get Recent Searches
        Print.i(TAG, "LOAD RECENT SEARCHES");
        showFragmentLoading();
        new GetSearchSuggestionsHelper(this);
    }


    private void setAppContentLayout(View mainView) {
        mRecentSearchesList = (RecyclerView) mainView.findViewById(R.id.recentsearch_list);
        mRecentSearchesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentSearchesList.setNestedScrollingEnabled(false);
        mClearAllButton = (TextView) mainView.findViewById(R.id.recentsearch_clear_all);
        mClearAllButton.setVisibility(View.GONE);
        mClearAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivityProgress();

                SearchRecentQueriesTableHelper.deleteAllRecentQueries();
                // needed to update mRecentSearchesAdapter
                for (int i = mRecentSearches.size() - 1; i >= 0; i--) {
                    mRecentSearches.remove(i);
                }

                mRecentSearchesAdapter.notifyDataSetChanged();

                showEmpty();
                mClearAllButton.setVisibility(View.GONE);
                Print.d(TAG, "RECENT SEARCHES: " + mRecentSearches.size());
                mClearAllButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideActivityProgress();
                    }
                }, 300);
            }
        });
    }

    /**
     * Show empty content
     * 
     * @author Andre Lopes
     */
    protected void showEmpty() {
        showErrorFragment(ErrorLayoutFactory.NO_RECENT_SEARCHES_LAYOUT, this);
    }

    /**
     * ########### TRIGGERS ########### 
     */

    /**
     * ########### RESPONSES ########### 
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.d(TAG, "ON RESPONSE COMPLETE:");

        if (isOnStoppingProcess) return;

        super.handleSuccessEvent(baseResponse);
        
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
            Print.d(TAG, "ON RESPONSE COMPLETE: GET_SEARCH_SUGGESTIONS_EVENT");
            ArrayList<Suggestion> response = (SuggestionsStruct)baseResponse.getContentData();
            if (response != null) {
                mRecentSearches = response;
                if (!mRecentSearches.isEmpty()) {
                    mRecentSearchesAdapter = new SearchDropDownAdapter(mContext, mRecentSearches);
                    mRecentSearchesAdapter.setOnViewHolderClickListener(this);
                    mRecentSearchesList.setAdapter(mRecentSearchesAdapter);
                    mRecentSearchesList.addItemDecoration(new HorizontalSpaceItemDecoration(getContext(), R.drawable._gen_divider_horizontal_black_400));
                    mClearAllButton.setVisibility(View.VISIBLE);
                    showFragmentContentContainer();
                } else {
                    showEmpty();
                }
            } else {
                mRecentSearches = null;
                showEmpty();
            }

            Print.d(TAG, "RECENT SEARCHES: " + mRecentSearches.size());

            break;
        default:
            Print.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
    }    

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.d(TAG, "ON RESPONSE ERROR:");

        if (isOnStoppingProcess) return;
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
            Print.d(TAG, "ON RESPONSE ERROR: GET_SEARCH_SUGGESTIONS_EVENT");
            showFragmentContentContainer();
            break;
        default:
            Print.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }

    @Override
    public void onHeaderClick(String target, String title) {

    }

    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        Print.d(TAG, "SEARCH: CLICKED ITEM " + position);
        Suggestion selectedSuggestion = ((SearchDropDownAdapter) adapter).getItem(position);
        String text = selectedSuggestion.getResult();
        GetSearchSuggestionsHelper.saveSearchQuery(selectedSuggestion);
        switch (selectedSuggestion.getType()){
            case Suggestion.SUGGESTION_PRODUCT:
                getBaseActivity().showSearchProduct(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_SHOP_IN_SHOP:
                getBaseActivity().showSearchShopsInShop(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_CATEGORY:
                getBaseActivity().showSearchCategory(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_OTHER:
                getBaseActivity().showSearchOther(selectedSuggestion);
                break;
        }
        BamiloApplication.INSTANCE.setSearchedTerm(text);
    }

    @Override
    public void onViewHolderItemClick(View view, RecyclerView.Adapter<?> adapter, int position) {

    }
}

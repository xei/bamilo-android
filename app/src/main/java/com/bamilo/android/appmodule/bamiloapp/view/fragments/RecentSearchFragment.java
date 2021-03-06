package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.GetSearchSuggestionsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.SuggestionsStruct;
import android.widget.TextView;
import com.bamilo.android.framework.components.recycler.HorizontalSpaceItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.controllers.SearchDropDownAdapter;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
import com.bamilo.android.framework.service.objects.search.Suggestion;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.R;

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
    }

    /**
     * ########### LAYOUT ########### 
     */
    
    private void init() {
        mContext = getBaseActivity();
        // Get Recent Searches
        showFragmentLoading();
        new GetSearchSuggestionsHelper(this);
    }


    private void setAppContentLayout(View mainView) {
        mRecentSearchesList = mainView.findViewById(R.id.recentsearch_list);
        mRecentSearchesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentSearchesList.setNestedScrollingEnabled(false);
        mClearAllButton = mainView.findViewById(R.id.recentsearch_clear_all);
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

        if (isOnStoppingProcess) return;

        super.handleSuccessEvent(baseResponse);
        
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
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


            break;
        default:
            break;
        }
    }    

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {

        if (isOnStoppingProcess) return;
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
            showFragmentContentContainer();
            break;
        default:
            break;
        }
    }

    @Override
    public void onHeaderClick(String target, String title) {

    }

    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
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

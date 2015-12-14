/**
 * 
 */
package com.mobile.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.SearchSuggestionsAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.search.GetSearchSuggestionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.SearchRecentQueriesTableHelper;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show recent searches
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class RecentSearchFragment extends BaseFragment implements IResponseCallback {
    
    private final static String TAG = RecentSearchFragment.class.getSimpleName();

    private Context mContext;

    private SearchSuggestionsAdapter mRecentSearchesAdapter;
    
    private ArrayList<Suggestion> mRecentSearches;
    
    private ListView mRecentSearchesList;
    
    private TextView mClearAllButton;

    /**
     * Empty constructor
     */
    public RecentSearchFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.RECENT_SEARCHES,
                R.layout.recentsearches,
                R.string.recent_searches,
                ADJUST_CONTENT);
    }
    
    /**
     * Create new RecentSearchFragment instance
     * @return RecentSearchFragment
     */
    public static RecentSearchFragment newInstance() {
        return new RecentSearchFragment();
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
        // Tracking page
        TrackerDelegator.trackPage(TrackingPage.RECENT_SEARCHES, getLoadTime(), false);
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

        mRecentSearchesList = (ListView) mainView.findViewById(R.id.recentsearch_list);

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
     * Execute search
     * @param searchText
     */
    protected void executeSearchRequest(String searchText) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES, false);
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

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

            ArrayList<Suggestion> response = (GetSearchSuggestionsHelper.SuggestionsStruct)baseResponse.getMetadata().getData();
            if (response != null) {
                mRecentSearches = response;
                if (!mRecentSearches.isEmpty()) {
                    mRecentSearchesAdapter = new SearchSuggestionsAdapter(mContext, mRecentSearches);
                    mRecentSearchesList.setAdapter(mRecentSearchesAdapter);
                    mRecentSearchesList.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Print.d(TAG, "SEARCH: CLICKED ITEM " + position);
                            Suggestion selectedSuggestion = (Suggestion) mRecentSearchesList.getItemAtPosition(position);
                            String text = selectedSuggestion.getResult();
                            GetSearchSuggestionsHelper.saveSearchQuery(text);
                            executeSearchRequest(text);
                            JumiaApplication.INSTANCE.setSearchedTerm(text);
                        }
                    });

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

}

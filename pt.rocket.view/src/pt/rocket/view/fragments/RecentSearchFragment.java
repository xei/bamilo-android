/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.SearchSuggestionsAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.database.SearchRecentQueriesTableHelper;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetSearchSuggestionHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import de.akquinet.android.androlog.Log;

/**
 * @author Andre Lopes
 * 
 */
public class RecentSearchFragment extends BaseFragment {
    
    private final static String TAG = LogTagHelper.create(RecentSearchFragment.class);

    private Context mContext;

    private static View mainView;

    private SearchSuggestionsAdapter mRecentSearchesAdapter;
    
    private ArrayList<SearchSuggestion> mRecentSearches;
    
    private GridView mRecentSearchesGrid;
    private Button mClearAllButton;

    // private View mLoadingView;

    public RecentSearchFragment() {

        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.RecentSearch,
                R.layout.recentsearches,
                true,
                R.string.recent_searches,
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setEmptyView(R.string.recentsearch_no_searches, R.drawable.img_norecentsearch);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");

        mainView = view;

        setAppContentLayout();

        init();
    }

    public static RecentSearchFragment getInstance() {
        return new RecentSearchFragment();
    }

    private void setAppContentLayout() {
        if (mainView == null) {
            mainView = getView();
        }

        mRecentSearchesGrid = (GridView) mainView.findViewById(R.id.middle_recentsearch_list);

        mClearAllButton = (Button) mainView.findViewById(R.id.recentsearch_clear_all);
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

                showFragmentEmpty();
                mClearAllButton.setVisibility(View.GONE);
                Log.d(TAG, "RECENT SEARCHES: " + mRecentSearches.size());

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideActivityProgress();
                    }
                }, 300);
            }
        });
    }

    private void init() {
        Log.d(TAG, "INIT");
        mContext = getActivity();

        // Get Recent Searches
        Log.i(TAG, "LOAD RECENT SEARCHES");
        showFragmentLoading(false);
        new GetSearchSuggestionHelper(responseCallback);
    }

    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    public void onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE COMPLETE:");

        if (isOnStoppingProcess) return;

        getBaseActivity().handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
            Log.d(TAG, "ON RESPONSE COMPLETE: GET_SEARCH_SUGGESTIONS_EVENT");

            Object response = bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            if (response != null && response instanceof ArrayList) {
                mRecentSearches = (ArrayList<SearchSuggestion>) response;
                if (mRecentSearches != null && !mRecentSearches.isEmpty()) {
                    mRecentSearchesAdapter = new SearchSuggestionsAdapter(mContext, mRecentSearches);
                    mRecentSearchesGrid.setAdapter(mRecentSearchesAdapter);
                    mRecentSearchesGrid.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d(TAG, "SEARCH: CLICKED ITEM " + position);
                            SearchSuggestion selectedSuggestion = (SearchSuggestion) mRecentSearchesGrid.getItemAtPosition(position);
                            String text = selectedSuggestion.getResult();
                            GetSearchSuggestionHelper.saveSearchQuery(text);
                            executeSearchRequest(text);
                        }
                    });

                    mClearAllButton.setVisibility(View.VISIBLE);
                    showFragmentContentContainer();
                } else {
                    showFragmentEmpty();
                }
            } else {
                mRecentSearches = null;
                showFragmentEmpty();
            }

            Log.d(TAG, "RECENT SEARCHES: " + mRecentSearches.size());

            break;
        default:
            Log.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
    }
    
    /**
     * Execute search
     * @param searchText
     */
    protected void executeSearchRequest(String searchText) {
        Log.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    public void onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE ERROR:");

        if (isOnStoppingProcess) return;

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_SEARCH_SUGGESTIONS_EVENT:
            Log.d(TAG, "ON RESPONSE ERROR: GET_SEARCH_SUGGESTIONS_EVENT");
            showFragmentContentContainer();
            break;
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }
}

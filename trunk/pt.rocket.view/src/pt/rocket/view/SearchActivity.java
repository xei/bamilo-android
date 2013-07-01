package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.SearchSuggestionsAdapter;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetSearchSuggestionsEvent;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.RightDrawableOnTouchListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
 * </p>
 * 
 */
public class SearchActivity extends MyActivity implements OnItemClickListener {

    private final static String TAG = LogTagHelper.create(SearchActivity.class);
    private final static String KEY_STATE_VIEW = "search_state_view";

    private Bundle savedState;

    private EditText searchTermView;
    private TextView suggestionsEmptyText;
    private View suggestionsLayout;    
    
    private long beginInMillis;

    private String searchSuggestionText;
    
    private ListView listView;

    
    public SearchActivity() {
        super(R.layout.search, NavigationAction.Search,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_SEARCH_SUGGESTIONS_EVENT),
                EnumSet.noneOf(EventType.class),
                0, R.layout.search_suggestions);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        savedState = savedInstanceState;
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.bg_actionbar_top));
        if (null != savedState && savedState.containsKey(KEY_STATE_VIEW)) {
            savedState.remove(KEY_STATE_VIEW);
        }
        
        setAppContentLayout();
        initSearchView();
    }
        
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initSearchView();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gsearch);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unbindDrawables(findViewById(R.id.rocket_app_content));
        System.gc();
    }
    
    private void setAppContentLayout() {
        searchTermView = (EditText) findViewById(R.id.search_component);
        searchTermView.setFocusable(true);
        listView = (ListView) findViewById(R.id.search_suggestions_list_content);
        listView.setOnItemClickListener(this);
        suggestionsEmptyText = (TextView) findViewById( R.id.searchsuggestions_notfound );
        suggestionsLayout = findViewById( R.id.searchsuggestions_empty );
    }
    
    private void initSearchView() {
        searchSuggestionText = "";
        searchTermView.requestFocus();
        searchTermView.setText("");
        setEmptySuggestions( R.string.searchsuggestions_pleaseenter );
        setSearchBar();
        showKeyboardAndFocus();
    }

    protected void executeSearch(String searchText) {
        ActivitiesWorkFlow.productsActivity(this, null, searchText, searchText, R.string.gsearch, "");
    }
    
    private void requestSuggestions() {
        beginInMillis = System.currentTimeMillis();
        EventManager.getSingleton().triggerRequestEvent(
                new GetSearchSuggestionsEvent(searchSuggestionText));
    }

    // Setting the searchbar icon Clickable
    protected void setSearchBar() {
        searchTermView.setOnTouchListener(new RightDrawableOnTouchListener(searchTermView) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                String searchTerm = searchTermView.getText().toString();
                if ( TextUtils.isEmpty( searchTerm ))
                    return false;
                
                executeSearch(searchTerm);
                hideKeyboardAndFocus();
                return true;
            }

        });
        searchTermView.addTextChangedListener(searchInputWatcher);
        searchTermView.setOnKeyListener(searchKeyListener);
        searchTermView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, " [onClick] -> autoCompleteView.setOnClickListener ");
            }
        });
    }

    // SEARCH SUGGESTIONS
    private void setSearchSuggestions(final List<SearchSuggestion> arrayList) {
        Log.d( TAG, "setSearchSuggestions" );
        SearchSuggestionsAdapter adapter = new SearchSuggestionsAdapter(this, arrayList);
        listView.setAdapter(adapter);
        suggestionsLayout.setVisibility( View.GONE );
    }

    private void setEmptySuggestions(int resId) {
        suggestionsEmptyText.setText(resId);
        SearchSuggestionsAdapter adapter = (SearchSuggestionsAdapter) listView.getAdapter();
        if ( adapter != null)
            adapter.clear();        
        suggestionsLayout.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        findViewById(R.id.dummy_search_layout).requestFocus();
        executeSearch( ((SearchSuggestion)parent.getAdapter().getItem(position)).getResult());
        // autoCompleteView.setText( "" );
        hideKeyboardAndFocus();
    }

    protected void hideKeyboardAndFocus() {
        hideKeyboard();
        findViewById(R.id.dummy_search_layout).requestFocus();
    }

    protected void showKeyboardAndFocus() {
        // autoCompleteView.clearFocus();
        searchTermView.requestFocus();
        showKeyboard();
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.d(TAG, "handleEvent: event type = " + event.getType().name());
        AnalyticsGoogle.get().trackLoadTiming(R.string.gsearchsuggestions, beginInMillis);
        setSearchSuggestions((List<SearchSuggestion>) event.result);
        return true;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if(event.errorCode == ErrorCode.REQUEST_ERROR) {
            setEmptySuggestions( R.string.searchsuggestions_empty);
            showContentContainer();
            return true;
        }
        return super.onErrorEvent(event);
    }
    
    private TextWatcher searchInputWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // get the suggestions
            searchSuggestionText = s.toString();
            searchTermView.removeCallbacks(getSuggestionsRunnable);
            if (searchTermView.hasFocus())
                searchTermView.postDelayed(getSuggestionsRunnable,100);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    
    private Runnable getSuggestionsRunnable = new Runnable() {

        @Override
        public void run() {
            if ( searchSuggestionText == null || searchSuggestionText.length() <= 2) {
                setEmptySuggestions( R.string.searchsuggestions_pleaseenter );
                return;
            }
            requestSuggestions();
        }
    };
    
    /**
     * The listener for the key down on the search box, so that when the user presses enter the
     * search is executed
     */
    OnKeyListener searchKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:

                // Check for ACTION_DOWN only...
                if (KeyEvent.ACTION_DOWN == event.getAction()) {
                    // asearchSuggestionText = "";
                    searchTermView.removeCallbacks(getSuggestionsRunnable);
                    hideKeyboard();
                    return true;
                }
            }
            return false;
        }
    };
}

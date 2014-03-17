/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.SearchSuggestionsAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetSearchSuggestionHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.RightDrawableOnTouchListener;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SearchFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = LogTagHelper.create(SearchFragment.class);
    
    private final static String KEY_STATE_VIEW = "search_state_view";

    private static SearchFragment searchFragment;
    
    private SearchSuggestionsAdapter searchSuggestionsAdapter;
    
    private Bundle savedState;

    private EditText searchTermView;

    private ListView listView;

    private TextView suggestionsEmptyText;

    private View suggestionsLayout;

    private String searchSuggestionText;
    
    private long mBeginRequestMillis;
    

    /**
     * Get instance
     * 
     * @return
     */
    public static SearchFragment getInstance() {
        if (searchFragment == null)
            searchFragment = new SearchFragment();
        return searchFragment;
    }

    /**
     * Empty constructor
     */
    public SearchFragment() {
        super(EnumSet.of(EventType.GET_SEARCH_SUGGESTIONS_EVENT), 
                EnumSet.noneOf(EventType.class), 
                EnumSet.of(MyMenuItem.SEARCH_BAR), 
                NavigationAction.Search, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        // Saved content
        savedState = savedInstanceState;
        if (null != savedState && savedState.containsKey(KEY_STATE_VIEW)) {
            savedState.remove(KEY_STATE_VIEW);
        }        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.search_suggestions, container, false);
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        
        setAppContentLayout();
        
        if(searchSuggestionText == null || searchSuggestions == null) {
            initSearchView();
        }else {
            restoreSearchView();
        }
            
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        AnalyticsGoogle.get().trackPage(R.string.gsearch);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        searchSuggestionText = "";
        searchSuggestions = null;
        ((BaseActivity) getActivity()).cleanSearchConponent();
    }
    
    
    private void setAppContentLayout() {
        ((BaseActivity) getActivity()).setSearchNormalBehaviour();
        searchTermView = ((BaseActivity) getActivity()).getSearchComponent();
        searchTermView.setFocusable(true);
        listView = (ListView) getView().findViewById(R.id.search_suggestions_list_content);
        listView.setOnItemClickListener(this);
        suggestionsEmptyText = (TextView) getView().findViewById( R.id.searchsuggestions_notfound );
        suggestionsLayout = getView().findViewById( R.id.searchsuggestions_empty );
    }
    
    public void initSearchView() {
        searchSuggestionText = "";
        searchTermView.requestFocus();
        searchTermView.setText("");
        setEmptySuggestions( R.string.searchsuggestions_pleaseenter );
        setSearchBar();
        showKeyboardAndFocus();
    }
    
    public void restoreSearchView() {
        Log.i(TAG, "ON RESTORE SEARCH VIEW");
        searchTermView.setText(searchSuggestionText);
        searchTermView.requestFocus();
        setSearchBar();
        showKeyboardAndFocus();
        if(searchSuggestions != null && searchSuggestions.size() > 0)
            setSearchSuggestions(searchSuggestions);
        else 
            setEmptySuggestions(R.string.searchsuggestions_pleaseenter);
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
        
        searchTermView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchTerm = searchTermView.getText().toString();
                    if ( TextUtils.isEmpty( searchTerm ))
                        return false;
                    
                    executeSearch(searchTerm);
                    hideKeyboardAndFocus();
                    return true;
                }
                return false;
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
    

    
    protected void executeSearch(String searchText) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        ((MainFragmentActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
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
    
    private void requestSuggestions() {
        mBeginRequestMillis = System.currentTimeMillis();
        Bundle bundle = new Bundle();
        bundle.putString(GetSearchSuggestionHelper.SEACH_PARAM, searchSuggestionText);
        JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionHelper(), bundle, mCallBack);
//        EventManager.getSingleton().triggerRequestEvent(
//                new GetSearchSuggestionsEvent(searchSuggestionText));
    }
    
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
                    ((BaseActivity) getActivity()).hideKeyboard();
                    return true;
                }
            }
            return false;
        }
    };

    private List<SearchSuggestion> searchSuggestions;

    
    
    
    private void setEmptySuggestions(int resId) {
        suggestionsEmptyText.setText(resId);
        SearchSuggestionsAdapter adapter = (SearchSuggestionsAdapter) listView.getAdapter();
        if ( adapter != null)
            adapter.clear();        
        suggestionsLayout.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideKeyboardAndFocus();
        getActivity().findViewById(R.id.dummy_search_layout).requestFocus();
        executeSearch( ((SearchSuggestion)parent.getAdapter().getItem(position)).getResult());
        // autoCompleteView.setText( "" );
    }
    
    protected void showKeyboardAndFocus() {
        // autoCompleteView.clearFocus();
        searchTermView.requestFocus();
        ((BaseActivity) getActivity()).showKeyboard();
    }

    protected void hideKeyboardAndFocus() {
        ((BaseActivity) getActivity()).hideKeyboard();
        getActivity().findViewById(R.id.dummy_search_layout).requestFocus();
    }
    
//    @Override
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        
//        return true;
//    }
    
    // SEARCH SUGGESTIONS
    private void setSearchSuggestions(final List<SearchSuggestion> arrayList) {
        Log.d( TAG, "setSearchSuggestions" );
        //SearchSuggestionsAdapter searchSuggestionsAdapter = new SearchSuggestionsAdapter(getActivity(), arrayList);
        try {
            searchSuggestionsAdapter = new SearchSuggestionsAdapter(getActivity(), arrayList);
            listView.setAdapter(searchSuggestionsAdapter);
            suggestionsLayout.setVisibility( View.GONE );
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON set search suggestion: ");
            e.printStackTrace();
        }
    }
    
    IResponseCallback mCallBack = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    private void onErrorEvent(Bundle bundle){
        Log.d(TAG, "ON ERROR EVENT");
        if(getBaseActivity() == null){
            return;
        }
        
        mBeginRequestMillis = System.currentTimeMillis();
        if(getBaseActivity().handleErrorEvent(bundle)){
            return;
        }
        // Validate fragment visibility
        if(isVisible()){
            setEmptySuggestions( R.string.searchsuggestions_empty);
            ((BaseActivity) getActivity()).showContentContainer(false);
        }
    }
    
    private void onSuccessEvent(Bundle bundle){
        Log.d(TAG, "ON SUCCESS EVENT");
        if(isVisible()){
            AnalyticsGoogle.get().trackLoadTiming(R.string.gsearchsuggestions, mBeginRequestMillis);
            searchSuggestions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);

            setSearchSuggestions((List<SearchSuggestion>) searchSuggestions);
        }
    }
}

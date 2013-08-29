package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.CategoriesFragment;
import pt.rocket.view.fragments.FragmentType;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CategoriesFragmentActivity extends BaseActivity {

    private final String TAG = LogTagHelper.create(CategoriesFragmentActivity.class);

    private Fragment fragment;

    private String categoryUrl;

    private boolean inBackground;
    
    // Used by CategoriesFragment and InnerCategoriesFragment
    public static List<Category> currentCategories;
    // Used by CategoriesFragment and InnerCategoriesFragment
    public static int selectedCategoryPosition = 0;
    // Used by CategoriesFragment and InnerCategoriesFragment
    public static int selectedSubCategoryPosition = -1;
    
    
    public static FragmentType currentFragment = FragmentType.CATEGORIES_LEVEL_1;
    

    /**
	 * Empty constructor
	 */
    public CategoriesFragmentActivity() {
        super(NavigationAction.Categories,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                0, R.layout.categories_fragments);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //onSwitchFragment(FragmentType.CATEGORIES_LEVEL_1, true);
        
        init(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init( intent );
    }
    
    private void init(Intent intent) {
        categoryUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        
        onSwitchFragment(FragmentType.CATEGORIES_LEVEL_1, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (inBackground) {
            // You just came from the background
            inBackground = false;
        }
        else {
            // You just returned from another activity within your own app
        }

    }
    
    @Override
    public void onUserLeaveHint() {
        inBackground = true;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        currentCategories = null;
        currentFragment = FragmentType.CATEGORIES_LEVEL_1;
        selectedCategoryPosition = 0;
        selectedSubCategoryPosition = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onSwitchFragment(pt.rocket.view.fragments.FragmentType,
     * java.lang.Boolean)
     */
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        Log.i(TAG, "ON SWITCH FRAGMENT");
        switch (type) {
        case CATEGORIES_LEVEL_1:
            currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            AnalyticsGoogle.get().trackPage(R.string.gcategories);
            break;
        case CATEGORIES_LEVEL_2:
            currentFragment = FragmentType.CATEGORIES_LEVEL_2;
            break;
        case CATEGORIES_LEVEL_3:
            currentFragment = FragmentType.CATEGORIES_LEVEL_3;
        default:
            break;
        }
        fragment = CategoriesFragment.newInstance(categoryUrl);
        fragmentManagerTransition(R.id.categories_fragments_container, fragment, addToBackStack, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.BaseActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");
        // Set the default value for sub category
        //selectedSubCategoryPosition = -1;
        switch (currentFragment) {
        case CATEGORIES_LEVEL_2:
            currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            break;
        case CATEGORIES_LEVEL_3:
            currentFragment = FragmentType.CATEGORIES_LEVEL_2;
            break;
        }
        fragmentManagerBackPressed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }

}

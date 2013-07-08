package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class ReviewFragmentActivity extends BaseActivity {

    private final static String TAG = LogTagHelper.create(ReviewFragmentActivity.class);

    /**
	 * Constructor
	 */
    public ReviewFragmentActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.review, R.layout.reviews_fragments);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return true;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
    }

}

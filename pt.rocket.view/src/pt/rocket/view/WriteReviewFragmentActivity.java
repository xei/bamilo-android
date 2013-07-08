package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.os.Bundle;

public class WriteReviewFragmentActivity extends BaseActivity {
    
    private final static String TAG = LogTagHelper.create(WriteReviewFragmentActivity.class);


    public WriteReviewFragmentActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.writereview_page_title, R.layout.writereview_fragments);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gproductreviewscreate);
    }
    
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {        
        return false;
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}

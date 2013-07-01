/**
 * 
 */
package pt.rocket.view;

import java.util.Collection;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.TeaserGroupAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * 
 */
public class TeaserActivity extends MyActivity {
    private final static String TAG = TeaserActivity.class.getSimpleName();

    private TeaserGroupAdapter adapter;
    private ListView teaserList;

    private OnClickListener teaserClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String targetUrl = (String) v.getTag(R.id.target_url);
            TargetType targetType = (TargetType) v.getTag(R.id.target_type);
            String targetTitle = (String) v.getTag(R.id.target_title);
            if (targetType != null) {
                Log.d(TAG, "targetType = " + targetType.name() + " targetUrl = " + targetUrl);
                switch (targetType) {
                case CATEGORY:
                    ActivitiesWorkFlow.categoriesActivityNew(TeaserActivity.this, targetUrl);
                    break;
                case PRODUCT_LIST:
                    if (targetUrl != null) {
                        ActivitiesWorkFlow.productsActivity(TeaserActivity.this,
                                targetUrl, targetTitle, null, R.string.gteaser_prefix,
                                AnalyticsGoogle.prepareNavigationPath(targetUrl));
                    }
                    break;
                case PRODUCT:
                    if (targetUrl != null) {
                        ActivitiesWorkFlow.productsDetailsActivity(
                                TeaserActivity.this, targetUrl, R.string.gteaserprod_prefix, "");
                    }
                    break;
                default:
                    Toast.makeText(TeaserActivity.this,
                            "The target for this teaser is not defined!",
                            Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    };

    /**
	 */
    public TeaserActivity() {
        super(R.layout.search,
                NavigationAction.Home,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_TEASERS_EVENT),
                EnumSet.noneOf(EventType.class),
                0, R.layout.teasers);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeSearchBarBehavior();
        adapter = new TeaserGroupAdapter(this, teaserClickListener);
        ((ListView) contentContainer).setAdapter(adapter);
        triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        HockeyStartup.register(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }

        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
    }

    private void changeSearchBarBehavior() {
        final EditText autoComplete = (EditText) findViewById(R.id.search_component);
        autoComplete.setEnabled(false);
        autoComplete.setFocusable(false);
        autoComplete.setFocusableInTouchMode(false);
        findViewById(R.id.search_overlay).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ActivitiesWorkFlow.searchActivity(TeaserActivity.this);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        Log.d(TAG, "Got teasers event: " + event);
        // Get Teasers
        adapter.myAddAll((Collection<? extends TeaserSpecification<?>>) event.result);
        return true;
    }

}

/**
 * 
 */
package pt.rocket.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.TeaserGroupAdapter;
import pt.rocket.framework.utils.Constants;
import pt.rocket.helpers.GetTeasersHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.EventType;
import pt.rocket.pojo.ITargeting;
import pt.rocket.pojo.TeaserSpecification;
import pt.rocket.pojo.ITargeting.TargetType;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
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
    private ArrayList<TeaserSpecification<ITargeting>> teasers = new ArrayList<TeaserSpecification<ITargeting>>();
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
                    ActivitiesWorkFlow.categoriesActivityNew(TeaserActivity.this);
                    break;
                case PRODUCT_LIST:
                    if (targetUrl != null) {
                        // ActivitiesWorkFlow.productsActivity(TeaserActivity.this,
                        // targetUrl, targetTitle, null, R.string.gteaser_prefix,
                        // AnalyticsGoogle.prepareNavigationPath(targetUrl));
                    }
                    break;
                case PRODUCT:
                    if (targetUrl != null) {
                        // ActivitiesWorkFlow.productsDetailsActivity(
                        // TeaserActivity.this, targetUrl, R.string.gteaserprod_prefix, "");
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
        
        Log.i(TAG,"Sending request");
        sendRequest(new GetTeasersHelper(), new IResponseCallback() {



            @Override
            public void onRequestError(Bundle bundle) {
                Log.d(TAG, "onRequestError");

            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.d(TAG, "onRequestComplete");
                teasers = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                Log.i(TAG,"Teasers size "+teasers.size());
                adapter.myAddAll((Collection<? extends TeaserSpecification<?>>) teasers);
                
                
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }
        
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
                        // ActivitiesWorkFlow.searchActivity(TeaserActivity.this);
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

}
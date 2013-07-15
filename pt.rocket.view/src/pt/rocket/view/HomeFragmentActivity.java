/**
 * 
 */
package pt.rocket.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.holoeverywhere.widget.ViewPager;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.HomePageViewPagerAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.fragments.CategoryTeaserFragment;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.HomeTeaserFragment;
import pt.rocket.view.fragments.MainOneSlideFragment;
import pt.rocket.view.fragments.ProducTeaserListFragment;
import pt.rocket.view.fragments.StaticBannerFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * @modified manuelsilva
 */
public class HomeFragmentActivity extends BaseActivity {
    private final static String TAG = HomeFragmentActivity.class.getSimpleName();

    private LayoutInflater mInflater;
    private ViewPager mPager;
    private HomePageViewPagerAdapter mPagerAdapter;
    /**
	 */
    public HomeFragmentActivity() {
        super(R.layout.search,
                NavigationAction.Home,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                0, R.layout.teasers_fragments_viewpager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mPager = (ViewPager) findViewById(R.id.home_viewpager);
        
        HomeTeaserFragment fragment1 = new HomeTeaserFragment();
//        HomeTeaserFragment fragment2 = new HomeTeaserFragment();
//        HomeTeaserFragment fragment3 = new HomeTeaserFragment();
//        HomeTeaserFragment fragment4 = new HomeTeaserFragment();
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(fragment1);
//        fragments.add(fragment2);
//        fragments.add(fragment3);
//        fragments.add(fragment4);
        
        mPagerAdapter = new HomePageViewPagerAdapter(getSupportFragmentManager(), fragments);

        mPager.setAdapter(mPagerAdapter);
        
        changeSearchBarBehavior();
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
                        ActivitiesWorkFlow.searchActivity(HomeFragmentActivity.this);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }

}

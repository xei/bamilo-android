///**
// * 
// */
//package pt.rocket.view;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.EnumSet;
//import java.util.Iterator;
//
//import org.holoeverywhere.widget.TextView;
//
//import pt.rocket.constants.ConstantsSharedPrefs;
//import pt.rocket.controllers.ActivitiesWorkFlow;
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.RequestEvent;
//import pt.rocket.framework.event.ResponseEvent;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.objects.BrandsTeaserGroup;
//import pt.rocket.framework.objects.CategoryTeaserGroup;
//import pt.rocket.framework.objects.Homepage;
//import pt.rocket.framework.objects.ITargeting.TargetType;
//import pt.rocket.framework.objects.ImageTeaserGroup;
//import pt.rocket.framework.objects.ProductTeaserGroup;
//import pt.rocket.framework.objects.Promotion;
//import pt.rocket.framework.objects.TeaserSpecification;
//import pt.rocket.framework.utils.AnalyticsGoogle;
//import pt.rocket.framework.utils.MixpanelTracker;
//import pt.rocket.utils.CheckVersion;
//import pt.rocket.utils.HockeyStartup;
//import pt.rocket.utils.JumiaViewPager;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.OnActivityFragmentInteraction;
//import pt.rocket.utils.dialogfragments.DialogPromotionFragment;
//import pt.rocket.view.fragments.BrandsTeaserListFragment;
//import pt.rocket.view.fragments.CategoryTeaserFragment;
//import pt.rocket.view.fragments.FragmentType;
//import pt.rocket.view.fragments.MainOneSlideFragment;
//import pt.rocket.view.fragments.ProducTeaserListFragment;
//import pt.rocket.view.fragments.StaticBannerFragment;
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.LinearGradient;
//import android.graphics.Paint;
//import android.graphics.Shader;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.view.PagerTabStrip;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
///**
// * @author manuelsilva
// * 
// */
//public class HomeFragmentActivity extends BaseActivity {
//    
//    /**
//	 */
//    public HomeFragmentActivity() {
//        super(R.layout.search,
//                NavigationAction.Home,
//                EnumSet.noneOf(MyMenuItem.class),
//                EnumSet.of(EventType.GET_API_INFO, EventType.GET_TEASERS_EVENT, EventType.GET_CALL_TO_ORDER_PHONE, EventType.GET_PROMOTIONS, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT),
//                EnumSet.noneOf(EventType.class),
//                0, R.layout.teasers_fragments_viewpager);
//    }
//    
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activity = this;
//        changeSearchBarBehavior();
//
//        HockeyStartup.register(this);
//    }
//
//    public void moveToRight() {
//    }
//
//    public void moveToLeft() {
//    }
//
//
//
//
//
//    
//
//
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        
//        if (inBackground) {
//            // You just came from the background
//            inBackground = false;
//        }
//        else {
//            // You just returned from another activity within your own app
//        }
//
//        
//    }
//    
//    @Override
//    public void onUserLeaveHint() {
//        inBackground = true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        
//    }
//
//    @Override
//    public void onBackPressed() {
//        finish();
//    }
//
//    @Override
//    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        switch (event.getType()) {
//        case GET_API_INFO:
//            CheckVersion.run(getApplicationContext());
//            break;
//        case GET_TEASERS_EVENT:
//            isFirstBoot = false;
//            proccessResult((Collection<? extends Homepage>) event.result);
//            Log.i(TAG, "code1 checkversion called");
//            CheckVersion.run(getApplicationContext());
//            break;
//        case GET_CALL_TO_ORDER_PHONE:
//            SharedPreferences sharedPrefs = this.getSharedPreferences(
//                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putString(ProductDetailsActivityFragment.KEY_CALL_TO_ORDER,
//                    (String) event.result);
//            editor.commit();
//
//            break;
//        case GET_PROMOTIONS:
//            if(((Promotion) event.result).getIsStillValid()){
//                DialogPromotionFragment.newInstance((Promotion) event.result).show(getSupportFragmentManager(), null);    
//            } else {
//                Log.i(TAG, "promotion expired!"+((Promotion) event.result).getEndDate());
//            }
//            
//            break;
//        }
//
//        return true;
//    }
//    
//    @Override
//    protected boolean onErrorEvent(ResponseEvent event) {
//        switch (event.getType()) {
//            case GET_TEASERS_EVENT:
//                setLayoutFallback();
//                break;
//            case GET_PROMOTIONS:
//                break;
//        }
//        return true;
//    }
//
// 
//}

package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MyLayoutInflater;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira 
 * 
 */
public class ChangeCountryFragmentActivity extends BaseActivity {

    public ChangeCountryFragmentActivity() {
        super(NavigationAction.Country,  
                EnumSet.noneOf(MyMenuItem.class), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                0,
                R.layout.change_country_fragments);
    }

    private final String TAG = LogTagHelper.create(ChangeCountryFragmentActivity.class);
    
    public static String KEY_COUNTRY = "country";

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE"); 
        setupActionBar();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }
    
//    /**
//     * 
//     */
//    private void setupActionBar() {
//        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
//        getSupportActionBar().setLogo(R.drawable.logo_ic);
//    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /**
     * Creates the custom layout inflater
     * 
     * @return the inflater
     * 
     *         (non-Javadoc)
     * @see android.app.Activity#getLayoutInflater()
     */
    @Override
    public LayoutInflater getLayoutInflater() {
        return new MyLayoutInflater(this, getOrigInflater());
    }

    /**
     * Creates the custom layout inflater if requested via system service
     * 
     * @return the inflater if requested by name, otherwise the system service
     * 
     *         (non-Javadoc)
     * @see android.app.Activity#getSystemService(java.lang.String)
     */
    @Override
    public Object getSystemService(String name) {
        if (name.equals(LAYOUT_INFLATER_SERVICE)) {
            return new MyLayoutInflater(this, getOrigInflater());
        }
        return super.getSystemService(name);
    }

    /**
     * Creates the original layout inflater
     * 
     * @return the original layout inflater
     */
    private LayoutInflater getOrigInflater() {
        return (LayoutInflater) super.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.app.Activity#finish()
//     */
//    @Override
//    public void finish() {
//        super.finish();
//        ActivitiesWorkFlow.addStandardTransition(this);
//    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onSwitchFragment(FragmentType search, Bundle bundle, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}

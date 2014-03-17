/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.MyAccountAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(MyAccountFragment.class);

    private final static int POSITION_USER_DATA = 0;
    
    private static MyAccountFragment myAccountFragment;
    
    private String[] myAccountOptions = null;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountFragment getInstance() {
        if (myAccountFragment == null)
            myAccountFragment = new MyAccountFragment();
        return myAccountFragment;
    }

    /**
     * Empty constructor
     */
    public MyAccountFragment() {
        super(EnumSet.noneOf(EventType.class), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.account_name);
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
        View view = inflater.inflate(R.layout.myaccount_fragment, container, false);
        showMyAccount(view);
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
        Log.i(TAG, "ON DESTROY");
        
        // FIXME : The next lines try fix this bug 
        // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
        View view = getView();
        if (view != null) {
            unbindDrawables(view);
        }
        
        System.gc();
    }
    
    /**
     * Shows my account options
     */
    private void showMyAccount(View v) {
        // Get User Account Option
        myAccountOptions = getResources().getStringArray(R.array.myaccount_array);
        // Get ListView
        ListView optionsList = (ListView) v.findViewById(R.id.middle_myaccount_list);
        // Create new Adapter
        MyAccountAdapter myAccountAdpater = new MyAccountAdapter(getActivity(), myAccountOptions);
        // Set adapter
        optionsList.setAdapter(myAccountAdpater);
        // Set Listener for all items
        optionsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Validate item
                if (position == POSITION_USER_DATA) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
                    bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN, getString(R.string.mixprop_loginlocationmyaccount));
                    getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
                }

            }
        });
        
    }

}

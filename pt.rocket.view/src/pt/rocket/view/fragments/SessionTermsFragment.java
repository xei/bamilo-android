/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
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
import org.holoeverywhere.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author Manuel Silva
 * 
 */
public class SessionTermsFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(SessionTermsFragment.class);

    private TextView textView;
    
    private static SessionTermsFragment termsFragment;
    
    private String termsText;
    /**
     * Get instance
     * 
     * @return
     */
    public static SessionTermsFragment getInstance() {
        if (termsFragment == null)
            termsFragment = new SessionTermsFragment();
        return termsFragment;
    }
    
    
    public static SessionTermsFragment getInstance(Bundle bundle) {
        if (termsFragment == null)
            termsFragment = new SessionTermsFragment();
        termsFragment.termsText = bundle.getString(ConstantsIntentExtra.TERMS_CONDITIONS);
        return termsFragment;
    }

    /**
     * Empty constructor
     */
    public SessionTermsFragment() {
        super(EnumSet.noneOf(EventType.class),
        EnumSet.noneOf(EventType.class),
        EnumSet.noneOf(MyMenuItem.class), 
        NavigationAction.Unknown, 
        0);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.termsText= (String) values;
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
        View view = inflater.inflate(R.layout.terms_conditions_fragment, container, false);
        textView = (TextView) view.findViewById(R.id.terms_text);
        setupView();
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
    }
    
    private void setupView() {
            textView.setText(termsText);     
    }
}

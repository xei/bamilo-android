package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.TermsFragment;
import android.os.Bundle;

/**
 * <p>This class shows the gallery of images of a given product.</p>
 * <p/>
 * <p>Copyright (C) 2012 Rocket Internet - All Rights Reserved</p>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by guilhermesilva, 19/06/2012.</p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author guilhermesilva
 * 
 * @modified josedourado
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */

public class TermsActivityFragment extends BaseActivity {

    private TermsFragment termsFragment;
    private OnActivityFragmentInteraction mCallbackTermsActivityFragment;
    
	public TermsActivityFragment() {
		super(NavigationAction.Unknown,
		        EnumSet.of(MyMenuItem.SEARCH),
		        EnumSet.noneOf(EventType.class),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.terms_conditions_frame);
	}
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
    }
    
    /**
     * Inflate the current activity layout into the main layout template
     */
    private void setAppContentLayout() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            termsFragment = TermsFragment.getInstance();
            startFragmentCallbacks();
            mCallbackTermsActivityFragment.sendValuesToFragment(0, b.getString("terms_conditions"));
            fragmentManagerTransition(R.id.terms_container, termsFragment, false, true);
        }
        
    }

    private void startFragmentCallbacks() {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackTermsActivityFragment = (OnActivityFragmentInteraction) termsFragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(termsFragment.toString()
                    + " must implement OnActivityFragmentInteraction");
        }

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
        // TODO Auto-generated method stub
        
    }
}
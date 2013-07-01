package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.os.Bundle;
import android.widget.TextView;

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

public class TermsActivity extends MyActivity {

	public TermsActivity() {
		super(NavigationAction.Unknown,
		        EnumSet.of(MyMenuItem.SEARCH),
		        EnumSet.noneOf(EventType.class),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.terms_conditions_layout);
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
        TextView terms_text = (TextView) findViewById(R.id.terms_text);
        if (b != null) {
            terms_text.setText(b.getString("terms_conditions"));
        }
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return true;
    }
}
/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.helpers.configs.GetTermsConditionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.statics.StaticTermsConditions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * @author Manuel Silva
 * 
 */
public class SessionTermsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = SessionTermsFragment.class.getSimpleName();

    private TextView textView;
    
    private String termsText;

    /**
     * New instance SessionTermsFragment.
     * @param bundle The arguments
     * @return SessionTermsFragment
     */
    public static SessionTermsFragment getInstance(Bundle bundle) {
        SessionTermsFragment termsFragment = new SessionTermsFragment();
        termsFragment.setArguments(bundle);
        return termsFragment;
    }

    /**
     * Empty constructor
     */
    public SessionTermsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Terms,
                R.layout.terms_conditions_fragment,
                R.string.terms_and_conditions,
                KeyboardState.NO_ADJUST_CONTENT);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        textView = (TextView) view.findViewById(R.id.terms_text);
        triggerTerms();
    }

    private void triggerTerms() {
        triggerContentEvent(new GetTermsConditionsHelper(), null, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() != null) {
            super.handleSuccessEvent(baseResponse);
        } else {
            return;
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_TERMS_EVENT:
                showFragmentContentContainer();
                termsText = ((StaticTermsConditions)baseResponse.getMetadata().getData()).getHtml();
                textView.setText(termsText);
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        super.handleErrorEvent(baseResponse);

    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerTerms();
    }
}

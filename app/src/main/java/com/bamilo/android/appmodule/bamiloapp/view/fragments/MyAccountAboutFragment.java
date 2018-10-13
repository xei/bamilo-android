package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bamilo.android.framework.service.objects.statics.MobileAbout;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * @author shahrooz
 *
 */
public class MyAccountAboutFragment extends BaseFragment /*implements  IResponseCallback*/ {

    private static final String TAG = MyAccountAboutFragment.class.getSimpleName();

    private static final String TARGETS_TAG = MobileAbout.class.getSimpleName();
    /**
     * Empty constructor
     */
    public MyAccountAboutFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.ABOUT,
                R.layout.my_account_about_fragment,
                R.string.account_about,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /*
             * (non-Javadoc)
             *
             * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
             */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button call_btn = getBaseActivity().findViewById(R.id.about_call_btn);
        call_btn.setOnClickListener(v -> UIUtils.onClickCallToOrder(getBaseActivity()));
        Button email_btn = getBaseActivity().findViewById(R.id.about_email_btn);
        email_btn.setOnClickListener(v -> UIUtils.onClickEmailToCS(getBaseActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*
         * (non-Javadoc)
         *
         * @see com.mobile.view.fragments.MyFragment#onPause()
         */
    @Override
    public void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }








}
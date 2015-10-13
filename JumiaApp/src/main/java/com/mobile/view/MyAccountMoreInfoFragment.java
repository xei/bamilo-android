package com.mobile.view;

import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.fragments.BaseFragment;

import java.util.EnumSet;

/**
 * Created by rsoares on 10/13/15.
 */
public class MyAccountMoreInfoFragment extends BaseFragment{

    private static final String TAG = MyAccountMoreInfoFragment.class.getSimpleName();

    private TextView faqLink;

    private TextView termsLink;

    /**
     * Get instance
     * @return MyAddressesFragment
     */
    public static MyAccountMoreInfoFragment newInstance() {
        return new MyAccountMoreInfoFragment();
    }

    public MyAccountMoreInfoFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.my_account_more_info,
                R.string.account_name,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        faqLink = (TextView)view.findViewById(R.id.faq_link);
        termsLink = (TextView)view.findViewById(R.id.terms_link);

        faqLink.setOnClickListener(this);
        termsLink.setOnClickListener(this);

        faqLink.setText(Html.fromHtml("FAQ"));
        termsLink.setText(Html.fromHtml("TERMS"));
//        link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {

        if(view == faqLink){
            onClickFaqButton();
        } else if(view == termsLink){
            onClickTermsButton();
        } else {
            super.onClick(view);
        }
    }

    private void onClickTermsButton() {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, GetStaticPageHelper.TERMS_PAGE);
        bundle.putString(RestConstants.JSON_TITLE_TAG, getString(R.string.terms_and_conditions));
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void onClickFaqButton() {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, "faq_mobile");
        bundle.putString(RestConstants.JSON_TITLE_TAG, "FAQ");
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
}

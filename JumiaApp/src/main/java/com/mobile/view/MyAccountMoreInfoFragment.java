package com.mobile.view;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetFaqTermsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.fragments.BaseFragment;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by rsoares on 10/13/15.
 */
public class MyAccountMoreInfoFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = MyAccountMoreInfoFragment.class.getSimpleName();

    private ViewGroup linksContainer;

    private List<TargetHelper> targets;

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

        linksContainer = (ViewGroup) view.findViewById(R.id.links_container);

        triggerFaqAndTerms();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void onClickStaticPageButton(String key, String label) {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, key);
        bundle.putString(RestConstants.JSON_TITLE_TAG, label);
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void triggerFaqAndTerms() {
        triggerContentEvent(new GetFaqTermsHelper(), null, this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return ;
        }

        switch (eventType) {
            case GET_FAQ_TERMS:
                targets = (MobileAbout) baseResponse.getMetadata().getData();
                loadForm(targets);
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return ;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return ;
        }

//        ErrorCode errorCode = baseResponse.getError().getErrorCode();

        switch (eventType) {
            case GET_FAQ_TERMS:
                showContinueShopping();
                break;
        }
    }

    private void loadForm(@NonNull List<TargetHelper> targets){
        for(TargetHelper targetHelper : targets){
            if(targetHelper.getTargetType() == ITargeting.TargetType.SHOP){

            }
        }
        showFragmentContentContainer();
    }

    private void createTextViewLink(){

    }
}

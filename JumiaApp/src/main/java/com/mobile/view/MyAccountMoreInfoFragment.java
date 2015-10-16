package com.mobile.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by rsoares on 10/13/15.
 */
public class MyAccountMoreInfoFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = MyAccountMoreInfoFragment.class.getSimpleName();

    private ViewGroup linksContainer;

    private ArrayList<TargetHelper> targets;

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
        Print.i(TAG, "ON CREATE");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        linksContainer = (ViewGroup) view.findViewById(R.id.links_container);


    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArrayList(MobileAbout.class.getSimpleName(), targets);
    }

    private void onClickStaticPageButton(String key, String label) {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, key);
        bundle.putString(RestConstants.JSON_TITLE_TAG, label);
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void loadForm(@NonNull List<TargetHelper> targets){
        for(TargetHelper targetHelper : targets){
            if(targetHelper.getTargetType() == ITargeting.TargetType.SHOP){
                createTextViewLink(targetHelper);
            }
        }
        showFragmentContentContainer();
    }

    private void createTextViewLink(@NonNull final TargetHelper targetHelper){
        TextView textView = new TextView(this.getContext());
        textView.setText(Html.fromHtml(targetHelper.getTargetTitle()));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStaticPageButton(targetHelper.getTargetValue(), targetHelper.getTargetTitle());
            }
        });
        linksContainer.addView(textView);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }
}

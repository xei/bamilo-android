package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.ShoppingCartRemoveItemHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DarwinRegex;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.cart.UICartUtils;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class CheckoutConfirmationFragment extends BaseFragment implements View.OnClickListener {
 Button next;
    Switch voucher_switch;
    LinearLayout voucher_layer;
    private static final String TAG = CheckoutConfirmationFragment.class.getSimpleName();



    /**
     * Empty constructor
     */
    public CheckoutConfirmationFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.BASKET,
                R.layout.checkout_confirmation,
                R.string.checkout_confirmation,
                ADJUST_CONTENT);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout._def_checkout_confirmation, container, false);
        return  view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Print.i(TAG, "ON VIEW CREATED");
        //super.setCheckoutStep(view, 2);
        next = (Button) view.findViewById(R.id.checkout_confirmation_btn);
        voucher_switch = (Switch) view.findViewById(R.id.voucher_switch);
        voucher_layer = (LinearLayout) view.findViewById(R.id.voucher_layout);
        next.setOnClickListener(this);

        voucher_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    voucher_layer.setVisibility(View.VISIBLE);
                }else{
                    voucher_layer.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId== R.id.checkout_confirmation_btn)
        {
            Toast.makeText(getActivity(),"asdasdasdas",Toast.LENGTH_LONG).show();
        }
    }
}

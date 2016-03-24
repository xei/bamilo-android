package com.mobile.utils.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
/**
 * Class used for TabLayout.
 * @author spereira
 */
public class TabLayoutUtils {


    public static void fillTabLayout(@NonNull TabLayout tabLayout, @Nullable TabLayout.OnTabSelectedListener listener) {
        // Home
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab);
        tab.setCustomView(R.layout.tab_home);
        // Saved
        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);
        tab2.setCustomView(R.layout.tab_saved);
        // Basket
        TabLayout.Tab tab3 = tabLayout.newTab();
        tabLayout.addTab(tab3);
        tab3.setCustomView(R.layout.tab_cart);
        // Set listener
        tabLayout.setOnTabSelectedListener(listener);
    }

    public static void fillCheckoutTabLayout(@NonNull TabLayout tabLayout, @Nullable TabLayout.OnTabSelectedListener listener, @Nullable android.view.View.OnClickListener clickListener) {
        // About You
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab);
        tab.setCustomView(R.layout.tab_about_you);
        ((View) tab.getCustomView().getParent()).setTag(ConstantsCheckout.CHECKOUT_ABOUT_YOU);
        ((View) tab.getCustomView().getParent()).setOnClickListener(clickListener);

        // Address
        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);
        tab2.setCustomView(R.layout.tab_address);
        ((View) tab2.getCustomView().getParent()).setTag(ConstantsCheckout.CHECKOUT_BILLING);
        ((View) tab2.getCustomView().getParent()).setOnClickListener(clickListener);

        // Shipping
        TabLayout.Tab tab3 = tabLayout.newTab();
        tabLayout.addTab(tab3);
        tab3.setCustomView(R.layout.tab_shipping);
        ((View) tab3.getCustomView().getParent()).setTag(ConstantsCheckout.CHECKOUT_SHIPPING);
        ((View) tab3.getCustomView().getParent()).setOnClickListener(clickListener);

        // Payment
        TabLayout.Tab tab4 = tabLayout.newTab();
        tabLayout.addTab(tab4);
        tab4.setCustomView(R.layout.tab_payment);
        ((View) tab4.getCustomView().getParent()).setTag(ConstantsCheckout.CHECKOUT_PAYMENT);
        ((View) tab4.getCustomView().getParent()).setOnClickListener(clickListener);

        // Set listener
        tabLayout.setOnTabSelectedListener(listener);
    }

    public static boolean isNavigationActionWithTabLayout(@NavigationAction.Type int action) {
        return action == NavigationAction.HOME || action == NavigationAction.SAVED || action == NavigationAction.BASKET;
    }

    public static int getTabPosition(@NavigationAction.Type int action) {
        // Case Home
        if (action == NavigationAction.HOME) {
            return 0;
        }
        // Case Basket
        else if (action == NavigationAction.SAVED) {
            return 1;
        }
        // Case Basket
        else {
            return 2;
        }
    }

    public static void tabSelected(BaseActivity activity, TabLayout.Tab tab, @NavigationAction.Type int action) {
        int pos = tab.getPosition();
        // Case Home
        if (pos == 0 && action != NavigationAction.HOME) {
            activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case Saved
        else if (pos == 1 && action != NavigationAction.SAVED) {
            activity.onSwitchFragment(FragmentType.WISH_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case Basket
        else if (pos == 2 && action != NavigationAction.BASKET) {
            activity.onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Method used to add a bottom margin with tool bar size.<br>
     * Because the Coordinator Layout first build the without tool bar size.<br>
     * And after add the tool bar and translate the view to below.
     */
    public static void setViewWithoutNestedScrollView(View view, @NavigationAction.Type int action) {
        // Case others
        if (action != NavigationAction.BASKET &&
                action != NavigationAction.SAVED &&
                action != NavigationAction.HOME &&
                action != NavigationAction.CATALOG &&
                action != NavigationAction.CAMPAIGN &&
                view != null) {
            TypedValue tv = new TypedValue();
            Context context = view.getContext();
            if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.bottomMargin += actionBarHeight;
            }
        }
    }

    public static void updateTabCartInfo(TabLayout mTabLayout) {
        // Update the cart tab
        try {
            // Show 0 while the cart is not updated
            String quantity = JumiaApplication.INSTANCE.getCart() == null ? "0" : String.valueOf(JumiaApplication.INSTANCE.getCart().getCartCount());
            //noinspection ConstantConditions
            int pos = getTabPosition(NavigationAction.BASKET);
            //noinspection ConstantConditions
            ((TextView) mTabLayout.getTabAt(pos).getCustomView().findViewById(R.id.action_cart_count)).setText(quantity);
        } catch (NullPointerException e) {
            // ...
        }
    }

}

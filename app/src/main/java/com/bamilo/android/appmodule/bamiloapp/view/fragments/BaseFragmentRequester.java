package com.bamilo.android.appmodule.bamiloapp.view.fragments;


import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;

import java.util.Set;

/**
 * Base requester fragment used for fragments that needs perform a network/database request.
 * @author spereira
 */
public abstract class BaseFragmentRequester extends BaseFragmentAutoState implements IResponseCallback {

    /*
     * ##### CONSTRUCTORS #####
     */

    public BaseFragmentRequester(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState);
    }

    public BaseFragmentRequester(Boolean isNestedFragment, @LayoutRes int layoutResId) {
        super(isNestedFragment, layoutResId);
    }

    public BaseFragmentRequester(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState, titleCheckout);
    }

    /*
     * ##### REQUEST CALLBACK #####
     */

    /**
     * On Success
     */
    @Override
    public final void onRequestComplete(BaseResponse response) {
        // Validate the current UI state
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Super handle
        if(onHandleSuccess(response)) {
            showFragmentContentContainer();
            return;
        }
        // Delegate
        onSuccessResponse(response);
    }

    /**
     * On Error
     */
    @Override
    public final void onRequestError(BaseResponse response) {
        // Validate the current UI state
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Super handle
        if(onHandleError(response)) {
            return;
        }
        // Delegate
        onErrorResponse(response);
    }

    /*
     * ##### HANDLE SUPER FRAGMENT #####
     */

    private boolean onHandleSuccess(BaseResponse response) {
        // TODO: Move the super method to here
        return super.handleSuccessEvent(response);
    }

    private boolean onHandleError(final BaseResponse response) {
        // TODO: Move the super method to here
        return super.handleErrorEvent(response);
    }

    /*
     * ##### DERIVED FRAGMENT #####
     */

    /**
     * Derived fragment intercept success response
     */
    protected abstract void onSuccessResponse(BaseResponse response);

    /**
     * Derived fragment intercept error response
     */
    protected abstract void onErrorResponse(BaseResponse response);

}

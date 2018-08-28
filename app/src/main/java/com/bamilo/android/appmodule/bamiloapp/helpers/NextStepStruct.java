package com.bamilo.android.appmodule.bamiloapp.helpers;

import android.support.annotation.Nullable;

import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepObject;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;

/**
 * Created by rsoares on 9/22/15.
 */
public class NextStepStruct {

    private CheckoutStepObject checkoutStepObject;

    private FragmentType fragmentType;

    public NextStepStruct() {
    }

    public NextStepStruct(@Nullable CheckoutStepObject checkoutStepObject) {
        this.checkoutStepObject = checkoutStepObject;
        if(checkoutStepObject != null) {
            fragmentType = CheckoutStepManager.getNextFragment(checkoutStepObject.getNextCheckoutStep());
        } else {
            fragmentType = FragmentType.UNKNOWN;
        }
    }

    public CheckoutStepObject getCheckoutStepObject() {
        return checkoutStepObject;
    }

    public FragmentType getFragmentType() {
        return fragmentType;
    }

}

package com.mobile.helpers;

import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.utils.CheckoutStepManager;

/**
 * Created by rsoares on 9/22/15.
 */
public class NextStepStruct {
    private CheckoutStepObject checkoutStepObject;
    private FragmentType fragmentType;

    public NextStepStruct() {
    }

    public NextStepStruct(CheckoutStepObject checkoutStepObject) {
        this.checkoutStepObject = checkoutStepObject;
        fragmentType = CheckoutStepManager.getNextFragment(checkoutStepObject.getNextStep());
    }

    public CheckoutStepObject getCheckoutStepObject() {
        return checkoutStepObject;
    }

    public FragmentType getFragmentType() {
        return fragmentType;
    }

}

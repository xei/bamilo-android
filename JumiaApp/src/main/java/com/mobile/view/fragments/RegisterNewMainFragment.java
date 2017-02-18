package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.EnumSet;
import java.util.Set;

/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 */
public class RegisterNewMainFragment extends NewBaseFragment implements IResponseCallback{


    public RegisterNewMainFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_register_session_fragment, container, false);
    }
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }
}

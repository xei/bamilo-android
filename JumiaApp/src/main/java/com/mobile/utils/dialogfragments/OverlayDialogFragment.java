package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;

/**
 * 
 * @author Paulo Carvalho
 * 
 */
public class OverlayDialogFragment extends DialogFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(OverlayDialogFragment.class);

    private static OverlayDialogFragment sOverlayFragment;

    private int mOverlayLayout;

    public static OverlayDialogFragment getInstance(int overlayLayout) {
        sOverlayFragment = new OverlayDialogFragment();
        sOverlayFragment.mOverlayLayout = overlayLayout;
        return sOverlayFragment;
    }

    /**
     * Empty constructor
     */
    public OverlayDialogFragment() {
        // ...
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Retain this fragment across configuration changes.
        //setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Print.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(mOverlayLayout, container);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view.findViewById(R.id.wizard_main_container).setOnClickListener(this);

    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Print.i(TAG, "ON CREATE DIALOG");
        // The content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        // Creating the full screen dialog
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        setCancelable(false);
        return dialog;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        dismissAllowingStateLoss();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
//        if (v.getId() == mWizardDismiss) {
//            WizardPreferences.changeState(getActivity(), mType);
//            dismiss();
//        }
    }

}

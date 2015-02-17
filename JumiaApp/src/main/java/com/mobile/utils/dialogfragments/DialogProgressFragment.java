package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.framework.utils.LoadingBarView;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogProgressFragment extends DialogFragment {
    
    private final static String TAG = LogTagHelper.create( DialogProgressFragment.class );
    
    private LoadingBarView loadingBarView;

    
    /**
     * Empty constructor
     */
    public DialogProgressFragment() {}
    
    /**
     * 
     * @return
     */
    public static DialogProgressFragment newInstance() {
        Log.d(TAG, "NEW INSTANCE");
        DialogProgressFragment dialogProgressFragment = new DialogProgressFragment();
        return dialogProgressFragment;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Jumia_Dialog_Progress, R.style.Theme_Jumia_Dialog_Progress);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_progress, container);
        loadingBarView = (LoadingBarView) view.findViewById(R.id.fragment_root_loading_gif);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        loadingBarView.startRendering();
        return view;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Hide title divider
        int dividerId = dialog.getContext().getResources().getIdentifier("titleDivider","id", "android");
        View divider = dialog.findViewById(dividerId);
        if(divider != null) divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        // Return layout
        return dialog;
    }
    
    /* (non-Javadoc)
     * @see android.app.Dialog#dismiss()
     */
    @Override
    public void dismiss() {
        super.dismiss();
        if(loadingBarView != null) loadingBarView.stopRendering();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        //this.dismiss();
    }
}

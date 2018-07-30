package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.mobile.service.utils.output.Print;
import com.mobile.view.R;

/**
 * @author sergiopereira
 */
public class DialogProgressFragment extends DialogFragment {

    private final static String TAG = DialogProgressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public DialogProgressFragment() {
        // ...
    }

    /**
     *
     */
    public static DialogProgressFragment newInstance() {
        Print.d(TAG, "NEW INSTANCE");
        return new DialogProgressFragment();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Custom_Dialog_Progress);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_progress, container);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        return view;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Hide title divider
        int dividerId = dialog.getContext().getResources()
                .getIdentifier("titleDivider", "id", "android");
        View divider = dialog.findViewById(dividerId);
        if (divider != null) {
            divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        }
        // Return layout
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
            // Trying fix https://rink.hockeyapp.net/manage/apps/33641/app_versions/143/crash_reasons/38911893?type=crashes
            // Or try this solution http://dimitar.me/android-displaying-dialogs-from-background-threads/
        } catch (IllegalStateException | WindowManager.BadTokenException ex) {
            Print.e(TAG, "Error showing Dialog", ex);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Dialog#dismiss()
     */
    @Override
    public void dismiss() {
        try {
            if (isAdded() && getContext() != null) {
                super.dismiss();
            }
        } catch (Exception e) {
            Log.e("dismiss dialog error", e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }
}

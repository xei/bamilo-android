package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments;

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
import com.bamilo.android.R;

/**
 * @author sergiopereira
 */
public class DialogProgressFragment extends DialogFragment {

    private final static String TAG = DialogProgressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public DialogProgressFragment() {
    }

    /**
     *
     */
    public static DialogProgressFragment newInstance() {
        return new DialogProgressFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Custom_Dialog_Progress);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_progress, container);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        return view;
    }

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
        } catch (IllegalStateException | WindowManager.BadTokenException ignored) {
        }
    }

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

    @Override
    public void onPause() {
        super.onPause();
    }
}

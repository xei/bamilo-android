package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.TextView;
import com.bamilo.android.R;

/**
 * Class used to create an dialog.
 * @author sergiopereira
 */
public class DialogGenericFragment extends DialogFragment {

    private static final String TAG = DialogGenericFragment.class.getSimpleName();

    private Boolean mainText;

    private Boolean secondaryText;

    private String title;

    private String content;

    private String buttonTitle1;

    private String buttonTitle2;

    private String buttonTitle3;

    private OnClickListener clickListener;

    /**
     * Create a new instance
     */
    public static DialogGenericFragment newInstance(
            Boolean main_text,
            Boolean secondary_text,
            String title,
            String content,
            String button1_title,
            String button2_title,
            OnClickListener click) {
        DialogGenericFragment dialogGenericFragment = new DialogGenericFragment();
        dialogGenericFragment.mainText = main_text;
        dialogGenericFragment.secondaryText = secondary_text;
        dialogGenericFragment.title = title;
        dialogGenericFragment.content = content;
        dialogGenericFragment.buttonTitle1 = button1_title;
        dialogGenericFragment.buttonTitle2 = button2_title;
        dialogGenericFragment.clickListener = click;
        return dialogGenericFragment;
    }

    /**
     * Create a new instance
     */
    public static DialogGenericFragment newInstance(
            Boolean main_text,
            Boolean secondary_text,
            String title,
            String content,
            String button1_title,
            String button2_title,
            String button3_title,
            OnClickListener click) {
        DialogGenericFragment dialogGenericFragment = new DialogGenericFragment();
        dialogGenericFragment.mainText = main_text;
        dialogGenericFragment.secondaryText = secondary_text;
        dialogGenericFragment.title = title;
        dialogGenericFragment.content = content;
        dialogGenericFragment.buttonTitle1 = button1_title;
        dialogGenericFragment.buttonTitle2 = button2_title;
        dialogGenericFragment.buttonTitle3 = button3_title;
        dialogGenericFragment.clickListener = click;
        return dialogGenericFragment;
    }

    /**
     * Empty constructor
     */
    public DialogGenericFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Custom_Dialog_NoTitle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (buttonTitle3 == null) {
            view = inflater.inflate(R.layout.dialog_generic, container);
            dialogWith2Buttons(view);
        } else {
            view = inflater.inflate(R.layout.dialog_generic_3buttons, container);
            dialogWith3Buttons(view);
        }
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException | WindowManager.BadTokenException ignored) {
        }
    }

    /**
     * Set the click listener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    /**
     * Dialog
     */
    private void dialogWith2Buttons(View view) {
        if (this.mainText != null && !this.mainText) {
            view.findViewById(R.id.dialog_text).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.items_count)).setText(content);
        }
        if (this.secondaryText != null && !this.secondaryText) {
            view.findViewById(R.id.items_count).setVisibility(View.GONE);
        }
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_text)).setText(content);
        view.findViewById(R.id.button1).setOnClickListener(clickListener);
        ((Button) view.findViewById(R.id.button1)).setText(buttonTitle1);
        if (TextUtils.isEmpty(buttonTitle2)) {
            view.findViewById(R.id.button2).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button2)).setText(buttonTitle2);
        view.findViewById(R.id.button2).setOnClickListener(clickListener);
        getActivity().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

        if (title == null)
            view.findViewById(R.id.dialog_title).setVisibility(View.GONE);
        else
            ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
    }

    /**
     * Dialog
     */
    private void dialogWith3Buttons(View view) {
        if (!mainText) {
            view.findViewById(R.id.dialog_text).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.items_count)).setText(content);
        }
        if (!secondaryText) {
            view.findViewById(R.id.items_count).setVisibility(View.GONE);
        }
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_text)).setText(content);
        view.findViewById(R.id.button1).setOnClickListener(clickListener);
        ((Button) view.findViewById(R.id.button1)).setText(buttonTitle1);
        if (TextUtils.isEmpty(buttonTitle2)) {
            view.findViewById(R.id.button2).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button2)).setText(buttonTitle2);
        view.findViewById(R.id.button2).setOnClickListener(clickListener);

        if (TextUtils.isEmpty(buttonTitle2)) {
            view.findViewById(R.id.button2).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button3)).setText(buttonTitle3);
        view.findViewById(R.id.button3).setOnClickListener(clickListener);
        getActivity().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
    }

    /**
     *
     */
    public static DialogGenericFragment createServerErrorDialog(
            final Activity activity,
            final OnClickListener retryClickListener,
            boolean finishActivity) {
        return createErrorDialog(
                activity.getString(R.string.server_error_title),
                activity.getString(R.string.server_error),
                activity,
                retryClickListener,
                finishActivity);
    }

    /**
     *
     */
    public synchronized static DialogGenericFragment createErrorDialog(
            final String title,
            final String message,
            final Activity activity,
            final OnClickListener retryClickListener,
            final boolean finishActivity) {
        final DialogGenericFragment dialog = DialogGenericFragment.newInstance(
                true,
                false,
                title,
                message,
                activity.getString(R.string.cancel_label),
                activity.getString(R.string.try_again),
                null);
        dialog.clickListener = v -> {
            int id = v.getId();
            if (id == R.id.button1) {
                if (finishActivity) {
                    activity.finish();
                } else {
                    dialog.dismissAllowingStateLoss();
                }
            } else if (id == R.id.button2) {
                retryClickListener.onClick(v);
            }
        };
        return dialog;
    }

    /**
     * Create an info dialog.
     */
    public static DialogGenericFragment createInfoDialog(String title, String message, String button) {

        final DialogGenericFragment dialog = DialogGenericFragment.newInstance(
                true,
                false,
                title,
                message,
                button,
                null, null);

        dialog.clickListener = v -> dialog.dismissAllowingStateLoss();

        return dialog;
    }

}

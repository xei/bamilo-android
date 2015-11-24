package com.mobile.utils.dialogfragments;

import android.app.Activity;
import android.os.Bundle;
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

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * 
 * @author sergiopereira
 * 
 */
public class DialogGenericFragment extends DialogFragment {

    private static final String TAG = DialogGenericFragment.class.getSimpleName();

    private static DialogGenericFragment dialogGenericFragment;

    // private Boolean hasHeader;

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
     * 
     * @param main_text
     * @param secondary_text
     * @param title
     * @param content
     * @param button1_title
     * @param button2_title
     * @param click
     * @return
     */
    public static DialogGenericFragment newInstance(
            Boolean main_text,
            Boolean secondary_text,
            String title,
            String content,
            String button1_title,
            String button2_title,
            OnClickListener click) {

        Print.d(TAG, "NEW INSTANCE: 2 Buttons");

        dialogGenericFragment = new DialogGenericFragment();
        // dialogGenericFragment.hasHeader = has_header;
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
     * 
     * @param has_header
     * @param main_text
     * @param secondary_text
     * @param title
     * @param content
     * @param button1_title
     * @param button2_title
     * @param button3_title
     * @param click
     * @return
     */
    public static DialogGenericFragment newInstance(Boolean main_text,
                                                    Boolean secondary_text,
                                                    String title,
                                                    String content,
                                                    String button1_title,
                                                    String button2_title,
                                                    String button3_title, OnClickListener click) {

        Print.d(TAG, "NEW INSTANCE: 3 Buttons");

        DialogGenericFragment dialogGenericFragment = new DialogGenericFragment();
        // dialogGenericFragment.hasHeader = has_header;
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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Custom_Dialog_NoTitle, R.style.Theme_Custom_Dialog_NoTitle);
        Print.i(TAG, "ON CREATE");
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
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle arg0) {
        /**
         * FIXME: 
         * Try fix: https://rink.hockeyapp.net/manage/apps/85532/app_versions/31/crash_reasons/21454355?type=crashes
         */
        //super.onSaveInstanceState(arg0);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager,tag);
            // Trying fix https://rink.hockeyapp.net/manage/apps/33641/app_versions/143/crash_reasons/38911893?type=crashes
            // Or try this solution http://dimitar.me/android-displaying-dialogs-from-background-threads/
        } catch (IllegalStateException | WindowManager.BadTokenException ex){
            Print.e(TAG, "Error showing Dialog", ex);
        }
    }

    /**
     * Dialog
     * 
     * @param view
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
            view.findViewById(R.id.grey_line_vertical).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button2)).setText(buttonTitle2);
        view.findViewById(R.id.button2).setOnClickListener(clickListener);
        getActivity().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

        if (title == null)
            view.findViewById(R.id.title_container).setVisibility(View.GONE);
        else
            ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
    }

    /**
     * Dialog
     * 
     * @param view
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
            view.findViewById(R.id.grey_line_vertical).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button2)).setText(buttonTitle2);
        view.findViewById(R.id.button2).setOnClickListener(clickListener);

        if (TextUtils.isEmpty(buttonTitle2)) {
            view.findViewById(R.id.button2).setVisibility(View.GONE);
            view.findViewById(R.id.grey_line_vertical2).setVisibility(View.GONE);
        }
        ((Button) view.findViewById(R.id.button3)).setText(buttonTitle3);
        view.findViewById(R.id.button3).setOnClickListener(clickListener);
        getActivity().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
    }

    /**
     * 
     * @param click
     */
    public void setOnClickListener(android.view.View.OnClickListener click) {
        getView().findViewById(R.id.button1).setOnClickListener(click);
        getView().findViewById(R.id.button2).setOnClickListener(click);
    }

    /**
     * 
     * @param sequence
     */
    public void setText(CharSequence sequence) {
        ((TextView) getView().findViewById(R.id.dialog_text)).setText(sequence,
                TextView.BufferType.SPANNABLE);
    }

    /**
     * 
     * @param activity
     * @param retryClickListener
     * @param finishActivity
     * @return
     */
    public static DialogGenericFragment createNoNetworkDialog(final Activity activity,
            final android.view.View.OnClickListener retryClickListener,
            final android.view.View.OnClickListener cancelClickListener,
            final boolean finishActivity) {

        Print.d(TAG, "CREATE NO NETWORK DIALOG");

        return DialogGenericFragment.newInstance(true, false,
                activity.getResources().getString(R.string.no_internet_access_warning_title),
                activity.getResources().getString(R.string.no_connect_dialog_content),
                activity.getResources().getString(R.string.cancel_label),
                activity.getResources().getString(R.string.try_again),

                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if (finishActivity) {
                                activity.finish();
                            } else {
                                if (cancelClickListener != null) {
                                    cancelClickListener.onClick(v);
                                }
                            }

                        } else if (id == R.id.button2) {
                            if (retryClickListener != null) {
                                retryClickListener.onClick(v);
                            }
                        }
                    }
                });
    }

    /**
     * 
     * @param activity
     * @param retryClickListener
     * @param finishActivity
     * @return
     */
    public static DialogGenericFragment createServerErrorDialog(final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getString(
                R.string.server_error_title), activity.getString(
                R.string.server_error), activity, retryClickListener, finishActivity);
    }

    /**
     * 
     * @param message
     * @param activity
     * @param retryClickListener
     * @param finishActivity
     * @return
     */
    public static DialogGenericFragment createServerErrorDialog(String message,
            final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getResources().getString(R.string.server_error_title),
                message, activity,
                retryClickListener, finishActivity);
    }

    /**
     * 
     * @param title
     * @param message
     * @param activity
     * @param retryClickListener
     * @param finishActivity
     * @return
     */
    public synchronized static DialogGenericFragment createErrorDialog(final String title,
            final String message,
            final Activity activity,
            final android.view.View.OnClickListener retryClickListener, final boolean finishActivity) {

        Print.d(TAG, "CREATE ERROR DIALOG");

        return DialogGenericFragment.newInstance(true, false,
                title, message, activity
                        .getString(R.string.cancel_label), activity.getString(R.string.try_again),

                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if (finishActivity) {
                                activity.finish();
                            } else {
                                dialogGenericFragment.dismissAllowingStateLoss();
                            }
                        } else if (id == R.id.button2) {
                            retryClickListener.onClick(v);
                        }
                    }
                });
    }

    public static DialogGenericFragment createClientErrorDialog(String message,
            final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getResources().getString(
                R.string.client_error_title), message, activity, retryClickListener, finishActivity);
    }


}

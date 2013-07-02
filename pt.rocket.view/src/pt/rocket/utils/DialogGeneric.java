package pt.rocket.utils;

import pt.rocket.view.R;
import pt.rocket.view.ShoppingCartActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class DialogGeneric extends Dialog {

    /**
     * Creates a customizable dialog message box with the look and feel of android 4
     * 
     * @param activity
     *            The parent activity
     * @param has_header
     *            pass true if you want to show the dialog title, which includes the icon,title and
     *            ruler
     * @param main_text
     *            pass true it you want to show the main text which has a regular font
     * @param secondary_text
     *            pass true it you want to show the secondary text which has a bigger font (this is
     *            only used in the product description when adding a product
     * @param title
     *            The title of the dialog
     * @param content
     *            The message to display on the dialog
     * @param button1_title
     *            The text to display on the left button
     * @param button2_title
     *            The text to display on the right button
     * @param click
     *            The ClickListener to be fired whe the user presses one of the dialog buttons
     * @return The created dialog
     */
    public DialogGeneric(final Activity activity, Boolean has_header, Boolean main_text,
            Boolean secondary_text, String title, String content, String button1_title,
            String button2_title, android.view.View.OnClickListener click) {
        super(activity, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
        
        setContentView(R.layout.dialog_generic);

        if (!main_text) {
            ((TextView) findViewById(R.id.dialog_text)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.items_count)).setText(content);
        }
        if (!secondary_text) {
            ((TextView) findViewById(R.id.items_count)).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.dialog_title)).setText(title);
        ((TextView) findViewById(R.id.dialog_text)).setText(content);
        ((Button) findViewById(R.id.button1)).setOnClickListener(click);
        ((Button) findViewById(R.id.button1)).setText(button1_title);
        if (TextUtils.isEmpty(button2_title)) {
            ((Button) findViewById(R.id.button2)).setVisibility(View.GONE);
            ((View) findViewById(R.id.grey_line_vertical)).setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.button2)).setText(button2_title);
        ((Button) findViewById(R.id.button2)).setOnClickListener(click);
        getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
    }
    
    public void setOnClickListener( android.view.View.OnClickListener click ) {
        ((Button) findViewById(R.id.button1)).setOnClickListener(click);
        ((Button) findViewById(R.id.button2)).setOnClickListener(click);
    }
    
    public void setText( CharSequence sequence) {
        ((TextView) findViewById( R.id.dialog_text)).setText( sequence, TextView.BufferType.SPANNABLE );
    }

//    @Override
//    public void show() {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
//            resizeDialog();
//        super.show();
//    }
//
//    @SuppressWarnings("deprecation")
//    public void resizeDialog() {
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(getWindow().getAttributes());
//        lp.width = (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.9f);
//        lp.horizontalMargin = 0;
//        lp.verticalMargin = 0;
//        getWindow().setAttributes(lp);
//        getWindow().setBackgroundDrawable(new ColorDrawable(0));
//
//    }
    
    public static DialogGeneric createNoNetworkDialog(final Activity activity,
            final android.view.View.OnClickListener retryClickListener, final boolean finishActivity ) {
        final DialogGeneric dialog = new DialogGeneric(activity, true, true, false, activity.getResources().getString(
                R.string.no_internet_access_warning_title), activity.getResources().getString(
                R.string.no_connect_dialog_content), activity.getResources().getString(
                R.string.cancel_label), activity.getResources().getString(R.string.try_again),
                null );
        
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.button1) {
                    if ( finishActivity ) {
                        activity.finish();
                    } else {
                        dialog.dismiss();
                    }

                } else if (id == R.id.button2) {
                    retryClickListener.onClick(v);
                }
            }

        };
        
        dialog.setOnClickListener(click);
        return dialog;
    }
    
    public DialogGeneric(final Activity activity, Boolean has_header, Boolean main_text,
            Boolean secondary_text, String title, String content, String button1_title,
            String button2_title, String button3_title, 
            android.view.View.OnClickListener click) {
        super(activity, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
        
        setContentView(R.layout.dialog_generic_3buttons);

        if (!main_text) {
            ((TextView) findViewById(R.id.dialog_text)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.items_count)).setText(content);
        }
        if (!secondary_text) {
            ((TextView) findViewById(R.id.items_count)).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.dialog_title)).setText(title);
        ((TextView) findViewById(R.id.dialog_text)).setText(content);
        ((Button) findViewById(R.id.button1)).setOnClickListener(click);
        ((Button) findViewById(R.id.button1)).setText(button1_title);
        if (TextUtils.isEmpty(button2_title)) {
            ((Button) findViewById(R.id.button2)).setVisibility(View.GONE);
            ((View) findViewById(R.id.grey_line_vertical)).setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.button2)).setText(button2_title);
        ((Button) findViewById(R.id.button2)).setOnClickListener(click);
        
        if (TextUtils.isEmpty(button2_title)) {
            ((Button) findViewById(R.id.button2)).setVisibility(View.GONE);
            ((View) findViewById(R.id.grey_line_vertical2)).setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.button3)).setText(button3_title);
        ((Button) findViewById(R.id.button3)).setOnClickListener(click);
        getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
    }
    
    public static DialogGeneric createServerErrorDialog(final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getString(
                R.string.server_error_title), activity.getString(
                R.string.server_error), activity, retryClickListener, finishActivity);
    }
    
    public static DialogGeneric createErrorDialog(final String title, final String message,
            final Activity activity,
            final android.view.View.OnClickListener retryClickListener, final boolean finishActivity) {
        final DialogGeneric dialog = new DialogGeneric(activity, true, true, false, title, message, activity
                .getString( R.string.cancel_label), activity.getString(R.string.try_again), null);
        
        dialog.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.button1) {
                    if ( finishActivity ) {
                        activity.finish();
                    } else {
                        dialog.dismiss();
                    }
                } else if (id == R.id.button2) {
                    retryClickListener.onClick(v);
                }
            }
        });
        
        return dialog;
    }
    
    public static DialogGeneric createServerErrorDialog(String message, final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getResources().getString(
                R.string.server_error_title), message, activity, retryClickListener, finishActivity);
    }
    
    public static DialogGeneric createClientErrorDialog(String message, final Activity activity,
            final android.view.View.OnClickListener retryClickListener, boolean finishActivity) {
        return createErrorDialog(activity.getResources().getString(
                R.string.client_error_title), message, activity, retryClickListener, finishActivity);
    }
    
}

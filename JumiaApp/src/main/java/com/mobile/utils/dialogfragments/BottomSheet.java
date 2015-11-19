package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.mobile.view.R;

/**
 * Class used to set the BottomSheet style
 */
public class BottomSheet extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Custom_Dialog_Bottom_Sheet);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return dialog;
    }

    /**
     * Function that sets the size of the list of the bottom sheet, based on orientation and number
     * of items on the list
     */
    protected void setListSize (AbsListView view, int count){
        // Get max for orientation
        int max = getResources().getInteger(R.integer.dialog_max_items);
        // Get size
        if(count > max) {
            // Get item height
            int height = max * (int) getResources().getDimension(R.dimen.action_bar_height);
            // Set the new height
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
            view.setLayoutParams(params);
        }
    }

}

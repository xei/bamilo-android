package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * Class used to set the BottomSheet style
 */
public class BottomSheet extends DialogFragment {

    private static final int MAX_ITEM_LANDSCAPE = 4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Jumia_Dialog_Bottom_Sheet);
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
     * @param listView
     * @param itemCount
     */
    protected void setListSize (ListView listView, int itemCount){
        if(getActivity() != null){
            int maxItems = getResources().getInteger(R.integer.dialog_max_item);

            int height = (int) DeviceInfoHelper.getHeight(getActivity()) / 2;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

            if(itemCount > MAX_ITEM_LANDSCAPE && DeviceInfoHelper.isTabletInLandscape(getActivity())){ // verify table landscape
                listView.setLayoutParams(params);
                Print.i("setDialogSize", "LAND > 4: " + height);

            } else if (itemCount > maxItems && !DeviceInfoHelper.isTabletInLandscape(getActivity())) {
                Print.i("setDialogSize", "PORT MAX ITEM: " + maxItems);
                listView.setLayoutParams(params);
            }
        }
    }

}

package com.mobile.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.view.R;

import java.util.ArrayList;


/**
 * Created by alexandrapires on 9/17/15.
 */
/**
 * Class that shows a bottomsheet with Product Simple itens
 */
public class CustomBottomSheet extends DialogFragment implements DialogInterface, AdapterView.OnItemClickListener {

    private  Dialog dialog;

    private ProductSimple selectedSimple;

    private ListView gridView;

    private SheetListAdapter sheetListAdapter;

    /**
     * bundle that contains multiple simples
     */

    private static ProductBundle productBundle;

    /**
     * Options array in bundle product
     */
    private ArrayList<ProductSimple> mSimpleOptions;

    /**
     * bottom sheet title
     */
    private String variationTitle;

    private static Context context;

    /**
     * Listeners
     */

    private DialogInterface.OnDismissListener dismissListener;


    private View.OnClickListener listener;




    /**
     * Constructors
     */

    public static CustomBottomSheet newInstance() {
        CustomBottomSheet customBottomSheet = new CustomBottomSheet();
        return customBottomSheet;
    }


    /**
     * Constructor with parameters
     * @param context - context
     * @param productBundle - The bundle that has multiple simples
     * @param  variationName - title for bottomsheet
     */

    public static CustomBottomSheet newInstance(Context context, ProductBundle productBundle, String variationName) {
        CustomBottomSheet customBottomSheet = new CustomBottomSheet();
        customBottomSheet.setContext(context);
        customBottomSheet.productBundle = productBundle;
        customBottomSheet.mSimpleOptions = productBundle.getSimples();
        customBottomSheet.variationTitle = variationName;

        return customBottomSheet;
    }






    public void setContext(Context context)
    {
        this.context = context;
    }



    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener)
    {
        this.dismissListener = listener;
    }



    public ProductBundle getProductBundle()
    {
        return productBundle;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // This dialog instance is unique because otherwise in case user rotates the screen the caller fragment that has the
        // onclicklistener used by this dialog will be "paused" and a new one will be created and this dialog doesn't have a reference to it.
        // In case user rotates the screen the dialog automatically dismisses (by design)
        //TODO CHECK IF THIS IS NEEDED
//        setRetainInstance(true);

        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        if (savedInstanceState == null) {
            dialog.show();
        }

        return dialog;
    }


    @Override
    public void cancel() {

    }

    @Override
    public void onStart() {
        super.onStart();

        if(dialog == null)
            dialog = getDialog();

        /**
         * Sets dialog position in fragment
         */

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.custom_bottom_sheet, container, false);
        super.onViewCreated(view, savedInstanceState);

        //Sheet title
        TextView txVariationTitle = (TextView) view.findViewById(R.id.txVariationTitle);
        txVariationTitle.setText(variationTitle);

        //content
        gridView = (ListView) view.findViewById(R.id.gridBottomSheet);
        sheetListAdapter = new SheetListAdapter(mSimpleOptions,context);
        gridView.setAdapter(sheetListAdapter);
        gridView.setOnItemClickListener(this);


        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(dismissListener != null)
            dismissListener.onDismiss(dialog);

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager,tag);
        } catch (IllegalStateException | WindowManager.BadTokenException ex){
            //  Print.e(TAG, "Error showing Dialog", ex);
        }
    }





    public ProductSimple getSelectedSimple()
    {
        return selectedSimple;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //keep the selected option
        selectedSimple = this.mSimpleOptions.get(position);

        if(selectedSimple != null)
        {
            sheetListAdapter.notifyDataSetChanged();

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                }
            }, 500);
        }
    }
}

package com.mobile.utils.dialogfragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.framework.objects.Promotion;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author manuelsilva
 *
 */
public class DialogPromotionFragment extends DialogFragment {
    
    private final static String TAG = LogTagHelper.create( DialogPromotionFragment.class );

    private Promotion mPromotion;

    /**
     * Empty constructor
     */
    public DialogPromotionFragment() {}
    
    /**
     * 
     * @return
     */
    public static DialogPromotionFragment newInstance(Promotion promo) {
        Log.d(TAG, "NEW INSTANCE");
        DialogPromotionFragment dialogProgressFragment = new DialogPromotionFragment();
        dialogProgressFragment.mPromotion = promo;
        return dialogProgressFragment;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.PromotionDialog, R.style.PromotionDialog);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.promotion_layout, container);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        setLayout();
    }
    
    private void setLayout(){
        
        if(mPromotion != null && mPromotion.getTitle() != null){
            SharedPreferences sP = getActivity().getSharedPreferences(
                    Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor eD = sP.edit();
            eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, false);
            eD.apply();
            ((TextView) getView().findViewById(R.id.promotion_title)).setText(Html.fromHtml(mPromotion.getTitle()));
            ((TextView) getView().findViewById(R.id.promotion_coupon_code)).setText(Html.fromHtml(mPromotion.getCouponCode()));
            getView().findViewById(R.id.promotion_coupon_code).setOnClickListener(new OnClickListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onClick(View v) {

                    if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
                        android.text.ClipboardManager ClipMan = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipMan.setText(((TextView) v).getText());
                    } else {
                        ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipMan.setPrimaryClip(ClipData.newPlainText("simple text", ((TextView) v).getText()));

                    }

                    Toast.makeText(getActivity(), getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) getView().findViewById(R.id.promotion_description)).setText(Html.fromHtml(mPromotion.getDescription()));
            ((TextView) getView().findViewById(R.id.promotion_terms)).setText(Html.fromHtml(mPromotion.getTermsConditions()));
            getView().findViewById(R.id.promotion_go_shop).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }
    
    
    /* (non-Javadoc)
     * @see android.app.Dialog#dismiss()
     */
    @Override
    public void dismiss() {
        super.dismiss();
    }
    
    
    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }
}

package com.mobile.utils.dialogfragments;
//package com.mobile.utils.dialogfragments;
//
//
//import org.holoeverywhere.drawable.ColorDrawable;
//
//import com.mobile.newFramework.utils.LogTagHelper;
//import com.mobile.preferences.WizardPreferences;
//import com.mobile.preferences.WizardPreferences.WizardType;
//import com.mobile.view.R;
//import android.app.Dialog;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.RelativeLayout;
//
//import com.viewpagerindicator.IconPagerAdapter;
//import com.viewpagerindicator.PageIndicator;
//
//import com.mobile.newFramework.utils.output.Log;
//
///**
// * 
// * @author sergiopereira
// * 
// */
//public class WizardPagerFragment extends DialogFragment implements OnClickListener {
//
//    private static final String TAG = LogTagHelper.create(WizardPagerFragment.class);
//
//    private static WizardPagerFragment wizardGenericFragment;
//
//    protected Boolean hasHeader;
//
//    private int[] layouts;
//
//    private WizardType type;
//    
//
//    public static WizardPagerFragment getInstance(int[] layouts, WizardType type) {
//        wizardGenericFragment = new WizardPagerFragment();
//        wizardGenericFragment.layouts = layouts;
//        wizardGenericFragment.type = type;
//        return wizardGenericFragment;
//    }
//
//    /**
//     * Empty constructor
//     */
//    public WizardPagerFragment() {
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "ON CREATE");
//        // Retain this fragment across configuration changes.
//        // setRetainInstance(true);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//     * android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        Log.i(TAG, "ON CREATE VIEW");
//        return inflater.inflate(R.layout.wizard_view_pager, container);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Get views
//        view.findViewById(R.id.wizard_gallery_button).setOnClickListener(this);
//        ViewPager pager = (ViewPager) view.findViewById(R.id.wizard_gallery_view_pager);
//        PageIndicator indicator = (PageIndicator) view.findViewById(R.id.wizard_gallery_indicator);
//        // Set adapter
//        WizardPagerAdapter wizardPagerAdapter = new WizardPagerAdapter(getLayoutInflater(savedInstanceState), layouts);
//        pager.setAdapter(wizardPagerAdapter);
//        indicator.setViewPager(pager);
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Log.i(TAG, "ON CREATE DIALOG");
//        // The content
//        final RelativeLayout root = new RelativeLayout(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        // Creating the full screen dialog
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        return dialog;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        this.dismiss();
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.wizard_gallery_button){
//            WizardPreferences.changeState(getActivity(), type);
//            dismiss();
//        }
//    }
//    
//    
//    private class WizardPagerAdapter extends PagerAdapter implements IconPagerAdapter {
//
//        LayoutInflater inflater;
//        int[] layouts;
//        
//        public WizardPagerAdapter(LayoutInflater inflater, int[] layouts) {
//            this.inflater = inflater;
//            this.layouts = layouts;
//           }
//        
//
//        @Override
//        public int getCount() {
//            return layouts == null ? 0 : layouts.length;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View view = inflater.inflate(layouts[position], null);
//            ((ViewPager) container).addView(view, 0);
//            return view;
//        }
//        
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            ((ViewPager) container).removeView((View) object);
//        }
//        
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((View) object);
//        }
//
//        @Override
//        public int getIconResId(int index) {
//            return R.drawable.shape_pageindicator;
//        }
//        
//    }
//
//}

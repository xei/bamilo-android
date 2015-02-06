package com.mobile.utils;
//package com.mobile.utils;
//
//import java.util.ArrayList;
//
//import com.mobile.framework.objects.CompleteProduct;
//import com.mobile.view.fragments.BaseFragment.OnFragmentActivityInteraction;
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import de.akquinet.android.androlog.Log;
//
///**
// * Interface to communicate between fragments using activity has initializer.
// * @author manuelsilva
// *
// */
//public class FragmentCommunicator {
//    private static final String TAG = "ProductDetailsFragmentCommunicator";
//
//    private static FragmentCommunicator mProductDetailsFragmentCommunicator;
//    
//    //private static OnFragmentActivityInteraction mActivityCallback = null;
//    private static ArrayList<OnActivityFragmentInteraction> mFragmentsCallback = null;
//    private static ArrayList<String> mFragmentsCallbackNames = null;
//    
//    public static String ACTION_IDENTIFIER = "action_identifier";
//    private CompleteProduct currentProduct;
//    
//    // used to stay on the same image on product gallery
//    private int currentImagePosition = 0;
//    
//    public static FragmentCommunicator getInstance() {
//        if(mProductDetailsFragmentCommunicator == null){
//            mProductDetailsFragmentCommunicator = new FragmentCommunicator();
//        }
//        return mProductDetailsFragmentCommunicator;
//    }
//    
//    public void destroyInstance(){
//        mProductDetailsFragmentCommunicator = null;
//        mFragmentsCallback = null;
//        mFragmentsCallbackNames = null;
//        currentProduct = null;
//    }
//    
//    private FragmentCommunicator(){
//        mFragmentsCallback = new ArrayList<OnActivityFragmentInteraction>();
//        mFragmentsCallbackNames = new ArrayList<String>();
//    }
//    
//    public void startFragmentsCallBacks(Fragment... mFragments){
//        for (Fragment fragment : mFragments) {
//         // This makes sure that the container activity has implemented
//            // the callback interface. If not, it throws an exception
//            try {
//                mFragmentsCallback.add((OnActivityFragmentInteraction) fragment);
//                mFragmentsCallbackNames.add(fragment.toString());
//                Log.i(TAG, "register fragment : "+fragment.toString());
//            } catch (ClassCastException e) {
//                throw new ClassCastException(fragment.toString()
//                        + " must implement OnActivityFragmentInteraction");
//            }
//        }
//    }
//    
////    public void defineActivityCallBack(Activity activity){
////        // This makes sure that the container activity has implemented
////        // the callback interface. If not, it throws an exception
////        try {
////            mActivityCallback = (OnFragmentActivityInteraction) activity;
////        } catch (ClassCastException e) {
////            throw new ClassCastException(activity.toString()
////                    + " must implement OnActivityFragmentInteraction");
////        }
////    }
//    
//    public void updateCurrentProduct(CompleteProduct product){
//        Log.i(TAG, " --- updateCurrentProduct --- "+product.getName());
//        this.currentProduct = product;
//    }
//    
//    public CompleteProduct getCurrentProduct(){
//        return this.currentProduct;
//    }
//    
//    public void notifyOthers(int position, Bundle bundle){
//        for (int i = 0; i < mFragmentsCallback.size(); i++) {
//            if(i!=position){
//                mFragmentsCallback.get(i).notifyFragment(bundle);
//            }
//        }
//    }
//    
//    public void notifyTarget(Fragment fragment, Bundle bundle, int position){
//        //Log.i(TAG, "notify fragment : "+fragment.toString());
//        try {
//            mFragmentsCallback.get(position).notifyFragment(bundle);
//        } catch (IndexOutOfBoundsException e) {
//            Log.w(TAG, "WARNING IOBE ON NOTIFY FRAGMENT", e);
//        }
//    }
//
//    public int getCurrentImagePosition() {
//        return currentImagePosition;
//    }
//
//    public void setCurrentImagePosition(int currentImagePosition) {
//        this.currentImagePosition = currentImagePosition;
//    }
//}

package pt.rocket.utils;

import java.util.ArrayList;

import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.view.fragments.BaseFragment.OnFragmentActivityInteraction;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Interface to communicate between fragments using activity has initializer.
 * @author manuelsilva
 *
 */
public class ProductDetailsFragmentCommunicator {
    private static final String TAG = "ProductDetailsFragmentCommunicator";

    private static ProductDetailsFragmentCommunicator mProductDetailsFragmentCommunicator;
    
    private static OnFragmentActivityInteraction mActivityCallback = null;
    private static ArrayList<OnActivityFragmentInteraction> mFragmentsCallback = null;
    private static ArrayList<String> mFragmentsCallbackNames = null;
    
    public static String ACTION_IDENTIFIER = "action_identifier";
    private CompleteProduct currentProduct;
    
    
    public static ProductDetailsFragmentCommunicator getInstance() {
        if(mProductDetailsFragmentCommunicator == null){
            mProductDetailsFragmentCommunicator = new ProductDetailsFragmentCommunicator();
        }
        return mProductDetailsFragmentCommunicator;
    }
    
    public void destroyInstance(){
        mProductDetailsFragmentCommunicator = null;
        mFragmentsCallback = null;
        mFragmentsCallbackNames = null;
        currentProduct = null;
    }
    
    private ProductDetailsFragmentCommunicator(){
        this.mFragmentsCallback = new ArrayList<OnActivityFragmentInteraction>();
        this.mFragmentsCallbackNames = new ArrayList<String>();
    }
    
    public void startFragmentsCallBacks(Fragment... mFragments){
        for (Fragment fragment : mFragments) {
         // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                this.mFragmentsCallback.add((OnActivityFragmentInteraction) fragment);
                this.mFragmentsCallbackNames.add(fragment.toString());
                Log.i(TAG, "register fragment : "+fragment.toString());
            } catch (ClassCastException e) {
                throw new ClassCastException(fragment.toString()
                        + " must implement OnActivityFragmentInteraction");
            }
        }
    }
    
    public void defineActivityCallBack(Activity activity){
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            this.mActivityCallback = (OnFragmentActivityInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnActivityFragmentInteraction");
        }
    }
    
    public void updateCurrentProduct(CompleteProduct product){
        Log.i(TAG, " --- updateCurrentProduct --- "+product.getName());
        this.currentProduct = product;
    }
    
    public CompleteProduct getCurrentProduct(){
        return this.currentProduct;
    }
    
    public void notifyOthers(int position, Bundle bundle){
        for (int i = 0; i < this.mFragmentsCallback.size(); i++) {
            if(i!=position){
                this.mFragmentsCallback.get(i).notifyFragment(bundle);
            }
        }
    }
    
    public void notifyTarget(Fragment fragment, Bundle bundle, int position){
        Log.i(TAG, "notify fragment : "+fragment.toString());
        this.mFragmentsCallback.get(position).notifyFragment(bundle);
        
    }
}

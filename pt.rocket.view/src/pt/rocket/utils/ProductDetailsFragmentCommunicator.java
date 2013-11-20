package pt.rocket.utils;

import java.util.ArrayList;

import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.view.fragments.BaseFragment.OnFragmentActivityInteraction;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Interface to communicate between fragments using activity has initializer.
 * @author manuelsilva
 *
 */
public class ProductDetailsFragmentCommunicator {
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
        this.currentProduct = product;
    }
    
    public CompleteProduct getCurrentProduct(){
        return this.currentProduct;
    }
    
    public void notifyOthers(Fragment fragment, Bundle bundle){
        for (int i = 0; i < this.mFragmentsCallback.size(); i++) {
            if(!this.mFragmentsCallbackNames.get(i).equalsIgnoreCase(fragment.toString())){
                this.mFragmentsCallback.get(i).notifyFragment(bundle);
            }
        }
    }
    
    public void notifyTarget(Fragment fragment, Bundle bundle){
        for (int i = 0; i < this.mFragmentsCallback.size(); i++) {
            if(this.mFragmentsCallbackNames.get(i).equalsIgnoreCase(fragment.toString())){
                this.mFragmentsCallback.get(i).notifyFragment(bundle);
            }
        }
    }
}

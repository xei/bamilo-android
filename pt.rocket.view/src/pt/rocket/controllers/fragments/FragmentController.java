package pt.rocket.controllers.fragments;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import de.akquinet.android.androlog.Log;

/**
 * This class is responsible to controller the fragment transition, to switch fragments on UI with back stack support.
 * Uses the back stack from support to get the instances of fragments but the behaviour of the back is performed using the our LinkedList.
 * Was added a middle layer (LinkedList) to simulate the flag FLAG_ACTIVITY_REORDER_TO_FRONT.
 *   
 * @author sergiopereira
 * @see <a href="http://stackoverflow.com/questions/16963642/how-to-implement-a-intent-flag-activity-reorder-to-front-for-a-fragment-backstac">stackoverflow</a> 
 *
 */
public class FragmentController {

    private static final String TAG = LogTagHelper.create(FragmentController.class);
    
    private static final boolean ANIMATION_IN = true;
    
    private static final boolean ANIMATION_OUT = false;
    
    public static final Boolean ANIMATED = true;
    
    public static final Boolean NO_ANIMATED = false;
    
    public static final Boolean ADD_TO_BACK_STACK = true;
    
    public static final Boolean NO_ADD_TO_BACK_STACK = false;
    
    public static final Bundle NO_BUNDLE = null;
    
    public static final boolean INCLUSIVE = true;

    public static final boolean NO_INCLUSIVE = false;
    
    private final int POP_BACK_STACK_NO_INCLUSIVE = 0;

    private static FragmentController fragmentController;
    
    private LinkedList<String> backStack = new LinkedList<String>();
    
    DialogGenericFragment dialog; 
    /**
     * ##################### CONSTRUCTOR #####################
     */
    
    /**
     * Method used to get the instance of FragmentController
     * @author sergiopereira
     * @return {@link FragmentController}
     */
    public static FragmentController getInstance(){
        if (fragmentController == null)
            fragmentController = new FragmentController();
        return fragmentController;
    }

    /**
     * Empty constructor
     */
    public FragmentController() { 
        //...
    }

    
    public void init(){
        this.backStack.clear();
    }
    
    /**
     * ##################### GETTERS AND SETTERS #####################
     */
    
    /**
     * Get the back stack size
     * @author sergiopereira
     * @return {@link Integer}
     */
    private int getBackStackSize(){
        return backStack.size();
    }
    
    /**
     * Remove all entries
     * @author sergiopereira
     */
    private void cleanBackStack(){
        backStack.clear();
    }
    
    /**
     * Add the tag of fragment to back stack
     * @param tag
     */
    private synchronized void addToBackStack(String tag) {
        Log.i(TAG, "ADD TO BACK STACK: " + tag);
        this.backStack.addLast(tag);
    }
    
    /**
     * Remove the last entry
     */
    public synchronized void popLastEntry() {
        if(!backStack.isEmpty())
            backStack.removeLast();
    }
    
    /**
     * Get the last entry
     * @return {@link String}
     */
    public synchronized String getLastEntry(){
//        Log.i(TAG, "GET LAST ENTRY: " + backStack.getLast());
        String lastElement = "";
        try {
            if (!backStack.isEmpty()) {
                lastElement = this.backStack.getLast();
            }
        } catch (NoSuchElementException  e) {
            Log.i(TAG, "GET LAST ENTRY: ERROR list empty");
            lastElement = "";
        }
        return lastElement;
    }
    
    /**
     * Remove all old entries
     * @param tag
     */
    private synchronized void removeAllEntriesWithTag(String tag) {
        Log.i(TAG, "REMOVE OLD ENTRIES: " + tag);
        Iterator<String> iterator = backStack.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(tag))
                iterator.remove();
        }
    }
    
    /**
     * Print all entries
     */
    public void printAllEntries(){
        Iterator<String> iterator = backStack.iterator();
        while (iterator.hasNext()) {
            Log.d(TAG, "ENTRY: " + iterator.next());
        }
    }
    
    /**
     * Search for a tag
     * @param tag
     * @return
     */
    public Boolean hasEntry(String tag){
        return (backStack != null) ? backStack.contains(tag) : false ;
    }
    
    /**
     * Method used to remove entries until a respective tag of fragment
     * @param tag
     * @return
     */
    private String removeEntriesUntilTag(final String tag) {
        Log.i(TAG, "POP ENTRIES UNTIL: " + tag);
        /**
         * The idea is use a thread to run in parallel with the FragmentManager not blocking the main thread
         * FragmentManager has a delay to replace fragments, so i created this method.
         * @author sergiopereira
         * TODO - Threads have costs, find a workaround
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Remove tag if not equal
                while (!getLastEntry().equals(tag) && !getLastEntry().equals(""))
                    popLastEntry();
            }
        }).start();
        return null;
    }

    /**
     * ##################### PUSH #####################
     */

    /**
     * Add the tag to the back stack removing duplicates 
     * @param tag
     */
    public synchronized void addEntryToBackStack(final String tag) {
        Log.d(TAG, "ADD ENTRY TO BACK STACK");
        /**
         * The idea is use a thread to run in parallel with the FragmentManager not blocking the main thread
         * FragmentManager has a delay to replace fragments, so i created this method.
         * @author sergiopereira
         * TODO - Threads have costs, find a workaround
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                removeAllEntriesWithTag(tag);
                addToBackStack(tag);
            }
        }).start();        
    }
    
    /**
     * Start transition to the new fragment with animation
     * @param activity
     * @param container
     * @param fragment
     * @param tag
     * @param addToBackStack
     */
    public void startTransition(BaseActivity activity, int container, Fragment fragment, String tag, Boolean addToBackStack){
        startTransition(activity, container, fragment, tag, addToBackStack, ANIMATION_IN);
    }
    
    /**
     * ##################### POP #####################
     */
    
    /**
     * Method used to perform a back stack using fragments
     * @author sergiopereira
     */
    public void fragmentBackPressed(BaseActivity activity){
        int size = getBackStackSize();
        Log.i(TAG, "BACK STACK SIZE: " + size);
        switch (size) {
        case 1:
            /**
             * In this point if fragment type isn't HOME then something wrong is happening, to fix show the HOME
             * @author sergiopereira
             */
            if (getLastEntry().equals(FragmentType.HOME.toString())) 
                activity.doubleBackPressToExit();
            else 
                activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            
            break;
        case 0:
            activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            popBackStack(activity);
        }

    }
   
    /**
     * Simulate the back pressed removing the current fragment
     * @param activity
     */
    private void popBackStack(BaseActivity activity) {
        Log.i(TAG, "POP BACK STACK");
        // Pop the last fragment
        popLastEntry();
        // Get the new last fragment
        String lastTag = getLastEntry();
        if ( !lastTag.equals("") ) {
            Log.i(TAG, "code1 lastTag is : "+lastTag);
            // Pop stack until fragment tag
            activity.getSupportFragmentManager().popBackStackImmediate(lastTag, POP_BACK_STACK_NO_INCLUSIVE);
        }
        // Find fragment on Fragment Manager
        //Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(lastTag);
        // Replace the current fragment
        //startTransition(activity, R.id.main_fragment_container, fragment, lastTag, NO_ADD_TO_BACK_STACK, ANIMATION_OUT);
    }
    
    /**
     * Pop all back stack
     * @param tag
     */
    public void popAllBackStack(BaseActivity activity, String tag){
        Log.d(TAG, "POP ALL BACK STACK: " + getBackStackSize() + " MANAGER:" + activity.getSupportFragmentManager().getBackStackEntryCount());        
        // Pop all our back stack
        cleanBackStack();
        // Pop all back stack
        try {
            Log.d(TAG, "POP");
            activity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //activity.getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //addToBackStack(tag);
            //activity.getSupportFragmentManager().popBackStackImmediate(tag, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Pop all entries until tag, inclusive or not the tag from back stack
     * @param activity
     * @param tag
     * @param inclusive
     */
    public void popAllEntriesUntil(BaseActivity activity, String tag) {
        Log.d(TAG, "POP ALL ENTRIES UNTIL: " + tag + " " + getBackStackSize());
        // Get the new last fragment
        // String lastTag = 
        removeEntriesUntilTag(tag);
        
        try {
            // Pop stack until fragment tag
            activity.getSupportFragmentManager().popBackStackImmediate(tag, POP_BACK_STACK_NO_INCLUSIVE);   
        } catch (IllegalStateException e) {
            Log.d(TAG, "POP ALL ENTRIES UNTIL: ERROR IllegalStateException");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        // Find fragment on Fragment Manager
//        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(lastTag);
//        // Replace the current fragment
//        startTransition(activity, R.id.main_fragment_container, fragment, lastTag, NO_ADD_TO_BACK_STACK, ANIMATION_OUT);
    }
    
    
    /**
     * ##################### SWITCH #####################
     */

    /**
     * Method used to switch fragment on UI with back stack support
     * 
     * @param fragment
     * @param addToBackStack
     * @author sergiopereira
     */
    private void startTransition(BaseActivity activity, int container, Fragment fragment, String tag, Boolean addToBackStack, Boolean animation) {
        Log.d(TAG, "TRANSITION");
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        // Animations
        if (animation == ANIMATION_IN)
            fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in, R.anim.pop_out);
            //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        if (animation == ANIMATION_OUT)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // BackStack
        if (addToBackStack) {
            // Add the fragment to back stack
            fragmentTransaction.addToBackStack(tag);
            // Add the fragment to our back stack
            addEntryToBackStack(tag);
        }
        // Commit
        fragmentTransaction.commit();
        // API 11 :
        //fragmentTransaction.commitAllowingStateLoss();
    }
    
    
    /**
     * ##################### OLD METHODS #####################
     */
    
    /**
     * Method used to switch fragment on UI with/without back stack support
     * 
     * @param fragment
     * @param addToBackStack
     * @author sergiopereira
     */
    @Deprecated
    public void fragmentManagerTransition(BaseActivity activity, int container, Fragment fragment, String tag, Boolean addToBackStack, boolean animated) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        // Animations
        if(animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // BackStack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(tag);
        // Commit
        fragmentTransaction.commit();
        
        int backStackSize = activity.getSupportFragmentManager().getBackStackEntryCount();
        Log.d("BA Fragment", "FRAGMENT BACK STACK SIZE: " + backStackSize);        
    }
    
    
    /**
     * Method used to perform a back stack using fragments
     * @author sergiopereira
     */
    @Deprecated
    public void fragmentManagerBackPressed(BaseActivity activity){
        int backStackSize = activity.getSupportFragmentManager().getBackStackEntryCount();
        Log.d("BA Fragment", "FRAGMENT BACK STACK SIZE: " + backStackSize);
        if (backStackSize == 1)
            activity.finish();
        else {
            //getSupportFragmentManager().popBackStack();
            activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }
    
    /**
     * 
     * @param activity
     * @param tag
     */
    @Deprecated
    public void popAllBackStack(BaseActivity activity, String tag, int i){
        activity.getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    
    /**
     * 
     * @param activity
     * @param tag
     * @param inclusive
     */
    @Deprecated
    public void popBackStackUntilTag(BaseActivity activity, String tag, boolean inclusive) {
        // getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activity.getSupportFragmentManager().popBackStackImmediate(tag, 0);
    }
    
}

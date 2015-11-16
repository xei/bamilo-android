package com.mobile.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.WorkerThread;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

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

    private static final String TAG = FragmentController.class.getSimpleName();
    
    private static final boolean ANIMATION_IN = true;
    
    private static final boolean ANIMATION_OUT = false;
    
    public static final Boolean ADD_TO_BACK_STACK = true;
    
    public static final Bundle NO_BUNDLE = null;
    
    private final int POP_BACK_STACK_NO_INCLUSIVE = 0;

    private static FragmentController fragmentController;
    
    private ConcurrentLinkedDeque<String> backStack = new ConcurrentLinkedDeque<>();

    protected WorkerThread mWorkerThread;
    
    /**
     * ##################### CONSTRUCTOR #####################
     */
    
    /**
     * Method used to get the instance of FragmentController
     * @author sergiopereira
     * @return {@link FragmentController}
     */
    public static FragmentController getInstance(){
        return fragmentController == null ? fragmentController = new FragmentController() : fragmentController;
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
     * @param tag The fragment tag
     */
    private void addToBackStack(String tag) {
        Print.i(TAG, "ADD TO BACK STACK: " + tag);
        this.backStack.addLast(tag);
    }
    
    /**
     * Remove the last entry
     */
    public void popLastEntry() {
        if(!backStack.isEmpty())
            backStack.removeLast();
    }
    
    /**
     * Remove the last entry if is the tag
     * @param tag The fragment tag
     */
    public void popLastEntry(String tag){
        if(!backStack.isEmpty() && backStack.getLast().equals(tag))
            backStack.removeLast();
    }
    
    /**
     * Get the last entry
     * @return {@link String}
     */
    public String getLastEntry(){
//        Log.i(TAG, "GET LAST ENTRY: " + backStack.getLast());
        String lastElement = "";
        try {
            if (!backStack.isEmpty()) {
                lastElement = this.backStack.getLast();
            }
        } catch (NoSuchElementException  e) {
            Print.i(TAG, "GET LAST ENTRY: ERROR list empty");
            lastElement = "";
        }
        return lastElement;
    }
    
    /**
     * Remove all old entries
     * @param tag The fragment tag
     */
    public void removeAllEntriesWithTag(String tag) {
        Print.i(TAG, "REMOVE OLD ENTRIES: " + tag);
        Iterator<String> iterator = backStack.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(tag))
                iterator.remove();
        }
    }

    /**
     * Remove all old entries.
     * Warning: Async operation.
     */
    public void removeAllEntriesWithTag(final String... tags) {
        WorkerThread.executeRunnable(getSingletonThread(), new Runnable() {
            @Override
            public void run() {
                for (String tag : tags){
                    removeAllEntriesWithTag(tag);
                }
            }
        });
    }

    /**
     * Print all entries
     */
    @SuppressWarnings("unused")
    public void printAllEntries(){
        String entries ="";
        for (String aBackStack : backStack) {
            entries += " " + aBackStack;
        }
        Print.d(TAG, "ENTRY: " + entries);
    }

    /**
     * Print all entries
     * @return The back stack
     */
    public ConcurrentLinkedDeque<String> returnAllEntries(){
       return backStack;
    }
    
    /**
     * Search for a tag
     * @param tag The fragment tag
     * @return true or false
     */
    public Boolean hasEntry(String tag){
        return backStack != null && backStack.contains(tag);
    }
    
    /**
     * Method used to remove entries until a respective tag of fragment
     * Warning: Async operation.
     * @param tag The fragment tag
     */
    public void removeEntriesUntilTag(final String tag) {
        Print.i(TAG, "POP ENTRIES UNTIL: " + tag);

        WorkerThread.executeRunnable(getSingletonThread(), new Runnable() {
            @Override
            public String toString() {
                return "removeEntriesUntilTag";
            }

            @Override
            public void run() {
                // Create reverse iterator
//               Log.e(TAG, "Doing work: removeEntriesUntilTag");
                Iterator<String> iterator = backStack.descendingIterator();
                while (iterator.hasNext()) {
                    String currentTag = iterator.next();
                    Print.i(TAG, "POP TAG: " + currentTag + " UNTIL TAG: " + tag + " STACK SIZE: " + backStack.size());
                    // Case HOME
                    if (currentTag.equals(FragmentType.HOME.toString())) break;
                    // Case TAG
                    else if (currentTag.equals(tag)) break;
                    // Case Remove
                    else iterator.remove();
                }
                Print.i(TAG, "AFTER POP UNTIL TAG: " + tag + " STACK SIZE: " + backStack.size());
            }
        });

    }

    /**
     * ##################### PUSH #####################
     */
    /**
     * Add the tag to the back stack removing duplicates.
     * Warning: Async operation.
     */
    public void addEntryToBackStack(final String tag) {
        Print.d(TAG, "ADD ENTRY TO BACK STACK");

        WorkerThread.executeRunnable(getSingletonThread(), new Runnable() {
            @Override
            public String toString() {
                return "addEntryToBackStack";
            }
            @Override
            public void run() {
                removeAllEntriesWithTag(tag);
                addToBackStack(tag);
            }
        });

    }

    /**
     * Start transition to the new fragment with animation
     * @param activity The current activity
     * @param container The frame layout
     * @param fragment The new fragment The new fragment
     * @param fragmentType The fragment type
     * @param addToBackStack The flag to add or not to back stack Flag to add or not to back stack
     */
    public void startTransition(BaseActivity activity, int container, Fragment fragment, FragmentType fragmentType, Boolean addToBackStack){
        startTransition(activity, container, fragment, fragmentType, addToBackStack, ANIMATION_IN);
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
        Print.i(TAG, "BACK STACK SIZE: " + size);
        Print.i(TAG, "THE CURRENT BACK STACK ENTRIES: " + backStack);
        switch (size) {
        case 1:
            /**
             * In this point if fragment type isn't HOME then something wrong is happening, to fix show the HOME
             * @author sergiopereira
             */
            if (getLastEntry().equals(FragmentType.HOME.toString()))
                activity.doubleBackPressToExit();
            else {
                Print.i(TAG, "WARNING: THE FIRST ENTRY IS NOT HOME, GOTO HOME");
                init();
                popAllBackStack(activity, null);
                activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
            
            break;
        case 0:
            Print.i(TAG, "WARNING: NO ENTRIES, GOTO HOME");
            activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            popBackStack(activity);
        }

    }
   
    /**
     * Simulate the back pressed removing the current fragment
     * @param activity The current activity
     */
    private void popBackStack(BaseActivity activity) {
        Print.i(TAG, "POP BACK STACK");
        // Pop the last fragment
        popLastEntry();
        // Get the new last fragment
        String lastTag = getLastEntry();
        // Case invisible fragment
        if (!TextUtils.isEmpty(lastTag) && lastTag.equals(FragmentType.UNKNOWN.toString()) && getBackStackSize() > 0) {
            Print.i(TAG, "ON POP BACK STACK: INVISIBLE TAG " + lastTag);
            popBackStack(activity);
        }
        // Case visible fragment
        else if (!TextUtils.isEmpty(lastTag)) {
            Print.i(TAG, "ON POP BACK STACK: TAG " + lastTag);
            // Pop stack until fragment tag
            try {
                activity.getSupportFragmentManager().popBackStackImmediate(lastTag, POP_BACK_STACK_NO_INCLUSIVE);
            } catch (IllegalStateException | NullPointerException e) {
                Print.w(TAG, "WARNING ON POP BACK STACK", e);
            }
        }
        // Case visible fragment
        else Print.w(TAG, "WARNING ON POP BACK STACK: TAG IS EMPTY " + getBackStackSize());
    }
    
    /**
     * Pop all back stack
     */
    public void popAllBackStack(BaseActivity activity){
        Print.d(TAG, "POP ALL BACK STACK: " + getBackStackSize() + " MANAGER:" + activity.getSupportFragmentManager().getBackStackEntryCount());
        // Pop all our back stack
        cleanBackStack();
        // Pop all back stack
        try {
            activity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException | NullPointerException e) {
            Print.d(TAG, "POP ALL ENTRIES: ERROR IllegalStateException");
            e.printStackTrace();
        }
    }
    
    /**
     * Pop all entries until tag, inclusive or not the tag from back stack
     * @param activity The current activity
     * @param tag The fragment tag
     */
    public void popAllEntriesUntil(BaseActivity activity, String tag) {
        Print.d(TAG, "POP ALL ENTRIES UNTIL: " + tag + " " + getBackStackSize());
        // Get the new last fragment
        removeEntriesUntilTag(tag);
        try {
            // Pop stack until fragment tag
            activity.getSupportFragmentManager().popBackStackImmediate(tag, POP_BACK_STACK_NO_INCLUSIVE);   
        } catch (IllegalStateException | NullPointerException e) {
            Print.d(TAG, "POP ALL ENTRIES UNTIL: ERROR IllegalStateException");
            e.printStackTrace();
        }
    }
    
    
    /**
     * ##################### SWITCH #####################
     */

    /**
     * Method used to switch fragment on UI with back stack support
     * 
     * @param fragment The new fragment
     * @param addToBackStack The flag to add or not to back stack
     * @author sergiopereira
     */
    private void startTransition(BaseActivity activity, int container, Fragment fragment, FragmentType fragmentType, Boolean addToBackStack, final Boolean animationIn) {
        Print.d(TAG, "START TRANSITION: " + fragmentType.toString() + " " + addToBackStack);
        final FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

        /**
         * FIXME: Excluded piece of code due to crash on API = 18.
         * Temporary fix - https://code.google.com/p/android/issues/detail?id=185457
         */
        DeviceInfoHelper.executeCodeExcludingJellyBeanMr2Version(new Runnable() {
            @Override
            public void run() {
                // Case ANIMATION_IN
                if (animationIn) {
                    fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in, R.anim.pop_out);
                }
                // Case ANIMATION_OUT
                else {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });


        /**
         * Case isn't add to back stack
         * Then add with an UNKNOWN tag
         */
        if(!addToBackStack) fragmentType = FragmentType.UNKNOWN;
            
        // Replace
        fragmentTransaction.replace(container, fragment, fragmentType.toString());
        // Add the fragment to back stack
        fragmentTransaction.addToBackStack(fragmentType.toString());
        // Add the fragment to our back stack
        addEntryToBackStack(fragmentType.toString());
        
        // Commit
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
        
        // XXX
        // FragmentManager.enableDebugLogging(true);
        // Log.d(TAG, "START TRANSITION: " + getBackStackSize() + " " + activity.getSupportFragmentManager().getBackStackEntryCount());
        // printAllEntries();
    }
    
    
    /**
     * ##################### OLD METHODS #####################
     */
    
//    /**
//     * Method used to switch fragment on UI with/without back stack support
//     *
//     * @param fragment The new fragment
//     * @param addToBackStack The flag to add or not to back stack
//     * @author sergiopereira
//     */
//    @Deprecated
//    public void fragmentManagerTransition(BaseActivity activity, int container, Fragment fragment, String tag, Boolean addToBackStack, boolean animated) {
//        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
//        // Animations
//        if(animated)
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//        // Replace
//        fragmentTransaction.replace(container, fragment, tag);
//        // BackStack
//        if (addToBackStack)
//            fragmentTransaction.addToBackStack(tag);
//        // Commit
//        fragmentTransaction.commit();
//
//        int backStackSize = activity.getSupportFragmentManager().getBackStackEntryCount();
//        Print.d("BA Fragment", "FRAGMENT BACK STACK SIZE: " + backStackSize);
//    }
    
    
//    /**
//     * Method used to perform a back stack using fragments
//     * @author sergiopereira
//     */
//    @Deprecated
//    public void fragmentManagerBackPressed(BaseActivity activity){
//        int backStackSize = activity.getSupportFragmentManager().getBackStackEntryCount();
//        Print.d("BA Fragment", "FRAGMENT BACK STACK SIZE: " + backStackSize);
//        if (backStackSize == 1)
//            activity.finish();
//        else {
//            activity.getSupportFragmentManager().popBackStackImmediate();
//        }
//    }
    
    /**
     *  @param activity The current activity
     * @param tag The fragment tag
     */
    public void popAllBackStack(BaseActivity activity, String tag){
        activity.getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    
    /**
     *
     * @param activity The current activity
     * @param tag The fragment tag
     */
    @Deprecated
    public void popBackStackUntilTag(BaseActivity activity, String tag) {
        try {
            activity.getSupportFragmentManager().popBackStackImmediate(tag, POP_BACK_STACK_NO_INCLUSIVE);
        } catch (IllegalStateException | NullPointerException e) {
            Print.w(TAG, "WARNING ON POP BACK STACK", e);
        }
    }
    
    /**
     * Restore the backstack using the provided fragment list
     * This is useful for when the application is released form memory by the OS and the user opens it again.
     *   
     * @param fragments The list of current opened fragments
     */
    private void restoreBackstack(BaseActivity activity, List<Fragment> fragments) {
        Print.i(TAG, "ON RESTORE BACKSTACK:");
        for (Fragment fragment : fragments) {
            if (null != fragment && null != fragment.getTag() && !backStack.contains(fragment.getTag())) {
                Print.i(TAG, "RESTORE: " + fragment.getTag());
                // Add to backstack
                addToBackStack(fragment.getTag());
                // Set activity
                if (fragment instanceof BaseFragment) ((BaseFragment) fragment).setActivity(activity);
            }
        }
    }
    
    /**
     * Validate the current state and try restore the saved state if necessary 
     * @param activity The current activity
     * @param backstackTypes
     * @param originalFragments
     * @param currentFragmentType
     * @author paulo
     * @modified sergiopereira
     */
    public void validateCurrentState(BaseActivity activity, ArrayList<String> backstackTypes, List<Fragment> originalFragments, FragmentType currentFragmentType) {
        // Validate the current back stack size
        if(getBackStackSize() > 0) {
            Print.i(TAG, "FRAGMENT CONTROLLER: HAS BACKSTACK!");
            return;
        }
        
        Print.i(TAG, "FRAGMENT CONTROLLER: TRY RECOVER BACKSTACK!");
//        for (String backstackType : backstackTypes) {
//            Print.i(TAG, "FRAGMENT CONTROLLER: TYPE " + backstackType);
//        }
//        for (Fragment originalFragment : originalFragments) {
//            if(originalFragment != null)
//                Print.i(TAG, "FRAGMENT CONTROLLER: FRAGMENT " + originalFragment.getTag());
//        }
        
        List<Fragment> orderedFragments = new ArrayList<>();
        if(originalFragments.size() > 0 && backstackTypes.size() > 0){
            for (int i = 0; i < backstackTypes.size(); i++) {
                for (int j = 0; j < originalFragments.size(); j++) {
                    if(originalFragments.get(j) != null && backstackTypes.get(i).equalsIgnoreCase(originalFragments.get(j).getTag()))
                        //validating that none of the checkout steps are entered in the new backstack because it will have an empty shopping cart 
                        //and will redirected to the shopping cart fragment, making it the top one
                        if(
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.LOGIN.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.CREATE_ADDRESS.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.EDIT_ADDRESS.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.MY_ADDRESSES.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.MY_ORDER.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.CHECKOUT_EXTERNAL_PAYMENT.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.SHIPPING_METHODS.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.PAYMENT_METHODS.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.CHECKOUT_THANKS.toString()) &&
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.CHECKOUT_BASKET.toString()) &&
                            // CASE CHOOSE_COUNTRY FROM SPLASH SCREEN
                            !backstackTypes.get(i).equalsIgnoreCase(FragmentType.CHOOSE_COUNTRY.toString())
                        ) {
                            Log.i(TAG, "Add Fragment: " + originalFragments.get(j).getTag());
                            orderedFragments.add(originalFragments.get(j));
                        }
                }
            }

//            //setting specific cases of behavior when we don't want to recover the back stack and set it starting from home screen
//            if(!currentFragmentType.toString().equalsIgnoreCase(FragmentType.CHOOSE_COUNTRY.toString()))
                restoreBackstack(activity, orderedFragments);

        }
    }

    /**
     * Start singleton thread if not initialized yet.
     *
     * @return The singleton thread.
     */
    protected WorkerThread getSingletonThread(){
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread();
            mWorkerThread.start();
        }
        return mWorkerThread;
    }


    /*
     * ########### FRAGMENTS WITH CHILD FRAGMENTS ###########
     */

    /**
     * Add a child fragment to parent fragment using ChildFragmentManager.
     */
    public static void addChildFragment(Fragment parent, int container, Fragment fragment) {
        // Child Fragment manger
        FragmentTransaction fragmentTransaction = parent.getChildFragmentManager().beginTransaction();
        // Replace
        fragmentTransaction.replace(container, fragment, fragment.getTag());
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void addChildFragment(Fragment parent, int container, Fragment fragment, String tag) {
        // Child Fragment manger
        FragmentTransaction fragmentTransaction = parent.getChildFragmentManager().beginTransaction();
        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Remove a child fragment from parent fragment using ChildFragmentManager.
     */
    public static void removeChildFragmentById(Fragment parent, int id) {
        FragmentManager manager = parent.getChildFragmentManager();
        // Find
        Fragment fragment = manager.findFragmentById(id);
        // Validate and remove
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

}

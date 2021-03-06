package com.bamilo.android.appmodule.bamiloapp.controllers.fragments;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.utils.WorkerThread;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class is responsible to controller the fragment transition, to switch fragments on UI with
 * back stack support. Uses the back stack from support to get the instances of fragments but the
 * behaviour of the back is performed using the our LinkedList. Was added a middle layer
 * (LinkedList) to simulate the flag FLAG_ACTIVITY_REORDER_TO_FRONT.
 *
 * @author sergiopereira
 * @see <a href="http://stackoverflow.com/questions/16963642/how-to-implement-a-intent-flag-activity-reorder-to-front-for-a-fragment-backstac">stackoverflow</a>
 */
public class FragmentController {

    public static final boolean ADD_TO_BACK_STACK = true;
    public static final Bundle NO_BUNDLE = null;
    // Animation type
    public static final int NONE = 0;
    public static final int FADE = 1;
    public static final int SLIDE = 2;
    public static final int SLIDE_IN = 3;
    public static final int SLIDE_OUT = 4;
    public static final int FADE_IN_SLIDE_TO_LEFT = 5;
    public static final int FADE_IN_SLIDE_TO_RIGHT = 6;

    @IntDef({NONE, FADE, SLIDE, SLIDE_IN, SLIDE_OUT, FADE_IN_SLIDE_TO_LEFT, FADE_IN_SLIDE_TO_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {

    }

    private final int POP_BACK_STACK_NO_INCLUSIVE = 0;
    private static FragmentController sFragmentController;
    @SuppressLint("NewApi")
    private ConcurrentLinkedDeque<String> mBackStack = new ConcurrentLinkedDeque<>();
    protected WorkerThread mWorkerThread;

    /**
     * Method used to get the instance of FragmentController
     *
     * @return {@link FragmentController}
     * @author sergiopereira
     */
    public static FragmentController getInstance() {
        return sFragmentController == null ? sFragmentController = new FragmentController()
                : sFragmentController;
    }

    public FragmentController() {
    }


    public void init() {
        this.mBackStack.clear();
    }

    /**
     * Get the back stack size
     *
     * @return {@link Integer}
     * @author sergiopereira
     */
    private int getBackStackSize() {
        return mBackStack.size();
    }

    /**
     * Remove all entries
     *
     * @author sergiopereira
     */
    private void cleanBackStack() {
        mBackStack.clear();
    }

    /**
     * Add the tag of fragment to back stack
     *
     * @param tag The fragment tag
     */
    public void addToBackStack(String tag) {
        this.mBackStack.addLast(tag);
    }

    public void popPdvFromBackStack() {
        if (!mBackStack.isEmpty() && mBackStack.getLast().equals("pdv")) {
            mBackStack.removeLast();
        }
    }

    /**
     * Remove the last entry
     */
    public void popLastEntry() {
        if (!mBackStack.isEmpty()) {
            mBackStack.removeLast();
        }
    }

    /**
     * Remove the last entry if is the tag
     *
     * @param tag The fragment tag
     */
    public void popLastEntry(String tag) {
        if (!mBackStack.isEmpty() && mBackStack.getLast().equals(tag)) {
            mBackStack.removeLast();
        }
    }

    /**
     * Get the last entry
     *
     * @return {@link String}
     */
    public String getLastEntry() {
        String lastElement = "";
        try {
            if (!mBackStack.isEmpty()) {
                lastElement = this.mBackStack.getLast();
            }
        } catch (NoSuchElementException e) {
            lastElement = "";
        }
        return lastElement;
    }

    /**
     * Remove all old entries
     *
     * @param tag The fragment tag
     */
    public void removeAllEntriesWithTag(String tag) {
        Iterator<String> iterator = mBackStack.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(tag)) {
                iterator.remove();
            }
        }
    }

    /**
     * Remove all old entries. Warning: Async operation.
     */
    public void removeAllEntriesWithTag(final String... tags) {
        WorkerThread.executeRunnable(getSingletonThread(), () -> {
            for (String tag : tags) {
                removeAllEntriesWithTag(tag);
            }
        });
    }

    /**
     * Print all entries
     */
    @SuppressWarnings("unused")
    public void printAllEntries() {
        StringBuilder entries = new StringBuilder();
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            for (String aBackStack : mBackStack) {
                entries.append(" ").append(aBackStack);
            }
        }
    }

    /**
     * Print all entries
     *
     * @return The back stack
     */
    public ConcurrentLinkedDeque<String> returnAllEntries() {
        return mBackStack;
    }

    /**
     * Search for a tag
     *
     * @param tag The fragment tag
     * @return true or false
     */
    public Boolean hasEntry(String tag) {
        return mBackStack != null && mBackStack.contains(tag);
    }

    /**
     * Method used to remove entries until a respective tag of fragment Warning: Async operation.
     *
     * @param tag The fragment tag
     */
    public void removeEntriesUntilTag(final String tag) {
        WorkerThread.executeRunnable(getSingletonThread(), new Runnable() {
            @Override
            public String toString() {
                return "removeEntriesUntilTag";
            }

            @Override
            public void run() {
                // Create reverse iterator
                Iterator<String> iterator = mBackStack.descendingIterator();
                while (iterator.hasNext()) {
                    String currentTag = iterator.next();
                    // Case HOME
                    if (currentTag.equals(FragmentType.HOME.toString())) {
                        break;
                    }
                    // Case TAG
                    else if (currentTag.equals(tag)) {
                        break;
                    }
                    // Case Remove
                    else {
                        iterator.remove();
                    }
                }
            }
        });
    }

    /**
     * Add the tag to the back stack removing duplicates. Warning: Async operation.
     */
    public void addEntryToBackStack(final String tag) {
        WorkerThread.executeRunnable(getSingletonThread(), new Runnable() {
            @Override
            public String toString() {
                return "addEntryToBackStack";
            }

            @Override
            public void run() {
//                removeAllEntriesWithTag(tag);
                addToBackStack(tag);
            }
        });

    }

    /**
     * Start transition to the new fragment with animation
     *
     * @param activity The current activity
     * @param container The frame layout
     * @param fragment The new fragment The new fragment
     * @param fragmentType The fragment type
     * @param addToBackStack The flag to add or not to back stack Flag to add or not to back stack
     */
    public void startTransition(BaseActivity activity, int container, Fragment fragment,
            FragmentType fragmentType, Boolean addToBackStack) {
        startTransition(activity, container, fragment, fragmentType, addToBackStack, FADE);
    }

    public void gotoPdv(BaseActivity activity) {
        Iterator<String> it = mBackStack.descendingIterator();
        while (it.hasNext()) {
            it.next();
            if (it.hasNext() && it.next().equals("pdv")) {
                popBackStack(activity);
                activity.finish();
            }
            popBackStack(activity);
        }
    }

    /**
     * Method used to perform a back stack using fragments
     *
     * @author sergiopereira
     */
    public void fragmentBackPressed(BaseActivity activity) {
        int size = getBackStackSize();

        Iterator<String> it = mBackStack.descendingIterator();
        if (it.hasNext()) {
            it.next();
            if (it.hasNext() && it.next().equals("pdv")) {
                popBackStack(activity);
                activity.finish();
                return;
            }
        }

        switch (size) {
            case 1:
                /**
                 * In this point if fragment type isn't HOME then something wrong is happening, to fix show the HOME
                 * @author sergiopereira
                 */
                if (getLastEntry().equals(FragmentType.HOME.toString())) {
                    activity.doubleBackPressToExit();
                } else {
                    init();
                    popAllBackStack(activity, null);
                    activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                            FragmentController.ADD_TO_BACK_STACK);
                }
                break;
            case 0:
                activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                        FragmentController.ADD_TO_BACK_STACK);
                break;
            default:
                popBackStack(activity);
        }
    }

    /**
     * Simulate the back pressed removing the current fragment
     *
     * @param activity The current activity
     */
    private void popBackStack(BaseActivity activity) {
        // Pop the last fragment
        popLastEntry();
        // Get the new last fragment
        String lastTag = getLastEntry();
        // Case invisible fragment
        if (!TextUtils.isEmpty(lastTag) && lastTag.equals(FragmentType.UNKNOWN.toString())
                && getBackStackSize() > 0) {
            popBackStack(activity);
        }
        // Case visible fragment
        else if (!TextUtils.isEmpty(lastTag)) {
            // Pop stack until fragment tag
            try {
                activity.getSupportFragmentManager()
                        .popBackStackImmediate(lastTag, POP_BACK_STACK_NO_INCLUSIVE);
            } catch (IllegalStateException | NullPointerException ignored) {
            }
        }
    }

    /**
     * Pop all back stack
     */
    public void popAllBackStack(BaseActivity activity) {
        // Pop all our back stack
        cleanBackStack();
        // Pop all back stack
        try {
            activity.getSupportFragmentManager()
                    .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pop all entries until tag, inclusive or not the tag from back stack
     *
     * @param activity The current activity
     * @param tag The fragment tag
     */
    public void popAllEntriesUntil(BaseActivity activity, String tag) {
        // Get the new last fragment
        removeEntriesUntilTag(tag);
        try {
            // Pop stack until fragment tag
            activity.getSupportFragmentManager()
                    .popBackStackImmediate(tag, POP_BACK_STACK_NO_INCLUSIVE);
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to switch fragment on UI with back stack support
     *
     * @param fragment The new fragment
     * @param stack The flag to add or not to back stack
     * @author sergiopereira
     */
    private void startTransition(BaseActivity activity, int container, Fragment fragment,
            FragmentType fType, Boolean stack, @AnimationType int aType) {
        // Case isn't add to back stack then add with an UNKNOWN tag
        fType = !stack ? FragmentType.UNKNOWN : fType;
        // Get transaction
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                .beginTransaction();
        // Animation
        addAnimation(fragmentTransaction, aType);
        // Replace
        fragmentTransaction.replace(container, fragment, fType.toString());
        // Add the fragment to back stack
        fragmentTransaction.addToBackStack(fType.toString());
        // Add the fragment to our back stack
        addEntryToBackStack(fType.toString());
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * @param activity The current activity
     * @param tag The fragment tag
     */
    public void popAllBackStack(BaseActivity activity, String tag) {
        activity.getSupportFragmentManager()
                .popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Restore the backstack using the provided fragment list This is useful for when the
     * application is released form memory by the OS and the user opens it again.
     *
     * @param fragments The list of current opened fragments
     */
    private void restoreBackstack(BaseActivity activity, List<Fragment> fragments) {
        for (Fragment fragment : fragments) {
            if (null != fragment && null != fragment.getTag() && !mBackStack
                    .contains(fragment.getTag())) {
                // Add to backstack
                addToBackStack(fragment.getTag());
                // Set activity
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).setActivity(activity);
                }
            }
        }
    }

    /**
     * Validate the current state and try restore the saved state if necessary
     *
     * @author paulo
     * @modified sergiopereira
     */
    public void validateCurrentState(BaseActivity activity, ArrayList<String> backstackTypes,
            List<Fragment> originalFragments) {
        // Validate the current back stack size
        if (getBackStackSize() > 0) {
            return;
        }

        List<Fragment> orderedFragments = new ArrayList<>();
        if (originalFragments.size() > 0 && backstackTypes.size() > 0) {
            for (int i = 0; i < backstackTypes.size(); i++) {
                for (int j = 0; j < originalFragments.size(); j++) {
                    if (originalFragments.get(j) != null && backstackTypes.get(i)
                            .equalsIgnoreCase(originalFragments.get(j).getTag())) {
                        if (
                                !backstackTypes.get(i)
                                        .equalsIgnoreCase(FragmentType.LOGIN.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_CREATE_ADDRESS.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_EDIT_ADDRESS.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_MY_ADDRESSES.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_FINISH.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_EXTERNAL_PAYMENT.toString())
                                        &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_SHIPPING.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_PAYMENT.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_THANKS.toString()) &&
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHECKOUT_BASKET.toString()) &&
                                        // CASE CHOOSE_COUNTRY FROM SPLASH SCREEN
                                        !backstackTypes.get(i).equalsIgnoreCase(
                                                FragmentType.CHOOSE_COUNTRY.toString())
                                ) {
                            Log.i("Add Fragment: ", originalFragments.get(j).getTag());
                            orderedFragments.add(originalFragments.get(j));
                        }
                    }
                }
            }
            restoreBackstack(activity, orderedFragments);
        }
    }

    /**
     * Start singleton thread if not initialized yet.
     *
     * @return The singleton thread.
     */
    protected WorkerThread getSingletonThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread();
            mWorkerThread.start();
        }
        return mWorkerThread;
    }

    /**
     * Add a child fragment to parent fragment using ChildFragmentManager.
     */
    @SuppressWarnings("unused")
    public void addChildFragment(@NonNull Fragment parent, @IdRes int container,
            @NonNull Fragment fragment, @NonNull String tag) {
        // Child Fragment manger
        FragmentTransaction fragmentTransaction = parent.getChildFragmentManager()
                .beginTransaction();
        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Remove a child fragment from parent fragment using ChildFragmentManager.
     */
    @SuppressWarnings("unused")
    public static void removeChildFragmentById(Fragment parent, int id) {
        FragmentManager manager = parent.getChildFragmentManager();
        // Find
        Fragment fragment = manager.findFragmentById(id);
        // Validate and remove
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    /**
     * Get parent from back stack.
     */
    @Nullable
    @SuppressWarnings("unused")
    public static String getParentBackStackTag(Fragment fragment) {
        try {
            FragmentManager manager = fragment.getActivity().getSupportFragmentManager();
            int size = manager.getBackStackEntryCount();
            return manager.getBackStackEntryAt(size - IntConstants.STACK_PARENT_LEVEL).getName();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Add animation
     */
    private static void addAnimation(@NonNull FragmentTransaction transaction,
            @AnimationType int type) {
        switch (type) {
            case FADE_IN_SLIDE_TO_LEFT:
                transaction.setCustomAnimations(R.anim.pop_in, R.anim.slide_to_left);
                break;
            case FADE_IN_SLIDE_TO_RIGHT:
                transaction.setCustomAnimations(R.anim.pop_in, R.anim.slide_to_right);
                break;
            case SLIDE_IN:
                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case SLIDE_OUT:
                transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case SLIDE:
                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                        R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case FADE:
                transaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in,
                        R.anim.pop_out);
                break;
            case NONE:
            default:
                break;
        }
    }

    /**
     * Class used to replace fragments.
     */
    @SuppressWarnings("unused")
    public static final class Transaction {

        private final Fragment parent;
        private final int container;
        private final Fragment fragment;
        private boolean child;
        private boolean stack;
        private boolean noState;
        private int animation = NONE;
        private String tag;

        /**
         * Constructor
         */
        public Transaction(Fragment parent, int container, Fragment fragment) {
            this.parent = parent;
            this.container = container;
            this.fragment = fragment;
        }

        /**
         * Indicates to use the child fragment manager.
         */
        public Transaction useChildFragmentManager() {
            this.child = true;
            return this;
        }

        /**
         * Add the new fragment to back stack.
         */
        public Transaction addBackStack(boolean b) {
            this.stack = b;
            return this;
        }

        /**
         * Add a tag for new fragment to be identified in fragment manager.
         */
        public Transaction addTag(@NonNull String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * Add an animation when the replacement is performed.
         */
        public Transaction addAnimation(@AnimationType int type, boolean rtl) {
            // Reverse slide animation
            if (rtl && DeviceInfoHelper.isPosJellyBean()) {
                if (type == SLIDE_IN) {
                    type = SLIDE_OUT;
                } else if (type == SLIDE_OUT) {
                    type = SLIDE_IN;
                } else if (type == FADE_IN_SLIDE_TO_LEFT) {
                    type = FADE_IN_SLIDE_TO_RIGHT;
                }
            }
            this.animation = type;
            return this;
        }

        /**
         * Will be used the method to commitAllowingStateLoss() from manager.
         */
        public Transaction allowStateLoss() {
            this.noState = true;
            return this;
        }

        /**
         * Process the transaction
         */
        public void commit() {
            // Manager
            FragmentManager manager =
                    child ? parent.getChildFragmentManager() : parent.getFragmentManager();
            // Transaction
            FragmentTransaction transaction = manager.beginTransaction();
            // Animation
            FragmentController.addAnimation(transaction, animation);
            // Replace
            transaction.replace(container, fragment, tag);
            // Back stack
            if (stack) {
                transaction.addToBackStack(tag);
            }
            // Commit
            if (noState) {
                transaction.commitAllowingStateLoss();
            } else {
                transaction.commit();
            }
        }
    }

}

package pt.rocket.utils.dialogfragments;

import java.util.ArrayList;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import org.holoeverywhere.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class DialogFilterFragment extends DialogFragment implements OnClickListener {

    private final static String TAG = LogTagHelper.create(DialogFilterFragment.class);

    private static final long DELAY_DISMISS = 250;

    public static final int NO_INITIAL_POSITION = -1;

    private String mTitle;

    private ArrayList<String> mItems;
    private ArrayList<String> mItemsAvailable;

    private String mId;

    private long mInitialPosition;

    private Activity mActivity;

    // private Dialog mDialog;
    private ListView list;

    // private DialogListAdapter mAdapter;

    public DialogFilterFragment() { }

    /**
     * 
     * @param activity
     * @param listener
     * @param id
     * @param title
     * @param items
     * @param initialPosition
     * @return
     */
    public static DialogFilterFragment newInstance(Activity activity, String id, String title, ArrayList<String> items, long initialPosition) {
        Log.d(TAG, "NEW INSTANCE");
        DialogFilterFragment dialogListFragment = new DialogFilterFragment();
        dialogListFragment.mActivity = activity;
        dialogListFragment.mId = id;
        dialogListFragment.mTitle = title;
        dialogListFragment.mItems = items;
        dialogListFragment.mInitialPosition = initialPosition;
        return dialogListFragment;
    }
    
    public static DialogFilterFragment newInstance() {
        Log.d(TAG, "NEW INSTANCE");
        DialogFilterFragment dialogListFragment = new DialogFilterFragment();
        return dialogListFragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Jumia_Dialog_Blue_NoTitle, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_main, container, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get header
        view.findViewById(R.id.dialog_filter_header_title).setOnClickListener(this);
        view.findViewById(R.id.dialog_filter_header_clear).setOnClickListener(this);
        // Get container
        onSwitchChildFragment(1);
        // Get buttons
    }

    private void onSwitchChildFragment(int type) {
        switch (type) {
        case 1:
            FragmentOne fragmentOne = FragmentOne.newInstance(this);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentOne, false, true);
            break;
        case 2:
            FragmentTwo fragmentTwo = FragmentTwo.newInstance(this);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentTwo, true, true);
            break;

        default:
            break;
        }
    }
    
    /**
     * Method used to associate the container and fragment
     * @param container
     * @param fragment
     * @param animated
     */
    public void fragmentChildManagerTransition(int container, Fragment fragment, boolean animated, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        if(animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // Back stack
        if(addToBackStack)
            fragmentTransaction.addToBackStack(null);
        // Commit
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void allowBackPressed() {
        Log.d(TAG, "ALLOW BACK PRESSED");
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {
         if(v.getId() == R.id.dialog_filter_header_title)
             dismiss();
        // else if (v.getId() == R.id.dialog_filter_header_clear) {
        // // Create the fragment and show it as a dialog.
        // DialogFilterFragment newFragment = new DialogFilterFragment();
        // newFragment.show(getFragmentManager(), "dialog");
        // }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    /**
     * FRAGMENT 1
     */

    private static class FragmentOne extends Fragment {

        private DialogFilterFragment mParent;

        public static FragmentOne newInstance(DialogFilterFragment parent) {
            Log.d(TAG, "NEW INSTANCE ONE");
            FragmentOne frag = new FragmentOne();
            frag.mParent = parent;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_filter_fragment_one, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.findViewById(R.id.dialog_filter_one).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParent.onSwitchChildFragment(2);
                }
            });
        }
    }

    /**
     * FRAGMENT 2
     */

    private static class FragmentTwo extends Fragment {

        private DialogFilterFragment mParent;

        public static FragmentTwo newInstance(DialogFilterFragment parent) {
            Log.d(TAG, "NEW INSTANCE TWO");
            FragmentTwo frag = new FragmentTwo();
            frag.mParent = parent;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_filter_fragment_two, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.findViewById(R.id.dialog_filter_two).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "NEW INSTANCE TWO");
                    mParent.allowBackPressed();
                }
            });
        }
    }
    
    


}

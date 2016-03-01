package com.mobile.utils.dialogfragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogListFragment extends BottomSheet implements OnItemClickListener, OnClickListener {
	
    private final static String TAG = DialogListFragment.class.getSimpleName();

	private String mTitle;
	
	private ArrayList<String> mItems;
	
	private ArrayList<String> mItemsAvailable;
	
	private int mInitialPosition;
	
	private Activity mActivity;
	
	private OnDialogListListener mSelectListener;
	
	private OnClickListener mClickListener;
	
	private DialogListAdapter mAdapter;

	/**
	 * 
	 * @author sergiopereira
	 *
	 */
    public interface OnDialogListListener {
        void onDialogListItemSelect(int position, String value);
        void onDismiss();
    }

    /**
	 * Empty constructor
	 */
	public DialogListFragment(){}

	/**
	 * Called from Shopping cart.
	 */
	public static DialogListFragment newInstance(Fragment fragment, OnDialogListListener listener, String title, ArrayList<String> items, int initialPosition) {
	    Print.d(TAG, "NEW INSTANCE");
	    DialogListFragment dialogListFragment = new DialogListFragment();  
	    dialogListFragment.mActivity = fragment.getActivity();
	    dialogListFragment.mSelectListener = listener;
	    dialogListFragment.mTitle = title;
	    dialogListFragment.mItems = items;
	    dialogListFragment.mInitialPosition = initialPosition;
	    return dialogListFragment;
	}

    /**
     * Called from Choose language.
     */
    public static DialogListFragment newInstance(Fragment fragment, OnDialogListListener listener, String title, DialogListAdapter dialogListAdapter, int initialPosition) {
        Print.d(TAG, "NEW INSTANCE");
        DialogListFragment dialogListFragment = new DialogListFragment();
        dialogListFragment.mActivity = fragment.getActivity();
        dialogListFragment.mSelectListener = listener;
        if (fragment instanceof OnClickListener) dialogListFragment.mClickListener = (OnClickListener) fragment;
        dialogListFragment.mTitle = title;
        dialogListFragment.mAdapter = dialogListAdapter;
        dialogListFragment.mItems = dialogListAdapter.getItems();
        dialogListFragment.mInitialPosition = initialPosition;
        return dialogListFragment;
    }

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.dialog_list_content, container);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Validate current activity
        if (this.mActivity == null) {
            dismiss();
            return;
        }
        // Set title
        TextView titleView = (TextView) view.findViewById(R.id.dialog_list_title);
        titleView.setText(mTitle);
        // Set size guide
        setSizeGuide(view);
        // Get list
        ListView list = (ListView) view.findViewById(R.id.dialog_list_view);
        // Set Max list size with size guide
        setListSize(list, mItems.size());
        // Validate adapter
        if(mAdapter == null) {
            mAdapter = new DialogListAdapter(mActivity, mItems, mItemsAvailable);
        }
        // Add adapter
        mAdapter.setCheckedPosition(mInitialPosition);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        // Show pre-selection
        if (mInitialPosition > IntConstants.DEFAULT_POSITION && mInitialPosition < mAdapter.getCount())
            list.setSelection(mInitialPosition);

        this.mActivity.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

    }

	/**
	 * Set the size guide button
	 * @author sergiopereira
	 */
    private void setSizeGuide(View view) {
        // Get views
        View divider = view.findViewById(R.id.dialog_list_size_guide_divider);
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
        divider.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        button.setOnClickListener(null);

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mItems = null;
        mItemsAvailable = null;
        mActivity = null;
        mSelectListener = null;
        mClickListener = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mSelectListener != null){
            mSelectListener.onDismiss();
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager,tag);
            // Trying fix https://rink.hockeyapp.net/manage/apps/33641/app_versions/143/crash_reasons/38911893?type=crashes
            // Or try this solution http://dimitar.me/android-displaying-dialogs-from-background-threads/
        } catch (IllegalStateException | WindowManager.BadTokenException ex){
            Print.e(TAG, "Error showing Dialog", ex);
        }
    }

    /*
     * ########### LISTENERS ###########
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Validate listener
                if(mClickListener != null) {
                    dismiss();
                    mClickListener.onClick(view);
                }
            }
        }, IntConstants.DIALOG_DELAY_DISMISS);

    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        DialogListAdapter adapter = (DialogListAdapter) adapterView.getAdapter();
        if(mItemsAvailable == null || mItemsAvailable.contains(mItems.get(position))){
            adapter.setCheckedPosition(position);
            adapter.notifyDataSetChanged();
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    if (mSelectListener != null) {
                        mSelectListener.onDialogListItemSelect(position, mItems.get(position));
                    }

                }
            }, IntConstants.DIALOG_DELAY_DISMISS);
        }
    }

}

package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import android.widget.TextView;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.R;

import java.lang.ref.WeakReference;
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

	private WeakReference<Activity> mActivity;
	
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
	    DialogListFragment dialogListFragment = new DialogListFragment();
        dialogListFragment.mActivity = new WeakReference(fragment.getActivity());
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
        DialogListFragment dialogListFragment = new DialogListFragment();
        dialogListFragment.mActivity = new WeakReference(fragment.getActivity());
        dialogListFragment.mSelectListener = listener;
        if (fragment instanceof OnClickListener) dialogListFragment.mClickListener = (OnClickListener) fragment;
        dialogListFragment.mTitle = title;
        dialogListFragment.mAdapter = dialogListAdapter;
        dialogListFragment.mItems = dialogListAdapter.getItems();
        dialogListFragment.mInitialPosition = initialPosition;
        return dialogListFragment;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.dialog_list_content, container);
	}

	@Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Validate current activity
        if (this.mActivity == null || this.mActivity.get() == null) {
            dismissAllowingStateLoss();
            return;
        }
        // Set title
        TextView titleView = view.findViewById(R.id.dialog_list_title);
        titleView.setText(mTitle);
        // Set size guide
        setSizeGuide(view);
        // Get list
        ListView list = view.findViewById(R.id.dialog_list_view);
        // Set Max list size with size guide
        setListSize(list, mItems.size());
        // Validate adapter
        if(mAdapter == null) {
            mAdapter = new DialogListAdapter(mActivity.get(), mItems, mItemsAvailable);
        }
        // Add adapter
        mAdapter.setCheckedPosition(mInitialPosition);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        // Show pre-selection
        if (mInitialPosition > IntConstants.DEFAULT_POSITION && mInitialPosition < mAdapter.getCount())
            list.setSelection(mInitialPosition);

        this.mActivity.get().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

    }

	/**
	 * Set the size guide button
	 * @author sergiopereira
	 */
    private void setSizeGuide(View view) {
        // Get views
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
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
        } catch (IllegalStateException | WindowManager.BadTokenException ignored){
        }
    }

    @Override
    public void onClick(final View view) {
        view.postDelayed(() -> {
            // Validate listener
            if(mClickListener != null) {
                dismissAllowingStateLoss();
                mClickListener.onClick(view);
            }
        }, IntConstants.DIALOG_DELAY_DISMISS);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        DialogListAdapter adapter = (DialogListAdapter) adapterView.getAdapter();
        if(mItemsAvailable == null || mItemsAvailable.contains(mItems.get(position))){
            adapter.setCheckedPosition(position);
            adapter.notifyDataSetChanged();
            view.postDelayed(() -> {
                dismissAllowingStateLoss();
                if (mSelectListener != null) {
                    mSelectListener.onDialogListItemSelect(position, mItems.get(position));
                }

            }, IntConstants.DIALOG_DELAY_DISMISS);
        }
    }
}

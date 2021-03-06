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
import com.bamilo.android.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *
 * Dialog to use on bottom sheet to view sorting options
 *
 * @author Paulo Carvalho
 *
 */
public class DialogSortListFragment extends BottomSheet implements OnItemClickListener, OnClickListener {

    private final static String TAG = DialogSortListFragment.class.getSimpleName();

	private static final long DELAY_DISMISS = 250;

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
	public DialogSortListFragment(){}


	/**
	 * Called from Shopping cart.
	 */
	public static DialogSortListFragment newInstance(Fragment fragment, OnDialogListListener listener, String id, String title, ArrayList<String> items, int initialPosition) {
	    DialogSortListFragment dialogListFragment = new DialogSortListFragment();
	    dialogListFragment.mActivity = new WeakReference(fragment.getActivity());
	    dialogListFragment.mSelectListener = listener;
	    dialogListFragment.mTitle = title;
	    dialogListFragment.mItems = items;
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
        // Hide Size guide on sort list
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
        button.setVisibility(View.GONE);
        // Set title
        TextView titleView = view.findViewById(R.id.dialog_list_title);
        titleView.setText(mTitle);

        // Get list
        ListView list = view.findViewById(R.id.dialog_list_view);
        // Set Max list size
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
        if (mInitialPosition > 0 && mInitialPosition < mAdapter.getCount())
            list.setSelection(mInitialPosition);

        this.mActivity.get().getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

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
        }, DELAY_DISMISS);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        if(mItemsAvailable == null || mItemsAvailable.contains(mItems.get(position))){
            mAdapter.setCheckedPosition(position);
            mAdapter.notifyDataSetChanged();

            view.postDelayed(() -> {
                dismissAllowingStateLoss();
                if (mSelectListener != null) {
                    mSelectListener.onDialogListItemSelect(position, mItems.get(position));
                }

            }, DELAY_DISMISS);
        }
    }
}

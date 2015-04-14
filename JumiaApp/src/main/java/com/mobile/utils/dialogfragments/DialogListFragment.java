package com.mobile.utils.dialogfragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogListFragment extends DialogFragment implements OnItemClickListener, OnClickListener {
	
    private final static String TAG = LogTagHelper.create( DialogListFragment.class );
	
	private static final long DELAY_DISMISS = 250;
	
	public static final int NO_INITIAL_POSITION = -1;
	
	private String mTitle;
	
	private ArrayList<String> mItems;
	
	private ArrayList<String> mItemsAvailable;
	
	private String mId;
	
	private int mInitialPosition;
	
	private Activity mActivity;
	
	private OnDialogListListener mSelectListener;
	
	private OnClickListener mClickListener;
	
	//private Dialog mDialog;
	private ListView list;
	
	private DialogListAdapter mAdapter;

    private String mSizeGuideUrl;

	
	/**
	 * 
	 * @author sergiopereira
	 *
	 */
	public interface OnDialogListListener {
		public void onDialogListItemSelect(int position, String value);
        	public void onDismiss();
	}
	
	/**
	 * Empty constructor
	 */
	public DialogListFragment(){}

	
	/**
	 * Called from PDV.
	 * @param fragment
	 * @param id
	 * @param title
	 * @param items
	 * @param initialPosition
	 * @return
	 */
	public static DialogListFragment newInstance(Fragment fragment, String id, String title, ArrayList<String> items, ArrayList<String> itemsAvailable, int initialPosition, String sizeGuideUrl) {
	    Log.d(TAG, "NEW INSTANCE");
	    DialogListFragment dialogListFragment = new DialogListFragment();
	    dialogListFragment.mActivity = fragment.getActivity();
        if (fragment instanceof OnDialogListListener) dialogListFragment.mSelectListener = (OnDialogListListener) fragment;
        if (fragment instanceof OnClickListener) dialogListFragment.mClickListener = (OnClickListener) fragment;
        dialogListFragment.mId = id;
        dialogListFragment.mTitle = title;
        dialogListFragment.mItems = items;
        dialogListFragment.mItemsAvailable = itemsAvailable;
        dialogListFragment.mInitialPosition = initialPosition;
        dialogListFragment.mSizeGuideUrl = sizeGuideUrl;
	    return dialogListFragment;
	}
	
	/**
	 * Called from Shopping cart.
	 * @param fragment
	 * @param listener
	 * @param id
	 * @param title
	 * @param items
	 * @param initialPosition
	 * @return
	 */
	public static DialogListFragment newInstance(Fragment fragment, OnDialogListListener listener, String id, String title, ArrayList<String> items, int initialPosition) {
	    Log.d(TAG, "NEW INSTANCE");
	    DialogListFragment dialogListFragment = new DialogListFragment();  
	    dialogListFragment.mActivity = fragment.getActivity();
	    dialogListFragment.mSelectListener = listener;
	    dialogListFragment.mId = id;
	    dialogListFragment.mTitle = title;
	    dialogListFragment.mItems = items;
	    dialogListFragment.mInitialPosition = initialPosition;
	    return dialogListFragment;   
	}
    
    /**
     * Called from Favorites.
     * @param fragment
     * @param listener
     * @param id
     * @param title
     * @param items
     * @param initialPosition
     * @param sizeGuideUrl 
     * @return
     */
    public static DialogListFragment newInstance(Fragment fragment, OnDialogListListener listener, String id, String title, ArrayList<String> items, ArrayList<String> itemsAvailable, int initialPosition, String sizeGuideUrl) {
        Log.d(TAG, "NEW INSTANCE");
        DialogListFragment dialogListFragment = new DialogListFragment();  
        dialogListFragment.mActivity = fragment.getActivity();
        dialogListFragment.mSelectListener = listener; 
        if (fragment instanceof OnClickListener) dialogListFragment.mClickListener = (OnClickListener) fragment;
        dialogListFragment.mId = id;
        dialogListFragment.mTitle = title;
        dialogListFragment.mItems = items;
        dialogListFragment.mItemsAvailable = itemsAvailable;
        dialogListFragment.mInitialPosition = initialPosition;
        dialogListFragment.mSizeGuideUrl = sizeGuideUrl; 
        return dialogListFragment;   
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setStyle(R.style.Theme_Jumia_Dialog_NoTitle, R.style.Theme_Jumia_Dialog_NoTitle);
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
        list = (ListView) view.findViewById(R.id.dialog_list_view);
        // Validate adapter
        mAdapter = new DialogListAdapter();
        // Add adapter
        mAdapter.setCheckedPosition(mInitialPosition);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        // Show pre-selection
        if (mInitialPosition > 0 && mInitialPosition < mAdapter.getCount())
            list.setSelection(mInitialPosition);

        this.mActivity.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

    }
	
	/**
	 * Set the size guide button
	 * @param view
	 * @author sergiopereira
	 */
    private void setSizeGuide(View view) {
        Log.i(TAG, "SIZE GUIDE: " + mSizeGuideUrl);
        // Get views 
        View divider = view.findViewById(R.id.dialog_list_size_guide_divider);
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
        // Set size guide button
        if (TextUtils.isEmpty(mSizeGuideUrl)) {
            divider.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
        } else {
            button.setTag(mSizeGuideUrl);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
            divider.setVisibility(View.VISIBLE);
        }
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
        }, DELAY_DISMISS);

    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        if(mItemsAvailable == null || mItemsAvailable.contains(mItems.get(position))){
            mAdapter.setCheckedPosition(position);
            mAdapter.notifyDataSetChanged();

            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                    if (mSelectListener != null) {
                        mSelectListener.onDialogListItemSelect(position, mItems.get(position));
                    }

                }
            }, DELAY_DISMISS);
        }
    }

    /*
     * ########### ADAPTER ###########
     */

	/**
	 * 
	 */
	private class DialogListAdapter extends BaseAdapter {

		private int mCheckedPosition = NO_INITIAL_POSITION;
		
		private LayoutInflater mInflater;
		
		/**
		 * Constructor
		 */
		public DialogListAdapter() {
		    mInflater = LayoutInflater.from(mActivity);
        }
		
		/*
		 * (non-Javadoc)
		 * @see android.widget.BaseAdapter#hasStableIds()
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
		    return mItems == null ? 0 : mItems.size();
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.dialog_list_item, parent, false);
			} else {
				view = convertView;
			}
			TextView textView = (TextView) view.findViewById(R.id.item_text);
			TextView textViewUnAvailable = (TextView) view.findViewById(R.id.item_text_unavailable);
			if(mItemsAvailable != null && !mItemsAvailable.contains(mItems.get(position))){
                view.setEnabled(false);
			    textView.setVisibility(View.GONE);
			    textViewUnAvailable.setVisibility(View.VISIBLE);
			    textViewUnAvailable.setText(mItems.get(position));
			} else {
                view.setEnabled(true);
			    textViewUnAvailable.setVisibility(View.GONE);
			    textView.setVisibility(View.VISIBLE);
	            textView.setText(mItems.get(position));
			}
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
			checkBox.setChecked(position == mCheckedPosition);

			return view;
		}

		public void setCheckedPosition(int position) {
			mCheckedPosition = position;
		}
		
		  /**
	     * #FIX: java.lang.IllegalArgumentException: The observer is null.
	     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
	     */
	    @Override
	    public void unregisterDataSetObserver(DataSetObserver observer) {
	        if(observer !=null){
	            super.unregisterDataSetObserver(observer);    
	        }
	    }

	}

}

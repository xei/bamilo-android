package com.mobile.utils.dialogfragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.campaign.CampaignItem;
import com.mobile.service.objects.product.pojo.ProductMultiple;
import com.mobile.service.objects.product.pojo.ProductSimple;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogSimpleListFragment extends BottomSheet implements OnItemClickListener, OnClickListener {

    private final static String TAG = DialogSimpleListFragment.class.getSimpleName();

	private String mTitle;
	private ProductMultiple mProduct;
	private CampaignItem mCampaignItem;
	private Context mContext;
	private OnDialogListListener mListener;

	/**
	 *
	 * @author sergiopereira
	 *
	 */
    public interface OnDialogListListener {

        void onDialogListItemSelect(int position);

        void onDialogListClickView(View view);

        void onDialogSizeListClickView(int position, CampaignItem item);

        void onDialogListDismiss();
    }

    /**
	 * Empty constructor
	 */
	public DialogSimpleListFragment(){
        super();
    }


    /**
     * Create new instance
     */
    public static DialogSimpleListFragment newInstance(Context context, String title, ProductMultiple product, OnDialogListListener listListener) {
        Print.d(TAG, "NEW INSTANCE");
        DialogSimpleListFragment dialogListFragment = new DialogSimpleListFragment();
        dialogListFragment.mContext = context;
        dialogListFragment.mListener = listListener;
        dialogListFragment.mTitle = title;
        dialogListFragment.mProduct = product;
        return dialogListFragment;
    }

    /**
     * Create new instance for CampaignItem
     */
    public static DialogSimpleListFragment newInstance(Context context, String title, CampaignItem product, OnDialogListListener listListener) {
        Print.d(TAG, "NEW INSTANCE");
        DialogSimpleListFragment dialogListFragment = new DialogSimpleListFragment();
        dialogListFragment.mContext = context;
        dialogListFragment.mListener = listListener;
        dialogListFragment.mTitle = title;
        dialogListFragment.mCampaignItem = product;
        dialogListFragment.mProduct = product;
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
        if (this.mContext == null) {
            dismissAllowingStateLoss();
            return;
        }
        // Set title
        TextView titleView = (TextView) view.findViewById(R.id.dialog_list_title);
        titleView.setText(mTitle);
        // Set size guide
        setSizeGuide(view, mProduct.getSizeGuideUrl());
        // Get list
        ListView list = (ListView) view.findViewById(R.id.dialog_list_view);
        // Set Max list size
        setListSize(list, mProduct.getSimples().size() + (TextUtils.isEmpty(mProduct.getSizeGuideUrl()) ? 0 : 1));
        // Validate adapter
        DialogListAdapter mAdapter = new DialogListAdapter(mProduct.getSimples());
        // Add adapter
        mAdapter.setCheckedPosition(mProduct.getSelectedSimplePosition());
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
    }
	
	/**
	 * Set the size guide button
	 * @author sergiopereira
	 */
    private void setSizeGuide(View view, String url) {
        Print.i(TAG, "SIZE GUIDE: " + url);
        // Get views 
        View button = view.findViewById(R.id.dialog_list_size_guide_button);
        // Set size guide button
        if (TextUtils.isEmpty(url)) {
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
        } else {
            button.setTag(url);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
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
        mContext = null;
        mListener = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDialogListDismiss();
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
                if(mListener != null) {
                    dismissAllowingStateLoss();
                    mListener.onDialogListClickView(view);
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
        // Case not enabled is an OOS
        if(!view.isEnabled()) {
            return;
        }
        // Update selected position
        mProduct.setSelectedSimplePosition(position);
        // Get adapter
        DialogListAdapter adapter = (DialogListAdapter) adapterView.getAdapter();
        // Set checked position
        adapter.setCheckedPosition(position);
        // Update content
        adapter.notifyDataSetChanged();
        // Dismiss and notify listener
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
                if (mListener != null && mCampaignItem != null) {
                    mListener.onDialogSizeListClickView(position, mCampaignItem);
                } else if(mListener != null){
                    mListener.onDialogListItemSelect(position);
                }
            }
        }, IntConstants.DIALOG_DELAY_DISMISS);
    }

    /*
     * ########### ADAPTER ###########
     */

	/**
	 * 
	 */
	private class DialogListAdapter extends BaseAdapter {

        private final ArrayList<ProductSimple> mItems;

        private int mCheckedPosition = ProductMultiple.NO_DEFAULT_SIMPLE_POS;

        private final LayoutInflater mInflater;

        /**
		 * Constructor
		 */
		public DialogListAdapter(ArrayList<ProductSimple> simples) {
		    mInflater = LayoutInflater.from(mContext);
            mItems = simples;
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
            // Get views
            TextView textView = (TextView) view.findViewById(R.id.item_text);
            TextView textViewUnAvailable = (TextView) view.findViewById(R.id.item_text_unavailable);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
            // Get simple
            ProductSimple simple = (ProductSimple) getItem(position);
            // Set text
            if(simple.isOutOfStock()) {
                view.setEnabled(false);
                textView.setVisibility(View.GONE);
                textViewUnAvailable.setVisibility(View.VISIBLE);
                textViewUnAvailable.setText(simple.getVariationValue());
            } else {
                view.setEnabled(true);
                textViewUnAvailable.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(simple.getVariationValue());
            }
            // Set check box
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
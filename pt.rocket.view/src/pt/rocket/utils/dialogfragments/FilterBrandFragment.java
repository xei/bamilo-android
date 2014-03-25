package pt.rocket.utils.dialogfragments;

import java.util.List;

import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.components.PinnedSectionListView;
import pt.rocket.framework.components.PinnedSectionListView.PinnedSectionListAdapter;
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.CatalogFilter.FilterOption;
import pt.rocket.view.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterBrandFragment extends Fragment implements OnClickListener, OnItemClickListener{
    
    private static final String TAG = FilterBrandFragment.class.getSimpleName();

    private static int mBackButtonId = R.id.dialog_filter_header_title;
    
    private static int mClearButtonId = R.id.dialog_filter_header_clear;
    
    private static int mCancelButtonId = R.id.dialog_filter_button_cancel;
    
    private static int mDoneButtonId = R.id.dialog_filter_button_done;
    
    private static int mPinnedListId = R.id.dialog_filter_pinnedlist;

    private DialogFilterFragment mParent;
    
    private CatalogFilter mBrandFilter;

    private FilterOptionArrayAdapter mOptionArray;
       
    private int mSelectedOptionPos = -1;


    /**
     * 
     * @param parent
     * @param bundle
     * @return
     */
    public static FilterBrandFragment newInstance(DialogFilterFragment parent, Bundle bundle) {
        Log.d(TAG, "NEW INSTANCE: BRAND");
        FilterBrandFragment frag = new FilterBrandFragment();
        frag.mParent = parent;
        frag.setArguments(bundle);
        return frag;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        // Get selected filter
        mBrandFilter = bundle.getParcelable(DialogFilterFragment.FILTER_TAG);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_fragment_brand, container, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get and set pre-selected option
        if(mBrandFilter.hasOptionSelected()){
            mSelectedOptionPos = mBrandFilter.getSelectedOption();
            mBrandFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(true);
        }
        // Title
        ((TextView) view.findViewById(R.id.dialog_filter_header_title)).setText(mBrandFilter.getName());
        // Back button
        view.findViewById(mBackButtonId).setOnClickListener(this);
        // Clear button
        view.findViewById(mClearButtonId).setOnClickListener(this);
        // Cancel button
        view.findViewById(mCancelButtonId).setOnClickListener(this);
        // Save button
        view.findViewById(mDoneButtonId).setOnClickListener(this);
        // Filter list
        ((PinnedSectionListView) view.findViewById(mPinnedListId)).setOnItemClickListener(this);
        // Create adapter
        mOptionArray = new FilterOptionArrayAdapter(getActivity(), mBrandFilter.getFilterOptions());
        // Set adapter
        ((PinnedSectionListView) view.findViewById(mPinnedListId)).setAdapter(mOptionArray);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Log.d(TAG, "ON CLICK: FILTER BACK");
        int id = view.getId();
        
        // Clean current selection
        if(id == mBackButtonId || id == mCancelButtonId) {
            // Clean the current selection
            if(mSelectedOptionPos != -1) mBrandFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(false);
            // Go to back
            mParent.allowBackPressed();
            
        } else if(id == mClearButtonId) {
            // Clean the current selection
            if(mSelectedOptionPos != -1){
                mBrandFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(false);
                mSelectedOptionPos = -1;
                mOptionArray.notifyDataSetChanged();
            }
            
        } else if(id == mDoneButtonId) {
            // Save the current selection
            if(mSelectedOptionPos != -1) mBrandFilter.setSelectedOption(mSelectedOptionPos);
            else mBrandFilter.cleanSelectedOption();
            // Goto back
            mParent.allowBackPressed();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICK: FILTER OPTION " + position + " CURRENT: " + mSelectedOptionPos);
        
        // Get selected option
        FilterOption selectedOption = (FilterOption) parent.getItemAtPosition(position);
        
        // Validate if is a section item
        if(selectedOption.isSectionItem()) return;
        
        // Not multi selection 
        if(position == mSelectedOptionPos) {
            // Disable the current selection
            mSelectedOptionPos = -1;
            // Set unselected
            selectedOption.setSelected(false);
        } else { 
            // Disable old selection
            if(mSelectedOptionPos != -1) ((FilterOption) parent.getItemAtPosition(mSelectedOptionPos)).setSelected(false);
            // Save the new selection
            mSelectedOptionPos = position;
            // Set selected
            selectedOption.setSelected(true);
        }
        // Update adapter
        ((FilterOptionArrayAdapter) parent.getAdapter()).notifyDataSetChanged();
    }
    
    /**
     * Adapter
     * @author sergiopereira
     */
     public static class FilterOptionArrayAdapter extends ArrayAdapter<FilterOption> implements PinnedSectionListAdapter{
            
        private static int layout = R.layout.dialog_list_sub_item_2;
        
        private static int layoutHeader = R.layout.dialog_list_header_item;
        
        public static final int ITEM = 0;
        
        public static final int SECTION = 1;
        
        public static final int TYPES = 2;
        
        /**
         * Class used to fill the pinned list view
         * Receives an list sorted alphabetic and with section items
         * @param context
         * @param objects is setted on the parse of the CatalogFilter.
         */
        public FilterOptionArrayAdapter(Context context, List<FilterOption> objects) {
            super(context, layout, objects);
        }
        
        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Filter
            FilterOption option = getItem(position);
            // Validate item type
            if(!isItemViewTypePinned(getItemViewType(position))) {
                Log.d(TAG, "FILTER OPTION: IS ITEM");
                // Validate current view
                //if (convertView == null) 
                convertView = LayoutInflater.from(getContext()).inflate(layout, null);
                // Set title
                ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
                // Set check box
                ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
            } else {
                Log.d(TAG, "FILTER OPTION: IS TITLE");
                // Inflate section layout
                convertView = LayoutInflater.from(getContext()).inflate(layoutHeader, null);
                // Set section
                ((TextView) convertView.findViewById(R.id.dialog_list_section)).setText(option.getLabel());
            }
            // Return the filter view
            return convertView;
        }
    
        /*
         * (non-Javadoc)
         * @see android.widget.BaseAdapter#getViewTypeCount()
         */
        @Override
        public int getViewTypeCount() {
            return TYPES;
        }

        /*
         * (non-Javadoc)
         * @see android.widget.BaseAdapter#getItemViewType(int)
         */
        @Override 
        public int getItemViewType(int position) {
            return (getItem(position).isSectionItem()) ? SECTION : ITEM;
        }

        /*
         * (non-Javadoc)
         * @see com.hb.views.PinnedSectionListView.PinnedSectionListAdapter#isItemViewTypePinned(int)
         */
        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == SECTION;
        }
    }
   
}
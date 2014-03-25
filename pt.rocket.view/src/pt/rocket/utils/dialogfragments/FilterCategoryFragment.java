package pt.rocket.utils.dialogfragments;

import java.util.List;

import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterCategoryFragment extends Fragment implements OnClickListener, OnItemClickListener{
    
    private static final String TAG = FilterCategoryFragment.class.getSimpleName();

    private static int mBackButtonId = R.id.dialog_filter_header_title;
    
    private static int mClearButtonId = R.id.dialog_filter_header_clear;
    
    private static int mCancelButtonId = R.id.dialog_filter_button_cancel;
    
    private static int mDoneButtonId = R.id.dialog_filter_button_done;
    
    private static int mListId = R.id.dialog_filter_list;

    private DialogFilterFragment mParent;
    
    private CatalogFilter mCategoryFilter;

    private FilterOptionArrayAdapter mOptionArray;
    
    private int mSelectedOptionPos = -1;


    /**
     * 
     * @param parent
     * @param bundle
     * @return
     */
    public static FilterCategoryFragment newInstance(DialogFilterFragment parent, Bundle bundle) {
        Log.d(TAG, "NEW INSTANCE: BRAND");
        FilterCategoryFragment frag = new FilterCategoryFragment();
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
        mCategoryFilter = bundle.getParcelable(DialogFilterFragment.FILTER_TAG);
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
        // Get pre selected option
        if(mCategoryFilter.hasOptionSelected()){
            mSelectedOptionPos = mCategoryFilter.getSelectedOption();
            mCategoryFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(true);
        }
        // Title
        ((TextView) view.findViewById(R.id.dialog_filter_header_title)).setText(mCategoryFilter.getName());
        // Back button
        view.findViewById(mBackButtonId).setOnClickListener(this);
        // Clear button
        view.findViewById(mClearButtonId).setOnClickListener(this);
        // Cancel button
        view.findViewById(mCancelButtonId).setOnClickListener(this);
        // Save button
        view.findViewById(mDoneButtonId).setOnClickListener(this);
        // Filter list
        ((ListView) view.findViewById(mListId)).setOnItemClickListener(this);
        mOptionArray = new FilterOptionArrayAdapter(getActivity(), mCategoryFilter.getFilterOptions());
        ((ListView) view.findViewById(mListId)).setAdapter(mOptionArray);
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
            if(mSelectedOptionPos != -1) mCategoryFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(false);
            // Go to back
            mParent.allowBackPressed();
            
        } else if(id == mClearButtonId) {
            // Clean the current selection
            if(mSelectedOptionPos != -1){
                mCategoryFilter.getFilterOptions().get(mSelectedOptionPos).setSelected(false);
                mSelectedOptionPos = -1;
                mOptionArray.notifyDataSetChanged();
            }
            
        } else if(id == mDoneButtonId) {
            // Save the current selection
            if(mSelectedOptionPos != -1) mCategoryFilter.setSelectedOption(mSelectedOptionPos);
            else mCategoryFilter.cleanSelectedOption();
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
        Log.d(TAG, "ON ITEM CLICK: FILTER OPTION " + position);
        // Validate if is multi
        
        // Not multi selection 
        if(position == mSelectedOptionPos) {
            // Disable the current selection
            mSelectedOptionPos = -1;
            // Set unselected
            ((FilterOption) parent.getItemAtPosition(position)).setSelected(false);
        } else {
            // Disable old selection
            if(mSelectedOptionPos != -1) ((FilterOption) parent.getItemAtPosition(mSelectedOptionPos)).setSelected(false);
            // Save the new selection
            mSelectedOptionPos = position;
            // Set selected
            ((FilterOption) parent.getItemAtPosition(position)).setSelected(true);
        }
        // Update adapter
        ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
    }
    
    
    /**
     * 
     * @author sergiopereira
     *
     */
     public static class FilterOptionArrayAdapter extends ArrayAdapter<FilterOption> {
            
            private static int layout = R.layout.dialog_list_sub_item_2;
    
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
                // Validate current view
                if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
                // Set title
                ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
                // Set check box
                ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
                // Return the filter view
                return convertView;
            }
        
        

    }
    
    
}
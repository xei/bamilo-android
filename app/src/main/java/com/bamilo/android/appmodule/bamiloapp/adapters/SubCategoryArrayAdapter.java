package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.bamilo.android.framework.components.customfontviews.CheckBox;
import android.widget.TextView;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogCheckFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.bamilo.android.framework.service.objects.category.Category;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Class used to fill the list view with filter options
 * @author sergiopereira
 *
 */
 public class SubCategoryArrayAdapter extends ArrayAdapter<Category> implements AdapterView.OnItemClickListener{

    private final ArrayList<Category> mCategories;
    protected CatalogCheckFilter catalogFilter;

    private SparseArray<MultiFilterOptionInterface> mCurrentSelectedOptions;

    /**
     * Constructor
     */
    public SubCategoryArrayAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.list_sub_item_2, categories);
        this.mCategories = categories;
        this.mCurrentSelectedOptions = new SparseArray<>(categories.size());
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get Filter
        Category option = getItem(position);
        // Validate current view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_sub_item_2, null);
        // Set title
        ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getName());
        //setProductsCount((TextView) convertView.findViewById(R.id.dialog_products_count), option);
        // Set check box
        setCheckboxBehavior(((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)), option);
        // Return the filter view
        return convertView;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //if(catalogFilter.isMulti()) processMultiSelection(parent, position);
        //else
        processSingleSelection(parent, position);

        ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Method used to save multiple options
     * @author sergiopereira
     */
    protected void processMultiSelection(AdapterView<?> parent, int position){
        // Validate if checked or not
        MultiFilterOptionInterface option = mCurrentSelectedOptions.get(position);
        if( option == null) {
            // Add item
            addSelectedItem((MultiFilterOptionInterface) parent.getItemAtPosition(position), position);
        } else {
            // Uncheck
            cleanSelectedItem(option, position);
        }
    }

    /**
     * Method used to save only one option
     * @author sergiopereira
     */
    protected void processSingleSelection(AdapterView<?> parent, int position){
        // Option is the last
        if(mCurrentSelectedOptions.get(position) != null) {
            // Clean old selection
            cleanOldSelections();
        } else {
            // Clean old selection
            cleanOldSelections();
            // Add item
            addSelectedItem((MultiFilterOptionInterface) parent.getItemAtPosition(position), position);
        }
    }

    /**
     * Clean all old selections
     * @author sergiopereira
     */
    public void cleanOldSelections(){
        // Disable old selection
        for(int i = 0; i < mCurrentSelectedOptions.size(); i++) {
            mCurrentSelectedOptions.valueAt(i).setSelected(false);
        }
        // Clean array
        mCurrentSelectedOptions.clear();
    }

    /**
     * Save the selected item
     * @author sergiopereira
     */
    protected void addSelectedItem(MultiFilterOptionInterface option, int position){
        // Add selected
        mCurrentSelectedOptions.put(position, option);
        // Set selected
        option.setSelected(true);
    }

    /**
     * Clean selected item
     * @author sergiopereira
     */
    protected void cleanSelectedItem(MultiFilterOptionInterface option, int position){
        // Disable old selection
        option.setSelected(false);
        // Remove item
        mCurrentSelectedOptions.remove(position);
    }

    protected void setCheckboxBehavior(CheckBox checkBox, MultiFilterOptionInterface option){
        checkBox.setVisibility(View.VISIBLE);
/*        if(catalogFilter.isMulti()){
            checkBox.setChecked(option.isSelected());
        } else {*/
            if(option.isSelected()){
                checkBox.setChecked(true);
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }
        //}
    }

    protected void setProductsCount(TextView textView, MultiFilterOptionInterface option){
        /*if(option instanceof CatalogFilterOption) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(PersinConvertor.toPersianNumber(textView.getResources().getString(R.string.parenthesis_placeholder, ((CatalogFilterOption) option).getTotalProducts())));
        } else {
            textView.setVisibility(View.GONE);
        }*/
    }

    public Category getSelected()
    {
        if (mCurrentSelectedOptions.size() == 0) return null;
        return (Category)mCurrentSelectedOptions.valueAt(0);
    }
}
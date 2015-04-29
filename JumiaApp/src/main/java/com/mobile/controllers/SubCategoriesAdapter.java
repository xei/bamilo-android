package com.mobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.Category;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class is used to create an adapter for the list of categories. It is called by Category Activity <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * 
 * @modified Sergio Pereira
 * 
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class SubCategoriesAdapter extends BaseAdapter {
	@SuppressWarnings("unused" )
	private final static String TAG = LogTagHelper.create( SubCategoriesAdapter.class );

	public final static int TYPE_ALL_CATEGORIES = 0;
	public final static int TYPE_SUBCATEGORIES = 1;
	private final static int TYPE_COUNT = 2;
    private List<Category> categories;
    private LayoutInflater inflater;
    //private Context context;
    private final int CATEGORIES_LAYOUT = R.layout.category_inner_childcat;
    private final int CATEGORIES_ALL_LAYOUT = R.layout.category_inner_currentcat;
	private String categoryName;
    private String selectedCategoryId = "";
    private Activity mActivity;


    /**
     * A representation of each item on the list
     */
    private static class Item {
        public TextView textView;
    }

    /**
     * Constructor for this adapter
     * 
     * @param activity
     *            NOT USED
     * @param categories
     *            The arraylist containing the sub categories
     * @param imageLoader
     *            NOT USED
     */
    public SubCategoriesAdapter(Activity activity, ArrayList<Category> categories, String categoryName) {
        this.categories = categories;
        this.categoryName = categoryName;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivity = activity;
        selectedCategoryId = "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return this.categories.size() + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
    	if ( position == 0 )
    		return categoryName;
    	else
    		return categories.get( position - 1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    public void setAdapterData(List<Category> children, String categoryName) {
    	this.categoryName = categoryName;
        this.categories = children;
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemViewType(int position) {
    	if ( position == 0 )
    		return TYPE_ALL_CATEGORIES;
    	else
    		return TYPE_SUBCATEGORIES;
    }
    
    @Override
    public int getViewTypeCount() {
    	return TYPE_COUNT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        Item item;
        if (itemView == null) {
        	// Inflate a New Item View
        	if ( getItemViewType(position) == TYPE_ALL_CATEGORIES)
        		itemView = inflater.inflate(CATEGORIES_ALL_LAYOUT, parent, false);
        	else
        		itemView = inflater.inflate(CATEGORIES_LAYOUT, parent, false);
        	
        	item = new Item();
            item.textView = (TextView) itemView.findViewById( R.id.text);
            itemView.setTag(item);
        } else {
        	item = (Item)itemView.getTag();
        }
        	
       
        if ( position == 0 ) {
            item.textView.setText(categoryName);
//            if(!TextUtils.isEmpty(selectedCategoryId) && categories.get(0).getId().equals(selectedCategoryId)){
//                itemView.setSelected(true);
//                itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_itemhighlight));
//            } else {
//                itemView.setSelected(false);
//                itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.selector_listitem_highlight));
//            }
        } else {
            item.textView.setText(categories.get(position - 1).getName());
            if(!TextUtils.isEmpty(selectedCategoryId) && categories.get(position - 1).getId().equals(selectedCategoryId)){
                itemView.setSelected(true);
                itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_itemhighlight));
            } else {
                itemView.setSelected(false);
                itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.selector_listitem_highlight));
            }
        }
        itemView.setSelected(true);

        return itemView;
        	
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

    public void setSelectedCategory(String catId){
        selectedCategoryId = catId;
        notifyDataSetChanged();
    }
}

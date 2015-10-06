//package com.mobile.controllers;
//
//import android.app.Activity;
//import android.content.Context;
//import android.database.DataSetObserver;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//import com.mobile.components.customfontviews.TextView;
//import com.mobile.newFramework.objects.category.Category;
//import com.mobile.view.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This Class is used to create an adapter for the list of categories. It is
// * called by Category Activity <p/><br>
// *
// * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
// *
// * Unauthorized copying of this file, via any medium is strictly prohibited <br>
// * Proprietary and confidential.
// *
// * @author Sergio Pereira
// *
// * @modified Sergio Pereira
// *
// * @version 1.01
// *
// *          2012/06/19
// *
// */
//public class CategoriesAdapter extends BaseAdapter {
//
//	private List<Category> categories;
//	private LayoutInflater inflater;
//	private final int CATEGORIES_LAYOUT = R.layout.category;
//	protected Activity activity;
//
//    /**
//     * A representation of each item on the list
//     */
//    private static class Item {
//        public TextView categoryName;
//    }
//
//	/**
//	 * THE constructor
//	 *
//	 * @param context
//	 * @param categories An array list with all the categories to display
//	 * @param imageLoader  Not used
//	 */
//	public CategoriesAdapter(Activity activity, List<Category> categories) {
//		this.activity = activity;
//		this.categories = categories;
//		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.widget.Adapter#getCount()
//	 */
//	public int getCount() {
//		return this.categories.size();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.widget.Adapter#getItem(int)
//	 */
//	public Object getItem(int position) {
//		return this.categories.get(position);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.widget.Adapter#getItemId(int)
//	 */
//	public long getItemId(int position) {
//		return position;// Long.parseLong(this.categories.get(position).getId());
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
//	 */
//	public View getView(int position, View convertView, ViewGroup parent) {
//		final Item catItem;
//		View categoryItemView = convertView;
//
//		if( categoryItemView == null) {
//			categoryItemView = inflater.inflate(CATEGORIES_LAYOUT, parent, false);
//			catItem = new Item();
//			catItem.categoryName = (TextView) categoryItemView.findViewById(R.id.category_name);
//			categoryItemView.setTag(catItem);
//		} else {
//			catItem = (Item) categoryItemView.getTag();
//		}
//
//		catItem.categoryName.setText(categories.get(position).getName());
//		return categoryItemView;
//	}
//
//	/**
//	 * Update Adapter
//	 * @param ArrayList<Category>
//	 */
//	public void updateAdapter(ArrayList<Category> cats) {
//		this.categories = cats;
//		this.notifyDataSetChanged();
//	}
//
//	/**
//	 * #FIX: java.lang.IllegalArgumentException: The observer is null.
//	 * @solution from : https://code.google.com/p/android/issues/detail?id=22946
//	 */
//	@Override
//	public void unregisterDataSetObserver(DataSetObserver observer) {
//	    if(observer !=null){
//	        super.unregisterDataSetObserver(observer);
//	    }
//	}
//}

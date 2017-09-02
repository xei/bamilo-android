package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.category.Category;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.SingleLineComponent;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * This Class is used to create an adapter for the list of categories. It is
 * called by NavigationCategory Fragment <p/><br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Sergio Pereira
 * @version 1.01
 *          <p/>
 *          2012/06/19
 * @modified Paulo Carvalho
 */
public class CategoriesListAdapter extends AnimatedExpandableListAdapter {

    private final LayoutInflater mInflater;
    private final ArrayList<Category> mCategories;
    private final Context mContext;

    /**
     * A representation of each item parent list
     */
    private class ItemCategory {
        public TextView name;
        public ImageView icon;
        public View indicator;
    }

    public CategoriesListAdapter(Context context, ArrayList<Category> categories) {
        this.mCategories = categories;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getGroupCount() {
        return mCategories == null ? 0 : mCategories.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCategories.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Get current category
        Category category = (Category) getGroup(groupPosition);
        // ##### Case section #####
        if (category.isSection()) {
            View view = mInflater.inflate(R.layout.category_header, parent, false);
            ((TextView) view.findViewById(R.id.parent_category)).setText(category.getName().toUpperCase());
            return view;
        }
        // ##### Case category #####
        ItemCategory item;
        // Case category with sub
        if (convertView != null && convertView.getTag() != null) {
            item = (ItemCategory) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.gen_single_line_with_two_icons, parent, false);
            item = new ItemCategory();
            item.name = (TextView)((SingleLineComponent) convertView).getTextView();
            item.icon = ((SingleLineComponent) convertView).getStartImageView();
            item.indicator = ((SingleLineComponent) convertView).getEndImageView();

            convertView.setTag(item);
        }
        // Set Name
        item.name.setText(category.getName());
        // ##### SET INDICATOR #####
        //Case parent level
        if (category.hasChildren()) {
            item.indicator.setSelected(isExpanded);
            item.indicator.setVisibility(View.VISIBLE);
            item.indicator.setBackgroundResource(R.drawable.selector_category_button);
        }
        // Case leaf level
        else {
            item.indicator.setVisibility(View.INVISIBLE);
        }
        // ##### SET CATEGORY ICON #####
        if (TextUtils.isEmpty(category.getImage())) {
            item.icon.setVisibility(View.INVISIBLE);
        } else {
            item.icon.setVisibility(View.VISIBLE);
            item.icon.setTag(R.id.no_animate, true);
            //RocketImageLoader.instance.loadImage(category.getImage(), item.icon, false);
            ImageManager.getInstance().loadImage(category.getImage(), item.icon, null, -1, false);
        }

        //
        return convertView;

    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        Category category = (Category) getGroup(groupPosition);
        return category.hasChildren() ? category.getChildren().size() : 0;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Get child category
        Category childCategory = (Category) getChild(groupPosition, childPosition);
        // Inflate view
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gen_single_line_with_two_icons, null);
        }
        ((TextView) convertView.findViewById(R.id.tx_single_line_text)).setText(childCategory.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

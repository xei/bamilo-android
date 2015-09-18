package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.category.Category;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * This Class is used to create an adapter for the list of categories. It is
 * called by Category Activity <p/><br>
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


    private Context context;
    private LayoutInflater infalInflater;
    private final int CATEGORY_HEADER_LAYOUT = R.layout.header;
    private final int CATEGORY_LIST_ITEM_LAYOUT = R.layout.single_line_list;
    private final int CATEGORY_SUBLIST_ITEM_LAYOUT = R.layout.sublist;

    private Categories categories; // header titles
    /**
     * A representation of each item parent list
     */
    static class ItemParent {
        public TextView categoryName;
        public ImageView categorySignal;
        public ImageView categoryIcon;
    }

    public CategoriesListAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = (Categories) categories;
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories.get(groupPosition).getChildren().get(childPosition);
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
        Category parentCategory = (Category) getGroup(groupPosition);

        View itemView = convertView;
        ItemParent item;
        // different layouts based on being an header or not
        int layout = CATEGORY_LIST_ITEM_LAYOUT;

        if (parentCategory.getIsHeader()) {

            layout = CATEGORY_HEADER_LAYOUT;
            infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = infalInflater.inflate(layout, parent, false);
            item = new ItemParent();
            item.categoryName = (TextView) itemView.findViewById(R.id.parent_category);
            item.categoryName.setText(parentCategory.getName());
            return itemView;
        } else {

            if (itemView != null && itemView.getTag() != null) {
                item = (ItemParent) itemView.getTag();
            } else {
                infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = infalInflater.inflate(layout, parent, false);
                item = new ItemParent();
                item.categoryName = (TextView) itemView.findViewById(R.id.parent_category);
                item.categorySignal = (ImageView) itemView.findViewById(R.id.category_signal);
                item.categoryIcon = (ImageView) itemView.findViewById(R.id.category_icon);

                itemView.setTag(item);
            }
            if (parentCategory.hasChildren()) {
                item.categorySignal.setVisibility(View.VISIBLE);
                if (isExpanded) {
                    item.categorySignal.setSelected(true);
                } else {
                    item.categorySignal.setSelected(false);
                }
            } else {
                item.categorySignal.setVisibility(View.GONE);
            }
            if (item.categoryIcon.getDrawable() == null) {
                Print.i("ADAPTER", "NO DRAWABLE");
                RocketImageLoader.instance.loadImage(parentCategory.getImage(), item.categoryIcon, null, R.drawable.no_image_small);

            } else {
                Print.i("ADAPTER", "has DRAWABLE");
            }
            item.categoryName.setText(parentCategory.getName());
//			itemView.setTag(R.id.parent_category,parentCategory);
            return itemView;
        }

    }


    @Override
    public int getRealChildrenCount(int groupPosition) {
        return categories.get(groupPosition).getChildren().size();
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Category childCategory = (Category) getChild(groupPosition, childPosition);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(CATEGORY_SUBLIST_ITEM_LAYOUT, null);
        }
        TextView categoryChildName = (TextView) convertView.findViewById(R.id.sublist_category);
        categoryChildName.setText(childCategory.getName());
//		convertView.setTag(childCategory);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

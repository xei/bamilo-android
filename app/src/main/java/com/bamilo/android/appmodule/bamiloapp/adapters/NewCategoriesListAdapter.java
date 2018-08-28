
package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView;
import com.bamilo.android.framework.service.objects.category.Category;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.SingleLineComponent;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.R;

import java.util.ArrayList;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;

public class NewCategoriesListAdapter extends MultiLevelListAdapter {

    private Context mContext;

    private class ItemCategory {
        public TextView name;
        public ImageView icon;
        public ImageView indicator;
    }

    public NewCategoriesListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ArrayList<Category> getSubObjects(Object object) {
        return ((Category) object).getChildren();
    }

    @Override
    public boolean isExpandable(Object object) {
        return ((Category) object).hasChildren();
    }

    @Override
    public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
        Category category = (Category) object;


        if (category.isSection()) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.new_category_header, null);
            ((XeiTextView) convertView.findViewById(R.id.parent_category)).setText(category.getName().toUpperCase());
            return convertView;
        }
        // ##### Case category #####
        ItemCategory item;
        // Case category with sub
        if (convertView != null && convertView.getTag() != null) {
            item = (ItemCategory) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gen_single_line_with_two_icons, null);
            item = new ItemCategory();
            item.name = (TextView) ((SingleLineComponent) convertView).getTextView();
            item.icon = ((SingleLineComponent) convertView).getStartImageView();
            item.indicator = ((SingleLineComponent) convertView).getEndImageView();

            convertView.setTag(item);
        }


        // Set Name
        item.name.setText(category.getName());
        // ##### SET INDICATOR #####
        //Case parent level
        if (category.hasChildren()) {
            item.indicator.setBackgroundResource(itemInfo.isExpanded() ?
                    R.drawable.ic_categories_minus : R.drawable.ic_categories_plus);
            item.indicator.setSelected(false);
            item.indicator.setVisibility(View.VISIBLE);
            //item.indicator.setBackgroundResource(R.drawable.selector_category_button);
        }
        // Case leaf level
        else {
            item.indicator.setVisibility(View.INVISIBLE);
        }
        int categoryLevelOffset = 0;
        if (TextUtils.isEmpty(category.getImage())) {
            categoryLevelOffset = -1;
        }
        int paddingLevel = category.getLevel() == 0 ? 0 : category.getLevel() + categoryLevelOffset;
        int CATEGORY_ITEMS_PADDING_DIP = 32;
        int itemRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingLevel *
                CATEGORY_ITEMS_PADDING_DIP, mContext.getResources().getDisplayMetrics());
        ((RelativeLayout) item.icon.getParent()).setPadding(0, 0, itemRightPadding, 0);

        // ##### SET CATEGORY ICON #####
        if (TextUtils.isEmpty(category.getImage())) {
            item.icon.setVisibility(View.INVISIBLE);
        } else {
            item.icon.setVisibility(View.VISIBLE);
            item.icon.setTag(R.id.no_animate, true);
            ImageManager.getInstance().loadImage(category.getImage(), item.icon, null, -1, false);
        }

        //
        return convertView;


    }
}

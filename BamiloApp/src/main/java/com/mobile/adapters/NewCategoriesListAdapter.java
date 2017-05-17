
package com.mobile.adapters;

import android.content.Context;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.category.Category;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.SingleLineComponent;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;

public class NewCategoriesListAdapter extends MultiLevelListAdapter {

    Context mContext;
    private class ViewHolder {
        TextView nameView;
        TextView infoView;
        ImageView arrowView;
    }
    private class ItemCategory {
        public TextView name;
        public ImageView icon;
        public View indicator;
    }
    public NewCategoriesListAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public ArrayList<Category> getSubObjects(Object object) {
        return ((Category)object).getChildren();
        //return DataProvider.getSubItems((BaseItem) object);
    }

    @Override
    public boolean isExpandable(Object object) {
        return ((Category)object).hasChildren();
        //return DataProvider.isExpandable((BaseItem) object);
    }

    @Override
    public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
        Category category = (Category)object;


                if (category.isSection()){
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.new_category_header, null);
                    ((TextView) convertView.findViewById(R.id.parent_category)).setText(category.getName().toUpperCase());
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
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10 + category.getLevel() * 16, mContext.getResources().getDisplayMetrics());
        item.name.setPadding(padding,0,padding,0);

        // ##### SET CATEGORY ICON #####
        if (TextUtils.isEmpty(category.getImage())) {
            item.icon.setVisibility(View.INVISIBLE);
        } else {
            item.icon.setVisibility(View.VISIBLE);
            item.icon.setTag(R.id.no_animate, true);
            //RocketImageLoader.instance.loadImage(category.getImage(), item.icon, false);
            ImageManager.getInstance().loadImage(category.getImage(), item.icon, null, -1);
        }

        //
        return convertView;


    }
}

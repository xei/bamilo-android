package com.bamilo.android.appmodule.bamiloapp.view.subcategory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.framework.components.customfontviews.CheckBox;

/**
 * Created by Farshid since 9/8/2018. contact farshidabazari@gmail.com
 */
public class SubCategoryFilterHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView dialog_products_count;
    ImageView dialog_item_color_box;
    CheckBox checkBox;
    public SubCategoryFilterHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.dialog_item_title);
        dialog_products_count = itemView.findViewById(R.id.dialog_products_count);
        dialog_item_color_box = itemView.findViewById(R.id.dialog_item_color_box);
        checkBox = itemView.findViewById(R.id.dialog_item_checkbox);
    }
}

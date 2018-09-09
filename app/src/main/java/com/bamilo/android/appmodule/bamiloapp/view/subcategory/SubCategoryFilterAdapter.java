package com.bamilo.android.appmodule.bamiloapp.view.subcategory;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.bamilo.android.R;
import com.bamilo.android.framework.service.objects.catalog.filters.SubCategory;
import java.util.ArrayList;

/**
 * Created by Farshid since 6/9/2018. contact farshidabazari@gmail.com
 */
public class SubCategoryFilterAdapter extends RecyclerView.Adapter<SubCategoryFilterHolder> {

    private ArrayList<SubCategory> mSubCategories;

    public SubCategoryFilterAdapter(ArrayList<SubCategory> subCategories) {
        mSubCategories = subCategories;
    }

    @NonNull
    @Override
    public SubCategoryFilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sub_item_2, parent, false);

        return new SubCategoryFilterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryFilterHolder holder,
            final int position) {
        holder.title.setText(mSubCategories.get(position).getName());
        setCheckboxBehavior(holder);
        holder.itemView.setOnClickListener((OnClickListener) v -> onItemClicked(holder));
    }

    private void setCheckboxBehavior(SubCategoryFilterHolder holder) {
        int position = holder.getAdapterPosition();

        if (mSubCategories.get(position).isSelected()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    private void onItemClicked(SubCategoryFilterHolder holder) {
        int position = holder.getAdapterPosition();

        if (mSubCategories.get(position).isSelected()) {
            mSubCategories.get(position).setSelected(false);
            holder.checkBox.setChecked(false);
        } else {
            removeLastItemsCheckBox(position);
            mSubCategories.get(position).setSelected(true);
            notifyDataSetChanged();
        }
    }

    private void removeLastItemsCheckBox(int position) {
        for (SubCategory subCategory : mSubCategories) {
            subCategory.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        if (mSubCategories == null) {
            return 0;
        }

        return mSubCategories.size();
    }

    public SubCategory getSelectedItem() {
        for (SubCategory subCategory : mSubCategories) {
            if (subCategory.isSelected()) {
                return subCategory;
            }
        }
        return null;
    }
}
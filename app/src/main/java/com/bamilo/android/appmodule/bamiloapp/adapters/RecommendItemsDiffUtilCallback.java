package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.support.v7.util.DiffUtil;

import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.Item;

import java.util.List;

public class RecommendItemsDiffUtilCallback extends DiffUtil.Callback {
    private List<Item> newItems, oldItems;

    public RecommendItemsDiffUtilCallback(List<Item> newItems, List<Item> oldItems) {
        this.newItems = newItems;
        this.oldItems = oldItems;
    }

    @Override
    public int getOldListSize() {
        if (oldItems == null) {
            return 0;
        }
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        if (newItems == null) {
            return 0;
        }
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newItems.get(newItemPosition).getItemID()
                .equals(oldItems.get(oldItemPosition).getItemID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newItems.get(newItemPosition).getItemID()
                .equals(oldItems.get(oldItemPosition).getItemID());
    }
}

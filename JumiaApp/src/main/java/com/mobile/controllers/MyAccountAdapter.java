package com.mobile.controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/10/15
 *
 * @see <a href="http://tutorials.jenkov.com/java-concurrency/volatile.html">volatile</a>
 */
public class MyAccountAdapter {

    public interface OnItemClickListener{
        void onItemClick(ViewGroup parent, View view, int position);
    }

    private ViewGroup viewGroup;

    private BaseAdapter baseAdapter;

    private OnItemClickListener onItemClickListener;

    public MyAccountAdapter(@NonNull ViewGroup viewGroup, @NonNull BaseAdapter baseAdapter) {
        this.viewGroup = viewGroup;
        this.baseAdapter = baseAdapter;
    }

    public MyAccountAdapter(@NonNull ViewGroup viewGroup, @NonNull BaseAdapter baseAdapter, @Nullable OnItemClickListener onItemClickListener) {
        this(viewGroup, baseAdapter);
        this.onItemClickListener = onItemClickListener;
    }

    public void buildLayout() {
        int count = baseAdapter.getCount();
        for(int i = 0; i < count; i++){
            final View viewCreated = baseAdapter.getView(i, null, viewGroup);

            if(onItemClickListener != null) {
                final int finalI = i;
                viewCreated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(viewGroup, viewCreated, finalI);
                    }
                });
            }

            viewGroup.addView(viewCreated);
        }
    }

}

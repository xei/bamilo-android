/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bamilo.android.appmodule.bamiloapp.utils.datepicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bamilo.android.appmodule.bamiloapp.utils.datepicker.DatePickerDialog.OnDateChangedListener;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a selectable list of years.
 */
public class YearPickerView extends ListView implements OnItemClickListener, OnDateChangedListener {
    private static final String TAG = "YearPickerView";

    private final DatePickerController mController;
    private YearAdapter mAdapter;
    private final int mViewSize;
    private final int mChildSize;
    private TextViewWithCircularIndicator mSelectedView;

    /**
     * @param context
     */
    public YearPickerView(Context context, DatePickerController controller) {
        super(context);
        mController = controller;
        mController.registerOnDateChangedListener(this);
        ViewGroup.LayoutParams frame = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        setLayoutParams(frame);
        Resources res = context.getResources();
        mViewSize = res.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height);
        mChildSize = res.getDimensionPixelOffset(R.dimen.year_label_height);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(mChildSize / 3);
        init(context);
        setOnItemClickListener(this);
        setSelector(new StateListDrawable());
        setDividerHeight(0);
        onDateChanged();
        installAccessibilityDelegate();
    }

    private void init(Context context) {
        ArrayList<String> years = new ArrayList<String>();
        for (int year = mController.getMinYear(); year <= mController.getMaxYear(); year++) {
            years.add(String.format("%d", year));
        }
        mAdapter = new YearAdapter(context, R.layout.date_picker_year_label_text_view, years);
        setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextViewWithCircularIndicator clickedView = (TextViewWithCircularIndicator) view;
        if (clickedView != null) {
            if (clickedView != mSelectedView) {
                if (mSelectedView != null) {
                    mSelectedView.drawIndicator(false);
                    mSelectedView.requestLayout();
                }
                clickedView.drawIndicator(true);
                clickedView.requestLayout();
                mSelectedView = clickedView;
            }
            mController.onYearSelected(getYearFromTextView(clickedView));
            mAdapter.notifyDataSetChanged();
        }
    }

    private int getYearFromTextView(TextView view) {
        return Integer.valueOf(view.getText().toString());
    }

    private class YearAdapter extends ArrayAdapter<String> {

        public YearAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewWithCircularIndicator v = (TextViewWithCircularIndicator)
                    super.getView(position, convertView, parent);
            v.requestLayout();
            int year = getYearFromTextView(v);
            boolean selected = mController.getSelectedDay().year == year;
            v.drawIndicator(selected);
            if (selected) {
                mSelectedView = v;
            }
            return v;
        }
    }

    public void postSetSelectionCentered(final int position) {
        postSetSelectionFromTop(position, mViewSize / 2 - mChildSize / 2);
    }

    public void postSetSelectionFromTop(final int position, final int offset) {
        post(new Runnable() {

            @Override
            public void run() {
                setSelectionFromTop(position, offset);
                requestLayout();
            }
        });
    }

    public int getFirstPositionOffset() {
        final View firstChild = getChildAt(0);
        if (firstChild == null) {
            return 0;
        }
        return firstChild.getTop();
    }

    @Override
    public void onDateChanged() {
        mAdapter.notifyDataSetChanged();
        postSetSelectionCentered(mController.getSelectedDay().year - mController.getMinYear());
    }

    private void installAccessibilityDelegate() {
        // The accessibility delegate enables customizing accessibility behavior
        // via composition as opposed as inheritance. The main benefit is that
        // one can write a backwards compatible application by setting the delegate
        // only if the API level is high enough i.e. the delegate is part of the APIs.
        // The easiest way to achieve that is by using the support library which
        // takes the burden of checking API version and knowing which API version
        // introduced the delegate off the developer.
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() {

            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                // Note that View.onInitializeAccessibilityNodeInfo was introduced in
                // ICS and we would like to tweak a bit the text that is reported to
                // accessibility services via the AccessibilityNodeInfo.
                if (event.getEventType() == AccessibilityEventCompat.TYPE_VIEW_SCROLLED) {
                    event.setFromIndex(0);
                }
            }

        });
    }
}
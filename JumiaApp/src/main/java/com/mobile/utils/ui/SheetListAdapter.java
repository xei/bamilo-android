package com.mobile.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;

import java.util.ArrayList;

/**
 * Created by alexandrapires on 9/15/15.
 */
public class SheetListAdapter extends BaseAdapter {

    ArrayList<ProductSimple> mSimples;
    Context context;

    public SheetListAdapter(ArrayList<ProductSimple> mSimples,Context context) {

        this.mSimples = mSimples;
        this.context = context;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return mSimples.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mSimples.get(arg0);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;//mSimples.get(position).getSku();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.sublist,null);
            //  View row= inflater.inflate(R.layout.custom, parent, false);
        }

        ProductSimple simple = mSimples.get(position);
        TextView tLabelSimple = (TextView) view.findViewById(R.id.tx_slist_title);
        tLabelSimple.setText(simple.getVariationValue());

        return (view);
    }




}


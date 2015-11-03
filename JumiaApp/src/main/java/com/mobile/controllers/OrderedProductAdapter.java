//package com.mobile.controllers;
//
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.mobile.newFramework.objects.product.pojo.ProductRegular;
//import com.mobile.utils.ui.OrderedProductListViewHolder;
//import com.mobile.utils.ui.ProductListViewHolder;
//import com.mobile.utils.ui.UIUtils;
//import com.mobile.view.R;
//
//import java.util.List;
//
///**
// * Created by rsoares on 10/22/15.
// */
//public class OrderedProductAdapter extends ProductListAdapter {
//
//    public OrderedProductAdapter(@NonNull List<ProductRegular> mDataSet) {
//        super(mDataSet);
//    }
//
//    @Override
//    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new OrderedProductListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_ordered_product, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(ProductListViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        OrderedProductListViewHolder orderedHolder = (OrderedProductListViewHolder)holder;
//    }
//
//    @Override
//    protected void setFavourite(ProductListViewHolder holder, ProductRegular item, int position) {
//        UIUtils.showOrHideViews(View.GONE, holder.favourite);
//    }
//
//    @Override
//    protected void setSpecificViewForListLayout(ProductListViewHolder holder, ProductRegular item) {
//        UIUtils.showOrHideViews(View.GONE, holder.ratingContainer);
//    }
//}

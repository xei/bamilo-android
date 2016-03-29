package com.mobile.utils.home.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class HomeFeaturedTeaserAdapter extends ArrayAdapter<BaseTeaserObject> {

    private final LayoutInflater mInflater;

    private final OnClickListener mOnClickListener;


    public HomeFeaturedTeaserAdapter(Context context, int textViewResourceId, ArrayList<BaseTeaserObject> objects, OnClickListener listener) {
        super(context, textViewResourceId, objects);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOnClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductViewHolder holder;
        if(convertView == null) {
            if(ShopSelector.isRtl()){
                convertView = mInflater.inflate(R.layout._v8_home_teaser_featured_stores_item, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout._def_home_teaser_featured_stores_item, parent, false);
            }


            holder = new ProductViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ProductViewHolder) convertView.getTag();
        }
        // Get item
        BaseTeaserObject item = getItem(position);
        // Set title
        holder.title.setText(item.getTitle());
        // Set sub title
        holder.sub.setText(item.getSubTitle());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImage(), holder.image, holder.progress, R.drawable.no_image_large);
        // Set listener
        TeaserViewFactory.setClickableView(convertView, item, mOnClickListener, position);
        // Return convert view
        return convertView;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView sub;
        public ImageView image;
        public View progress;

        public ProductViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            progress = view.findViewById(R.id.home_teaser_item_progress);
            title = (TextView) view.findViewById(R.id.home_teaser_stores_text_title);
            sub = (TextView) view.findViewById(R.id.home_teaser_stores_text_sub);
        }
    }

}

package com.mobile.utils.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.EnumTeaserTargetType;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class HomeFeaturedTeaserAdapter2 extends ArrayAdapter<BaseTeaserObject> {

    private final LayoutInflater mInflater;

    private OnClickListener mOnClickListener;


    public HomeFeaturedTeaserAdapter2(Context context, int textViewResourceId, ArrayList<BaseTeaserObject> objects) {
        super(context, textViewResourceId, objects);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout._def_home_teaser_featured_stores_item, parent, false);
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
        RocketImageLoader.instance.loadImage(item.getImagePhone(), holder.image, holder.progress, R.drawable.no_image_large);
        // Set listener
        setClickableView(convertView, item);

        return convertView;
    }

    private void setClickableView(View view, BaseTeaserObject teaser) {
        if (mOnClickListener != null) {
            view.setTag(R.id.target_title, teaser.getTitle());
            view.setTag(R.id.target_type, EnumTeaserTargetType.CATALOG.getType());
            view.setTag(R.id.target_url, teaser.getUrl());
            view.setOnClickListener(mOnClickListener);
        }
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

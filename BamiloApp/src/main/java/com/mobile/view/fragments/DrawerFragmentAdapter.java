package com.mobile.view.fragments;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.extlibraries.emarsys.predict.RecommendationWidgetType;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * @author sergiopereira
 */
public class DrawerFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else if (mDataSet.get(position).isDivider()) {
            return 2;
        } else return 1;
        //return super.getItemViewType(position);
    }

    private Context mContext;

    private final ArrayList<DrawerItem> mDataSet;
    private RecommendationWidgetType recommendationWidgetType;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views
        private final ImageView mImage;
        private final View mProgress;
        private final TextView mBrand;
        private final TextView mName;
        private final TextView mPrice;
        private final TextView mOldPrice;

        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = view.findViewById(R.id.brand);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);
            mOldPrice = view.findViewById(R.id.old_price);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public DrawerFragmentAdapter(Context context, ArrayList<DrawerItem> dataSet) {
        mDataSet = dataSet;
        mContext = context;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == 0) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hamburger_menu_header, parent, false);
            vh = new HeaderViewHolder(mView);
        } else if (viewType == 1) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bamilo_drawer_item, parent, false);
            vh = new DrawerItemViewHolder(mView);
        } else if (viewType == 2) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_divider, parent, false);
            vh = new DividerViewHolder(mView);
        }
        return vh;


    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get item
        DrawerItem item = mDataSet.get(position);
        if (holder instanceof DrawerItemViewHolder) {
            DrawerItemViewHolder vh = (DrawerItemViewHolder) holder;

            if (item.getIcon() != 0) {
                vh.material_drawer_icon.setVisibility(View.VISIBLE);
                vh.material_drawer_icon.setImageResource(item.getIcon());
            } else {
                vh.material_drawer_icon.setImageDrawable(null);
                vh.material_drawer_icon.setVisibility(View.GONE);
            }

            vh.material_drawer_name.setText(item.getName());
            vh.material_drawer_name.setTextColor(mContext.getResources().getColor(item.getTextColor()));
            vh.view.setOnClickListener(item.getListener());
            vh.material_drawer_icon.setOnClickListener(item.getListener());
            vh.material_drawer_name.setOnClickListener(item.getListener());
            if (item.getBadge() > 0) {
                vh.material_drawer_badge_container.setVisibility(View.VISIBLE);
                vh.material_drawer_badge.setText(String.valueOf(item.getBadge()));
            } else {
                vh.material_drawer_badge_container.setVisibility(View.GONE);
            }
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            vh.material_drawer_account_header_name.setText(item.getUserName());
            vh.material_drawer_account_header_email.setText(TextUtils.makeDigitsEnglish(item.getEmail()));
            if (item.getGender().equals("female")) {
                vh.material_drawer_account_header_current.setImageResource(R.drawable.drawer_profile_woman);
            } else {
                vh.material_drawer_account_header_current.setImageResource(R.drawable.drawer_profile_man);

            }
            if (item.getLoginListener() != null)
                vh.material_drawer_account_header_email.setOnClickListener(item.getLoginListener());
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }


    public class DividerViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private View divider;

        private DividerViewHolder(View view) {
            super(view);
            this.view = view;
            this.divider = view.findViewById(R.id.material_drawer_divider);
        }
    }

    public class DrawerItemViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public ImageView material_drawer_icon;
        public TextView material_drawer_name;
        public TextView material_drawer_description;
        public LinearLayout material_drawer_badge_container;
        public TextView material_drawer_badge;

        private DrawerItemViewHolder(View view) {
            super(view);
            this.view = view;
            this.material_drawer_icon = view.findViewById(R.id.material_drawer_icon);
            this.material_drawer_name = view.findViewById(R.id.material_drawer_name);
            //this.material_drawer_description = (TextView) view.findViewById(R.id.material_drawer_description);
            this.material_drawer_badge_container = view.findViewById(R.id.material_drawer_badge_container);
            this.material_drawer_badge = view.findViewById(R.id.material_drawer_badge);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public ImageView material_drawer_account_header_current;
        public TextView material_drawer_account_header_name;
        public android.widget.TextView material_drawer_account_header_email;


        private HeaderViewHolder(View view) {
            super(view);
            this.view = view;
            this.material_drawer_account_header_current = view.findViewById(R.id.material_drawer_account_header_current);
            this.material_drawer_account_header_name = view.findViewById(R.id.material_drawer_account_header_name);
            this.material_drawer_account_header_email =  view.findViewById(R.id.material_drawer_account_header_email);
        }
    }

    public interface ItemVisibilityListener {
        void onItemGotVisible(View v, @StringRes int name);
    }
}

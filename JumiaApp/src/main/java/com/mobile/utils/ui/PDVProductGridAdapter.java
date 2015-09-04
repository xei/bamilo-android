package com.mobile.utils.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 * @author sergiopereira
 * @changed alexandrapires
 */
public class PDVProductGridAdapter extends RecyclerView.Adapter<PDVProductGridAdapter.ProductViewHolder> implements OnClickListener {



    private static int GRID_LINE_ITEMS_MOBILE = 2;

    private static int GRID_LINE_ITEMS_TABLET_PORTRAIT = 3;


    private boolean isTabletInLandscape=false;

    private boolean isTablet=false;

    private ArrayList<ProductRegular> mDataSet;

    private Context mContext;

    private int mLastPosition = -1;

    private OnViewHolderClickListener mOnViewHolderClicked;

    private int nColumns;

    private int itemHeight;




    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     *
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView name;
        public TextView brand;
        public ImageView image;
        public TextView price;
        public View progress;
        public ImageView favourite;


        /**
         * Constructor
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txTitle);
            brand = (TextView) view.findViewById(R.id.item_brand);
            image = (ImageView) view.findViewById(R.id.image_view);
            progress = view.findViewById(R.id.image_loading_progress);
            brand = (TextView) view.findViewById(R.id.item_brand);
            price = (TextView) view.findViewById(R.id.item_price);
     //       favourite = (ImageView) view.findViewById(R.id.image_is_favourite);

        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context - the application context
     * @param data - the array lisl
     */
    public PDVProductGridAdapter(Context context, ArrayList<ProductRegular> data) {
        mContext = context;
        mDataSet = data;
        isTabletInLandscape = DeviceInfoHelper.isTabletInLandscape(mContext);
        isTablet = context.getResources().getBoolean(R.bool.isTablet);


    }


    public int getNumberOfColumns()
    {
        //check column number
        if(!isTablet || isTabletInLandscape) nColumns = GRID_LINE_ITEMS_MOBILE;
        else nColumns = GRID_LINE_ITEMS_TABLET_PORTRAIT;
        return nColumns;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.pdp_product_item_grid;
        // Create a new view
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
     */
 /*   @Override
    public int getItemViewType(int position) {

        // Case item
        return isShowingGridLayout ? ITEM_VIEW_TYPE_GRID : ITEM_VIEW_TYPE_LIST;
    }*/

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataSet == null ? 0 : mDataSet.size();
    }

    /**
     * Get the real position validating the header view.
     * @param position - the virtual position
     * @return int
     */
    private int getRealPosition(int position) {
        return position;// - (hasHeaderView() ? 1 : 0);
    }

    /*
      * (non-Javadoc)
      * @see android.support.v7.widget.RecyclerView.Adapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
      */
    @Override
    public void onViewDetachedFromWindow(ProductViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel the animation for detached views
        holder.itemView.clearAnimation();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // Set animation
        setAnimation(holder, position);

        // Get real position
        position = getRealPosition(position);
        // Get item
        ProductRegular item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set is new image
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set is favorite image
 //       setFavourite(holder, item, position);
        // Set rating and reviews
   //     setSpecificViewForListLayout(holder, item);
        // Set prices
        setProductPrice(holder, item);
        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);

        View v = holder.itemView;
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST);
        itemHeight = v.getMeasuredHeight();

    }




    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        ProductViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate( R.layout.pdp_product_item_grid, parent, false);
            holder = new ProductViewHolder(convertView);
            // Get real position
            position = getRealPosition(position);
            // Get item
            ProductRegular item = mDataSet.get(position);
            // Set name
            holder.name.setText(item.getName());
            // Set brand
            holder.brand.setText(item.getBrand());
            // Set is new image
            // Set image
            RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
            // Set is favorite image
      //      setFavourite(holder, item, position);
            convertView.setTag(holder);
        } else {
            holder = (ProductViewHolder)convertView.getTag();
        }
    //    holder.textView.setText(mData.get(position));
        return convertView;
    }




    /**
     * Set the favourite view.
     * @param holder - the view holder
     * @param item - the product
     */
 /*   private void setFavourite(ProductViewHolder holder, ProductRegular item, int position) {
        // Set favourite data
        holder.favourite.setTag(R.id.position, position);
        holder.favourite.setSelected(item.isWishList());
        holder.favourite.setOnClickListener(this);

    }*/
    
    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setProductPrice(ProductViewHolder holder, ProductRegular item) {
        // Case discount
        holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));

 /*       if(item.hasDiscount()) {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setPaintFlags( holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.percentage.setText(String.format(mContext.getString(R.string.format_discount_percentage), item.getMaxSavingPercentage()));
            holder.percentage.setVisibility(View.VISIBLE);
        }
        // Case normal
        else {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setText("");
            holder.percentage.setVisibility(View.GONE);
        }*/
    }
    


    /**
     * Set an animation for new items.
     * @param holder - the view holder
     * @param position - the current position
     */
    private void setAnimation(ProductViewHolder holder, int position) {
        if(position > mLastPosition) {
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.entry_up_from_bottom);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }


    /**
     * Set the flag used to switch between list or grid layout
     * @param isShowingGridLayout - the flag
     */
 /*   public void updateLayout(boolean isShowingGridLayout){
        this.isShowingGridLayout = isShowingGridLayout;
        notifyDataSetChanged();
    }*/
    

    /**
     * Get the product from the current data.
     * @param position - the respective product position
     * @return Product or null
     */
    public ProductRegular getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ?  null : mDataSet.get(position);
    }
    
    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Case other sent to listener
        if (mOnViewHolderClicked != null) {
            // Get view id
            int id = view.getId();
            // position
            int position = (Integer) view.getTag(R.id.position);
            // Case favourite
            if (id == R.id.image_is_favourite) {
                mOnViewHolderClicked.onWishListClick(view, this, position);
            }

            // Case item
            else {
                mOnViewHolderClicked.onViewHolderClick(this, position);
            }
        }
    }



}

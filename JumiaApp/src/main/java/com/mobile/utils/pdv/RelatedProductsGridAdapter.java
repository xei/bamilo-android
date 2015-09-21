//package com.mobile.utils.pdv;
//
//import android.content.Context;
//import android.graphics.Paint;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//
//import com.mobile.components.customfontviews.TextView;
//import com.mobile.interfaces.OnViewHolderClickListener;
//import com.mobile.newFramework.objects.product.pojo.ProductRegular;
//import com.mobile.newFramework.utils.CollectionUtils;
//import com.mobile.newFramework.utils.shop.CurrencyFormatter;
//import com.mobile.utils.imageloader.RocketImageLoader;
//import com.mobile.view.R;
//
//import java.util.ArrayList;
//
///**
// * Adapter to fill grid for related products in Product detail section
// *
// * @author alexandrapires
// */
//public class RelatedProductsGridAdapter extends RecyclerView.Adapter<RelatedProductsGridAdapter.ProductViewHolder> implements OnClickListener {
//
//    private ArrayList<ProductRegular> mDataSet;
//
//    private Context mContext;
//
//    private int mLastPosition = -1;
//
//    private OnViewHolderClickListener mOnViewHolderClicked;
//
//
//    /**
//     * Provide a reference to the views for each data item.<br>
//     *
//     * @author alexandrapires
//     */
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//        // Data
//        public TextView name;
//        public TextView brand;
//        public ImageView image;
//        public TextView price;
//        public View progress;
//        public ImageView favourite;
//        public TextView discount;
//
//        /**
//         * Constructor
//         *
//         * @param view -  the view holder
//         */
//        public ProductViewHolder(View view) {
//            super(view);
//            name = (TextView) view.findViewById(R.id.header_text);
//            brand = (TextView) view.findViewById(R.id.item_brand);
//            image = (ImageView) view.findViewById(R.id.image_view);
//            progress = view.findViewById(R.id.image_loading_progress);
//            brand = (TextView) view.findViewById(R.id.item_brand);
//            price = (TextView) view.findViewById(R.id.item_price);
//            discount = (TextView) view.findViewById(R.id.item_discount);
//
//        }
//    }
//
//    /**
//     * Provide a suitable constructor (depends on the kind of data)
//     *
//     * @param context - the application context
//     * @param data    - list of product regular data
//     */
//    public RelatedProductsGridAdapter(Context context, ArrayList<ProductRegular> data) {
//        mContext = context;
//        mDataSet = data;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
//     */
//    @Override
//    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // Create a new view
//        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pdv_related_product_item, parent, false));
//    }
//
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
//     */
//    @Override
//    public int getItemCount() {
//        // Return the size of your dataset (invoked by the layout manager)
//        return mDataSet == null ? 0 : mDataSet.size();
//    }
//
//
//    /*
//      * (non-Javadoc)
//      * @see android.support.v7.widget.RecyclerView.Adapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
//      */
//    @Override
//    public void onViewDetachedFromWindow(ProductViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        // Cancel the animation for detached views
//        holder.itemView.clearAnimation();
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
//     */
//    @Override
//    public void onBindViewHolder(ProductViewHolder holder, int position) {
//        // Set animation
//        setAnimation(holder, position);
//        // Get item
//        ProductRegular item = mDataSet.get(position);
//        // Set name
//        holder.name.setText(item.getName());
//        // Set brand
//        holder.brand.setText(item.getBrand());
//        // Set is new image
//        // Set image
//        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
//        // Set prices
//        setProductPrice(holder, item);
//        // Set the parent layout
//        holder.itemView.setTag(R.id.position, position);
//        holder.itemView.setOnClickListener(this);
//
//    }
//
//
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        System.out.println("getView " + position + " " + convertView);
//        ProductViewHolder holder = null;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(parent.getContext()).inflate( R.layout.pdv_related_product_item, parent, false);
//            holder = new ProductViewHolder(convertView);
//            // Get item
//            ProductRegular item = mDataSet.get(position);
//            // Set name
//            holder.name.setText(item.getName());
//            // Set brand
//            holder.brand.setText(item.getBrand());
//            // Set is new image
//            // Set image
//            RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
//            // Set is favorite image
//      //      setFavourite(holder, item, position);
//            convertView.setTag(holder);
//        } else {
//            holder = (ProductViewHolder)convertView.getTag();
//        }
//        return convertView;
//    }
//
//
//    /**
//     * Set the product price.
//     *
//     * @param holder - the view holder
//     * @param item   - the product
//     */
//    private void setProductPrice(ProductViewHolder holder, ProductRegular item) {
//        // Case discount
//        if (item.hasDiscount()) {
//            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
//            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
//            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }
//        // Case normal
//        else {
//            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
//            holder.price.setText("");
//        }
//    }
//
//
//    /**
//     * Set an animation for new items.
//     *
//     * @param holder   - the view holder
//     * @param position - the current position
//     */
//    private void setAnimation(ProductViewHolder holder, int position) {
//        if (position > mLastPosition) {
//            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.entry_up_from_bottom);
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
//            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
//            holder.itemView.startAnimation(animation);
//            mLastPosition = position;
//        }
//    }
//
//
//    /**
//     * Get the product from the current data.
//     *
//     * @param position - the respective product position
//     * @return Product or null
//     */
//    public ProductRegular getItem(int position) {
//        return CollectionUtils.isEmpty(mDataSet) ? null : mDataSet.get(position);
//    }
//
//    /**
//     * Set the listener the click on view holder.
//     *
//     * @param listener - the listener
//     */
//    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
//        this.mOnViewHolderClicked = listener;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.view.View.OnClickListener#onClick(android.view.View)
//     */
//    @Override
//    public void onClick(View view) {
//        // Case other sent to listener
//        if (mOnViewHolderClicked != null) {
//            // Get view id
//            int id = view.getId();
//            // position
//            int position = (Integer) view.getTag(R.id.position);
//            // Case favourite
//            if (id == R.id.image_is_favourite) {
//                mOnViewHolderClicked.onWishListClick(view, this, position);
//            }
//
//            // Case item
//            else {
//                mOnViewHolderClicked.onViewHolderClick(this, position);
//            }
//        }
//    }
//
//
//}

package com.mobile.utils.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.service.objects.product.pojo.ProductBundle;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the grid/list of bundles in a combo on combo page.<br>
 *
 * @author sergiopereira
 */
public class ComboGridAdapter extends RecyclerView.Adapter<ComboGridAdapter.ProductViewHolder> implements OnClickListener {

    private final ArrayList<ProductBundle> mDataSet;

    private final String mProductSku;

    private OnProductViewHolderClickListener mOnViewHolderClicked;


    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     *
     * @author sergiopereira
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView brand;
        public ImageView image;
        public View progress;
        public TextView discount;
        public TextView price;
        public CheckBox checkBox;
        public TextView variation;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image_view);
            progress = view.findViewById(R.id.image_loading_progress);
            brand = (TextView) view.findViewById(R.id.item_brand);
            name = (TextView) view.findViewById(R.id.item_title);
            price = (TextView) view.findViewById(R.id.item_price);
            discount = (TextView) view.findViewById(R.id.item_discount);
            variation = (TextView) view.findViewById(R.id.item_variation);
            checkBox = (CheckBox) view.findViewById(R.id.item_check);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     */
    public ComboGridAdapter(ArrayList<ProductBundle> data, String sku) {
        mDataSet = data;
        mProductSku = sku;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.combo_fragment_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }


    public ArrayList<ProductBundle> getItems() {
        return mDataSet;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // Get item
        ProductBundle item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrandName());
        // Set image
        ImageManager.getInstance().loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_large, false);
        // Set prices
        UIProductUtils.setPriceRules(item, holder.price, holder.discount);
        // Set variation
        setVariations(holder.variation, item, position);
        // Set check box
        setCheckBox(holder.checkBox, item);
        // Set listeners
        setListeners(holder, item, position);
    }

    /**
     * Set the check box
     */
    private void setCheckBox(CheckBox view, ProductBundle item) {
        if(DeviceInfoHelper.isPosLollipop()){
            UIUtils.checkBoxDrawableStateCompat(view);
        }
        view.setChecked(item.isChecked());
        view.setEnabled(!TextUtils.equals(item.getSku(), mProductSku));
    }

    /**
     * Set variations
     */
    private void setVariations(View view, ProductBundle item, int position) {
        UIProductUtils.setVariationContent(view, item);
        view.setTag(R.id.position, position);
        view.setOnClickListener(this);
    }

    /**
     * Set listeners
     */
    private void setListeners(ProductViewHolder holder, ProductBundle item, int position) {
        // Case other items
        if (!TextUtils.equals(item.getSku(), mProductSku)) {
            holder.checkBox.setTag(R.id.position, position);
            holder.checkBox.setOnClickListener(this);
            holder.itemView.setTag(R.id.position, position);
            holder.itemView.setOnClickListener(this);
        }
    }

    /**
     * Get the product from the current data.
     *
     * @param position - the respective product position
     * @return Product or null
     */
    public ProductBundle getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ? null : mDataSet.get(position);
    }

    /**
     * Set the listener the click on view holder.
     *
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnProductViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    @Override
    public void onClick(View view) {
        // position
        int position = (Integer) view.getTag(R.id.position);
        // Validate
        if (mOnViewHolderClicked != null) {
            //Case checkbox
            if (view.getId() == R.id.item_check || view.getId() == R.id.item_variation) {
                mOnViewHolderClicked.onViewHolderItemClick(view, this, position);
            }
            // Case whole view
            else {
                mOnViewHolderClicked.onViewHolderClick(this, position);
            }
        }
    }

}

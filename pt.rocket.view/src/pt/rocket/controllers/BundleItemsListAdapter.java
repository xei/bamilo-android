package pt.rocket.controllers;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import pt.rocket.components.absspinner.IcsSpinner;
import pt.rocket.components.customfontviews.Button;
import pt.rocket.components.customfontviews.CheckBox;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.controllers.BundleItemsListAdapter.ViewHolder.OnItemChecked;
import pt.rocket.controllers.BundleItemsListAdapter.ViewHolder.OnItemSelected;
import pt.rocket.controllers.BundleItemsListAdapter.ViewHolder.OnSimplePressed;
import pt.rocket.framework.objects.CampaignItem;
import pt.rocket.framework.objects.CampaignItemSize;
import pt.rocket.framework.objects.ProductBundleProduct;
import pt.rocket.framework.objects.ProductBundleSimple;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Ad
 * @author sergiopereira
 *
 */
public class BundleItemsListAdapter extends RecyclerView.Adapter<BundleItemsListAdapter.ViewHolder> {
    
    private ArrayList<ProductBundleProduct> mDataset;
    private OnItemSelected itemSelected;
    private OnSimplePressed simplePressed;
    private OnItemChecked itemChecked;
    private OnItemSelectedListener simplesSelected;
    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br> 
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView mBrand;
        public TextView mTitle;
        private ImageView mImage;
        private ProgressBar mProgress;
        private TextView mPrice;
        private CheckBox mCheck;
//        private Button mSimple;
        private View mContainer;
        private IcsSpinner mSizeSpinner;
        private RelativeLayout mSizeSpinnerContainer;
        
        public interface OnItemSelected {
            public void SelectedItem(ProductBundleProduct selectedProduct);
        }
        
        public interface OnItemChecked {
            public void checkItem(ProductBundleProduct selectedProduct, boolean isChecked, int pos);
        }
        
        public interface OnSimplePressed {
            public void PressedSimple(ProductBundleProduct selectedProduct);
        }
        /**
         * Constructor 
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.item_container);
            mBrand = (TextView) view.findViewById(R.id.item_brand);
            mTitle = (TextView) view.findViewById(R.id.item_title);
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mCheck = (CheckBox) view.findViewById(R.id.item_check);
//            mSimple = (Button) view.findViewById(R.id.bundle_simple_button);
            mSizeSpinnerContainer = (RelativeLayout) view.findViewById(R.id.bundle_size_container);
            mSizeSpinner = (IcsSpinner) view.findViewById(R.id.bundle_simple_button);
            mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
            mPrice = (TextView) view.findViewById(R.id.item_price);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context
     * @param productTeaserGroup
     * @param parentClickListener
     * @author sergiopereira
     */
    public BundleItemsListAdapter(Context context, ArrayList<ProductBundleProduct> bundleItemsList, OnItemSelected selectedClickListener,
            OnItemChecked checkedClickListener, OnSimplePressed simpleClickListener, OnItemSelectedListener simplesSelectedListener) {
        mDataset = bundleItemsList;
        itemSelected = selectedClickListener;
        itemChecked = checkedClickListener;
        simplePressed = simpleClickListener;
        simplesSelected = simplesSelectedListener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public BundleItemsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_bundle, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Get item
        final ProductBundleProduct item = mDataset.get(position);
        // Set brand
        holder.mBrand.setText(item.getBundleProductBrand());
        // Set title
        holder.mTitle.setText(item.getBundleProductName());
        // Set image
        RocketImageLoader.instance.loadImage(item.getBundleProductImage(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        // Set price
        holder.mPrice.setText(item.getBundleProductMaxSpecialPrice());
        // Set listener and tags
//        holder.mContainer.setTag(item.getProductUrl());
        
//        holder.mCheck.setChecked(true);
        
        holder.mContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                itemSelected.SelectedItem(item);                
            }
        });
        
        if(item.isChecked()){
            holder.mCheck.setChecked(true);
        } else {
            holder.mCheck.setChecked(false);
            
        }
        
        holder.mCheck.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(holder.mCheck.isChecked()){
                    item.setChecked(true);
                } else {
                    item.setChecked(false);
                }
                itemChecked.checkItem(item, holder.mCheck.isChecked(), position);      
            }
        });
        
        
        // Set size
        setSizeContainer(holder, item, position);
        
//        holder.mSizeSpinner.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                
//                simplePressed.PressedSimple(item);                
//            }
//        });
        
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataset == null ? 0 : mDataset.size();
    }
    
    private void setSizeContainer(ViewHolder view, ProductBundleProduct item, int position){
        // Campaign has sizes except itself (>1)
        if(item.getBundleSimples().size() > 0) {
            view.mSizeSpinnerContainer.setVisibility(View.VISIBLE);
            // Show container
//            view.mSizeContainer.setVisibility(View.VISIBLE);
            // Get sizes
            ArrayList<ProductBundleSimple> sizes = item.getBundleSimples();
            // Create an ArrayAdapter using the sizes values
            ArrayAdapter<ProductBundleSimple> adapter = new ArrayAdapter<ProductBundleSimple>(JumiaApplication.INSTANCE.getApplicationContext(), R.layout.campaign_spinner_item, sizes);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.campaign_spinner_dropdown_item);
            // Apply the adapter to the spinner
            view.mSizeSpinner.setAdapter(adapter);
            // Save position in spinner
            view.mSizeSpinner.setTag(position);
            // Check pre selection
            
            view.mSizeSpinner.setSelection(item.getSimpleSelectedPos());
            // Force reload content to redraw the default selection value
            adapter.notifyDataSetChanged();
            // Apply the select listener
            view.mSizeSpinner.setTag(item.getBundleProductSku());
            view.mSizeSpinner.setOnItemSelectedListener(simplesSelected);
        } else {
            view.mSizeSpinnerContainer.setVisibility(View.GONE);
//            // Hide the size container
//            // Set itself as selected size
//            CampaignItemSize size = null;
//            try {
//                size = item.getSizes().get(0);
//            } catch (IndexOutOfBoundsException e) {
//                Log.w(TAG, "WARNING: IOBE ON SET SIZE SELECTION: 0");
//            } catch (NullPointerException e) {
//                Log.w(TAG, "WARNING: NPE ON SET SELECTED SIZE: 0");
//            }
//            item.setSelectedSizePosition(0);
//            item.setSelectedSize(size);
        }
    }
    
    public ArrayList<ProductBundleProduct> getBundleArray() {
        return mDataset;
    }
    
    public void updateBundle(ArrayList<ProductBundleProduct> productBundles) {
        mDataset = productBundles;
        notifyDataSetChanged();
    }
    
}

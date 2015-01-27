package pt.rocket.controllers;

import java.util.ArrayList;

import pt.rocket.components.HorizontalListView.OnViewHolderSelected;
import pt.rocket.components.HorizontalListView.OnViewSelectedListener;
import pt.rocket.framework.objects.Variation;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Ad
 * @author sergiopereira
 *
 */
public class ProductVariationsListAdapter extends RecyclerView.Adapter<ProductVariationsListAdapter.ViewHolder> implements OnClickListener, OnViewHolderSelected {
    
    private ArrayList<Variation> mDataset;
    private int mSelectedItem = 0;
    private OnViewSelectedListener mOnViewHolderSelected;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br> 
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Data
        private ImageView mImage;
        private ProgressBar mProgress;
        private View mMarker;
        private View mContainer;
        
        /**
         * Constructor 
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.image_container);
            mImage = (ImageView) view.findViewById(R.id.image);
            mProgress = (ProgressBar) view.findViewById(R.id.loading_progress);
            mMarker = view.findViewById(R.id.marker);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context
     * @param productTeaserGroup
     * @param parentClickListener
     * @author sergiopereira
     */
    public ProductVariationsListAdapter(ArrayList<Variation> variationsList) {
        mDataset = variationsList;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductVariationsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.productdetails_images_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Get item
        Variation item = mDataset.get(position);
        // Set image
        RocketImageLoader.instance.loadImage(item.getImage(), holder.mImage, holder.mProgress, R.drawable.no_image_small);
        // Set listener
        holder.mContainer.setTag(position);
        holder.mContainer.setOnClickListener(this);
        holder.mMarker.setSelected(mSelectedItem == position);
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
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get position
        int position = (Integer) view.getTag();
        // Set the selected position and update
        setSelectedPosition(position);
        // Get the variation URL
        String url = mDataset.get(position).getLink();
        // Send to listener
        if(mOnViewHolderSelected != null) mOnViewHolderSelected.onViewSelected(view, position, url);
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.components.HorizontalListView.OnViewHolderSelected#setSelectedPosition(int)
     */
    @Override
    public void setSelectedPosition(int position) {
        mSelectedItem = position;
        notifyDataSetChanged();   
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.components.HorizontalListView.OnViewHolderSelected#setOnViewHolderSelected(pt.rocket.components.HorizontalListView.OnViewSelectedListener)
     */
    @Override
    public void setOnViewHolderSelected(OnViewSelectedListener listener) {
        mOnViewHolderSelected = listener;
        
    }

}

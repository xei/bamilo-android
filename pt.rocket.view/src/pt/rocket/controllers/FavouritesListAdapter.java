package pt.rocket.controllers;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of favourites. It is called by FavouritesFragment
 * 
 * @author Andre Lopes
 * @modified sergiopereira
 * 
 */
public class FavouritesListAdapter extends ArrayAdapter<Favourite> {
    
    public final static String TAG = LogTagHelper.create(FavouritesListAdapter.class);

    private LayoutInflater mInflater;

    private OnClickListener mOnClickParentListener;

    /**
     * A representation of each item on the list
     */
    public static class Item {
        private ImageView image;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView discountPercentage;
        private TextView brand;
        private View isNew;
        private Button varianceButton;
        private View variantChooseError;
        private View addToCartButton;
        private View deleteButton;
        private View container;
        private View stockError;
    }
    
    /**
     * Constructor 
     * @param context
     * @param items
     * @param parentListener
     * @author sergiopereira
     */
    public FavouritesListAdapter(Context context, ArrayList<Favourite> items, OnClickListener parentListener) {
        super(context, R.layout.favourite_item, items);
        mInflater = LayoutInflater.from(context);
        mOnClickParentListener = parentListener;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.i(TAG, "ON GET VIEW: " + position);
        // Validate view
        View itemView;
        // If the view already exists there is no need to inflate it again
        if (convertView != null) itemView = convertView;
        // Inflate the view
        else itemView = mInflater.inflate(R.layout.favourite_item, parent, false);
        // Get the class associated to the view
        Item prodItem = getItemView(itemView);
        // Get favourite
        Favourite favourite = (Favourite) getItem(position);
        // Set Image
        setImage(prodItem, favourite);
        // Set brand, name and price 
        setTextContent(prodItem, favourite);
        // Set variation
        setVariationContent(prodItem, favourite);
        // Set warnings
        setWarnings(prodItem, favourite);
        // Set clickable views
        setClickableViews(position, prodItem.container, prodItem.deleteButton, prodItem.addToCartButton, prodItem.varianceButton);
        // Set incomplete item 
        setIncompleItem(prodItem.addToCartButton, favourite.isComplete());
        // Return view
        return itemView;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.i(TAG, "ON DATA SET CHANGED");
    }
    
    /**
     * Get the recycled view
     * @param view
     * @return ItemView
     * @author sergiopereira
     */
    private Item getItemView(View itemView){
        Item item;
        if ((Item) itemView.getTag() == null) {
            // Create tag
            item = new Item();
            item.container = itemView.findViewById(R.id.favourite_item_container);
            item.isNew = itemView.findViewById(R.id.favourite_item_image_is_new);
            item.image = (ImageView) itemView.findViewById(R.id.favourite_item_image);
            item.name = (TextView) itemView.findViewById(R.id.favourite_item_name);
            item.brand = (TextView) itemView.findViewById(R.id.favourite_item_brand);
            item.price = (TextView) itemView.findViewById(R.id.favourite_item_regprice);
            item.discount = (TextView) itemView.findViewById(R.id.favourite_item_discount);
            item.discountPercentage = (TextView) itemView.findViewById(R.id.favourite_item_percentage);
            item.varianceButton = (Button) itemView.findViewById(R.id.favourite_button_variant);
            item.variantChooseError = itemView.findViewById(R.id.favourite_error_variant);
            item.stockError = itemView.findViewById(R.id.favourite_error_stock);
            item.addToCartButton = itemView.findViewById(R.id.favourite_button_shop);
            item.deleteButton = itemView.findViewById(R.id.favourite_button_delete);
            itemView.setTag(item);
        } else {
            item = (Item) itemView.getTag();
        }
        return item;
    }
    
    /**
     * Disable the add to cart button for incomplete products
     * @param view
     * @param isComplete
     * @author sergiopereira
     */
    private void setIncompleItem(View view, boolean isComplete) {
        if(!isComplete) {
            view.setOnClickListener(null);
            view.setEnabled(false);
            view.setBackgroundResource(R.drawable.btn_grey);
        }
    }
    
    /**
     * Set warnings, stock and variation
     * @param prodItem
     * @param favourite
     * @author sergiopereira
     */
    private void setWarnings(Item prodItem, Favourite favourite) {
        // Set variation error visibility 
        prodItem.variantChooseError.setVisibility(favourite.showChooseVariationWarning() ? View.VISIBLE : View.GONE);
        // Set stock error visibility 
        prodItem.stockError.setVisibility(favourite.showStockVariationWarning() ? View.VISIBLE : View.GONE);
    }

    /**
     * Set this as clickable
     * @param position
     * @param views...
     * @author sergiopereira
     */
    private void setClickableViews(int position, View... views){
        // For each view add position and listener
        for (View view : views) {
            view.setTag(position);
            if(mOnClickParentListener != null) view.setOnClickListener(mOnClickParentListener);
        }
    }
    
    /**
     * Set the variation container
     * @param prodItem
     * @param favourite
     * @param position
     * @author sergiopereira
     */
    private void setVariationContent(Item prodItem, Favourite favourite){
        // Set simple button
        if(favourite.hasSimples()) {
            // Set simple value
            String simpleValue = "..."; 
            if(favourite.getSelectedSimple() != Favourite.NO_SIMPLE_SELECTED ) simpleValue = favourite.getSelectedSimpleValue();
            else favourite.setSelectedSimple(Favourite.NO_SIMPLE_SELECTED);
            // Set visibilty
            prodItem.varianceButton.setText(simpleValue);
            prodItem.varianceButton.setVisibility(View.VISIBLE);
        } else {
            // favourite.setSelectedSimple(0);
            prodItem.varianceButton.setVisibility(View.GONE);
        }
    }
    
    /**
     * Set the image view
     * @param prodItem
     * @param favourite
     * @author sergiopereira
     */
    private void setImage(Item prodItem, Favourite favourite){
        // Set is new image
        if (favourite.isNew()) prodItem.isNew.setVisibility(View.VISIBLE);
        else prodItem.isNew.setVisibility(View.GONE);
        // Set image
        String imageURL = (favourite.getImageList().size() > 0) ? imageURL = favourite.getImageList().get(0) : "";
        RocketImageLoader.instance.loadImage(imageURL, prodItem.image,  null, R.drawable.no_image_small);
    }

    /**
     * Set the text content
     * @param prodItem
     * @param favourite
     * @author sergiopereira
     */
    private void setTextContent(Item prodItem, Favourite favourite) {
        // Set brand
        prodItem.brand.setText(favourite.getBrand().toUpperCase());
        // Set name
        prodItem.name.setText(favourite.getName());
        // Validate special price
        if (null != favourite.getSpecialPrice() && !favourite.getSpecialPrice().equals(favourite.getPrice())) {
            // Set discount 
            prodItem.discount.setText(favourite.getSpecialPrice());
            prodItem.discountPercentage.setText("-" + favourite.getMaxSavingPercentage().intValue() + "%");
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
            // Set price
            prodItem.price.setText(favourite.getPrice());
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.price.setSelected(true);
            prodItem.price.setTextColor(getContext().getResources().getColor(R.color.grey_light));
            prodItem.price.setTextAppearance(getContext(), R.style.text_normal);
        } else {
            // Set price
            prodItem.discount.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.INVISIBLE);
            prodItem.price.setText(favourite.getPrice());
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(getContext(), R.style.text_bold);
            prodItem.price.setTextColor(getContext().getResources().getColor(R.color.red_basic));
        }
    }
    
}

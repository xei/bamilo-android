package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.ListIterator;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.events.ModifyWishListEvent;
import pt.rocket.framework.objects.Product;
import pt.rocket.pojo.WishlistItem;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.view.R;
import pt.rocket.view.WishListActivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

/**
 * The adapter that implements the visualization of each item on the WishList list <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.00
 * 
 *          2012/06/15
 * 
 */
public class WishlistAdapter extends BaseAdapter {

    private ArrayList<WishlistItem> items = new ArrayList<WishlistItem>();
    private Activity activity;
    private ViewGroup parent = null;
    private LayoutInflater inflater;

    // Initialize Handler
    private Handler handleComponent;

    private View currentItem;

    /**
     * A representation of each item on the list
     * 
     */
    public static class ItemView {
        public ImageView itemLogo;
        public ProgressBar imgProgress;
        public TextView itemName;
        public TextView itemPrice;

        public View itemButtonContainer;
        public ImageView itemProductArrow;

        public ImageView itemDelete;
        public ImageView itemAddCart;

    }

    /**
     * The constructor for the WishlistAdapter
     * 
     * @param activity
     *            - The parent activity where the list is
     */
    public WishlistAdapter(Activity activity) {
        this.activity = activity;
        this.items = new ArrayList<WishlistItem>();
        this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Gets the number of items on the list
     * 
     * @return the number of items that the list has
     */
    public int getCount() {
        return items.size();
    }

    /**
     * Gets an item of the list
     * 
     * @param position
     *            The position of the item we want to retrieve
     * @return A wishlist item that corresponds for the given position
     */
    public WishlistItem getItem(int position) {
        return items.get(position);
    }

    /**
     * Gets the id of the selected item
     * 
     * @param position
     *            The position of the item we want to get the id
     * @return The id of the item for the given position
     */
    public long getItemId(int position) {
        return -1;
    }

    /**
     * Gets the parent of the items
     * 
     * @return The Listview that contains all the items
     */
    public ViewGroup getParent() {
        return parent;
    }

    /**
     * Gets the view for each item
     * 
     * @param position
     * @param convertView
     * @param parent
     * 
     * @return the View that represent the item
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View wishlistItemView;
        final ItemView itemView;

        this.parent = parent;

        if (convertView != null) {
            // if the view already exists there is no need to inflate it again
            wishlistItemView = convertView;

            itemView = (ItemView) wishlistItemView.getTag();
        } else {
            // Inflate a New Item View
            wishlistItemView = inflater.inflate(R.layout.wishlistitem, null);

            itemView = new ItemView();

            // Get controls
            itemView.itemLogo = (ImageView) wishlistItemView.findViewById(R.id.item_logo);
            itemView.imgProgress = (ProgressBar) wishlistItemView.findViewById(R.id.image_loading_progress);
            itemView.itemName = (TextView) wishlistItemView.findViewById(R.id.item_name);
            itemView.itemPrice = (TextView) wishlistItemView.findViewById(R.id.item_price);
            itemView.itemDelete = (ImageView) wishlistItemView.findViewById(R.id.delete_button);
            itemView.itemAddCart = (ImageView) wishlistItemView.findViewById(R.id.add_to_cart_button);
            itemView.itemButtonContainer = wishlistItemView.findViewById(R.id.button_container);
            itemView.itemProductArrow = (ImageView) wishlistItemView.findViewById(R.id.product_arrow);

            // stores the item representation on the tag of the view for later retreival
            wishlistItemView.setTag(itemView);

        }

        // Set item information
        itemView.itemName.setText(this.items.get(position).getProduct().getName());
        itemView.itemPrice.setText(this.items.get(position).getProduct().getPrice());
        itemView.itemAddCart.setEnabled(!this.items.get(position).getIsInCart());

        // Load Image Using Lazy Framework
        ImageLoader.getInstance().displayImage(this.items.get(position).getProduct().getImages().get(0).getUrl(), itemView.itemLogo, new SimpleImageLoadingListener() {
            
            /* (non-Javadoc)
             * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(java.lang.String, android.view.View, android.graphics.Bitmap)
             */
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                itemView.imgProgress.setVisibility(View.GONE);
                itemView.itemLogo.setVisibility(View.VISIBLE);
            }

        });

        // set the listener for the click on the addtocart button
        itemView.itemAddCart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WishlistItem item = getItem(position);

                // get product information
                Product prod = item.getProduct();
                // create the shopping cart item to add
//                ShoppingCart cart = new ShoppingCart(prod.getName(), 1, prod.getPrice(), prod.getImages().get(0).getUrl(), 0, "", prod.getSKU(), prod);
//                cart.setInWishList(true);

                // send the product to the cart
                ImageView iM;
                
                //iM = (ImageView) activity.findViewById(R.id.header_button_basket);
                TextView navIm = (TextView) activity.findViewById(R.id.nav_basket_elements);

                // refresh the list
                notifyDataSetChanged();
            }
        });

        // set the listener for the click on the remove item button
        itemView.itemDelete.setOnClickListener(new OnClickListener() {

            private DialogGeneric confirmationDialog;

            @Override
            public void onClick(View v) {
                if (!((WishListActivity) activity).getDontAskOnDelete()) {
                    // Display an confirmation message to the user
                    currentItem = wishlistItemView;
                    confirmationDialog = new DialogGeneric(activity, false, true, false, activity.getString(R.string.wishlist_remove_title), activity.getString(R.string.wishlist_remove_message), activity.getString(R.string.yes_label),
                            activity.getString(R.string.no_label), new OnClickListener() {
                                
                                @Override
                                public void onClick(View v) {
                                    removeItem(wishlistItemView, position);
                                }
                            });
                    confirmationDialog.show();
                } else {
                    // removes the item from the wishlist
                    removeItem(wishlistItemView, position);
                }
            }
        });

        // Return the Wishlist Item View
        return wishlistItemView;
    }

    /**
     * Removes the selected item from the wishlist
     * 
     * @param wishlistItemView
     *            The view for the item that we want to delete. This is needed for the delete animation
     * @param position
     *            The position of the item we want to delete
     */
    private void removeItem(final View wishlistItemView, final int position) {
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.anim_remove_product_shopcart);
        anim.setAnimationListener(new AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                // remove the item from the wishlist
                ArrayList<String> selectedProductsIds = new ArrayList<String>();
                selectedProductsIds.add(getItem(position).getProduct().getSKU());

                EventManager.getSingleton().triggerRequestEvent(
                        new ModifyWishListEvent(EventType.REMOVE_ITEM_FROM_WISHLIST_EVENT,
                                selectedProductsIds));
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        wishlistItemView.startAnimation(anim);

    }

    /**
     * Sets the items list for the wishlist
     * 
     * @param products
     *            - an ArrayList of the items for populate the wishlist
     */
    public void updateWishList(ArrayList<Product> products) {
        ListIterator<Product> prodIterator = products.listIterator();

        items.clear();

        // convert all the products to the wishlist structure
        while (prodIterator.hasNext()) {
            items.add(new WishlistItem(prodIterator.next()));
        }

        // refresh the list
        notifyDataSetChanged();
    }

}

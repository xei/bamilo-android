package pt.rocket.pojo;

import pt.rocket.framework.objects.Product;

/**
 * This Class represents each item on the wishlist <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.00
 * 
 * 2012/06/15
 * 
 */
public class WishlistItem {

    private Product product;
    private boolean checked;


    /**
     * The generic constructor for the WishlistItem
     */
    public WishlistItem() {
        product = new Product();

        this.checked = false;
    }

    /**
     * The constructor for the WishlistItem
     * 
     * @param product
     *            the product associated with this wishlist item
     */
    public WishlistItem(Product product) {
        this.product = product;
        this.checked = false;

    }

    /**
     * Gets the check state of the item
     * 
     * @return if the item is checked on the list
     */
    public boolean getChecked() {
        return checked;
    }

    /**
     * Sets the checked state of the item
     * 
     * @param value
     *            - if the item is checked or not (true/ false)
     * @return - if the new value is different from the old one
     */
    public boolean setChecked(boolean value) {
        boolean hasChanges;
        if (hasChanges = (checked != value))
            checked = value;

        return hasChanges;
    }

    /**
     * Gets if the product is already on the cart or not
     * 
     * @return if the product is on the cart (true/false)
     */
    public boolean getIsInCart() {
//        // create the shopping cart item to add        
//        ShoppingCart cart = new ShoppingCart(product.getName(), 1, product.getPrice(), product.getImagesUrl().get(0), 0,"", product.getSKU(), product);
//
//        return persistentData.isProductAtShopCartById(cart.getProductSku());
        return false;
    }

    /**
     * gets the product object that is associated with this item
     * 
     * @return the product object linked to this wishlist item
     */
    public Product getProduct() {
        return this.product;
    }

}

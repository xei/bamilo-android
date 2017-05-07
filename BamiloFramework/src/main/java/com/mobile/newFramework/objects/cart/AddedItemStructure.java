package com.mobile.newFramework.objects.cart;

/**
 * Holds the structure of the items added to cart
 *
 * Created by Paulo Carvalho on 12/9/15.
 */
public class AddedItemStructure {

    private PurchaseEntity purchaseEntity;
    private int currentPos;

    public PurchaseEntity getPurchaseEntity() {
        return purchaseEntity;
    }

    public void setPurchaseEntity(PurchaseEntity purchaseEntity) {
        this.purchaseEntity = purchaseEntity;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

}

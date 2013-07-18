/** 
 * @author nunocastro
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.objects.ProductReviewCommentCreated;

/**
 * Event used to send a review request to the mobile api
 * @author nunocastro
 *
 */
public class ReviewProductEvent extends RequestEvent {

    private static final EventType type = EventType.REVIEW_PRODUCT_EVENT;

    public final String productSKU;
    public final ProductReviewCommentCreated productReviewCreated;
    public final int customerId;
    
    /**
     * 
     */
    public ReviewProductEvent(String productSKU, ProductReviewCommentCreated productReview ) {
    	super(type);
        this.productReviewCreated = productReview;
        this.productSKU = productSKU;
        this.customerId=-1;
    }    
    
    public ReviewProductEvent(String productSKU,int customerId, ProductReviewCommentCreated productReview ) {
        super(type);
        this.productReviewCreated = productReview;
        this.productSKU = productSKU;
        this.customerId=customerId;
    }  
}

package com.mobile.libraries.emarsys.predict.recommended;

import android.support.annotation.NonNull;
import android.util.Log;

import com.emarsys.predict.CartItem;
import com.emarsys.predict.CompletionHandler;
import com.emarsys.predict.Error;
import com.emarsys.predict.ErrorHandler;
import com.emarsys.predict.RecommendationRequest;
import com.emarsys.predict.RecommendationResult;
import com.emarsys.predict.RecommendedItem;
import com.emarsys.predict.Session;
import com.emarsys.predict.Transaction;
import com.mobile.app.JumiaApplication;


import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RecommendManager {

    private static final String TAG = "RecommendManager";
    private static final int RecommendLimit = 15;

    public void sendPurchaseRecommend() {
        Transaction transaction = new Transaction();
        List<CartItem> cartItems = getCartItems();
        transaction.cart(cartItems);

        String uuid = UUID.randomUUID().toString();

        transaction.purchase(uuid, cartItems);

        sendTransaction(transaction);

        //???
        //sharedCart().clear();
    }

   public void sendHomeRecommend(final RecommendListCompletionHandler callBack) {
        setEmail();
       Transaction transaction = new Transaction();
       transaction.cart(getCartItems());

           String logic = "PERSONAL";
           RecommendationRequest recommend = new RecommendationRequest(logic);
           recommend.setLimit(RecommendLimit);
           transaction.recommend(recommend, new CompletionHandler() {
               @Override
               public void onCompletion(RecommendationResult result) {
                   // Process result
                   Log.d(TAG, result.getFeatureId());

                   String category = result.getTopic();

                       List<Item> data = new ArrayList<>();

                       for (RecommendedItem next : result.getProducts()) {
                           Item item = new Item(next);
                           data.add(item);
                       }

                       callBack.onRecommendedRequestComplete(category, result.getProducts());
               }
           });


       sendTransaction(transaction);
   }

    public void sendCartRecommend(final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "CART", null, null, null, null, callBack);
    }

    public void sendCategoryRecommend(String searchTerm,
                                      String category,
                                      final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "", category, searchTerm, null, null, callBack);
    }

    public void sendRelatedRecommend(RecommendedItem item,
                                     String searchTerm,
                                     String itemId,
                                     List<String> excludeItems,
                                     final RecommendListCompletionHandler callBack) {
        sendRecommend(item, "RELATED", null, searchTerm, itemId, excludeItems, callBack);
    }

    /*public void sendAlsoBoughtRecommend(RecommendedItem recommendedItem,
                                        String itemId,
                                        final RecommendListCompletionHandler callBack) {
        sendRecommend(recommendedItem, "ALSO_BOUGHT", null, null, itemId, null, callBack);
    }*/

   /* public void sendPersonalRecommend(String searchTerm,
                                      final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "PERSONAL", null, searchTerm, null, null, callBack);
    }*/

    public void sendNoResultRecommend(String searchTerm,
                                      final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "PERSONAL", null, searchTerm, null, null, callBack);
    }

    private void sendRecommend(RecommendedItem recommendedItem,
                               String logic, String category,
                               String searchTerm,
                               String itemId,
                               List<String> excludeItems,
                               final RecommendListCompletionHandler callBack) {
        final List<Item> data = new ArrayList<>();
        setEmail();
        Transaction transaction = recommendedItem == null ?
                new Transaction() :
                new Transaction(recommendedItem);
        transaction.cart(getCartItems());

        if (category != null && !category.isEmpty()) {
            transaction.category(category);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            transaction.searchTerm(searchTerm);
        }

        if (itemId != null && !itemId.isEmpty()) {
            transaction.view(itemId);
        }

        if (!logic.isEmpty()) {
            RecommendationRequest recommend = new RecommendationRequest(logic);
            int recommendCount = RecommendLimit;
            if (logic.compareTo("RELATED") == 0) {
                recommendCount = 4;
            }
            recommend.setLimit(recommendCount);
            if (excludeItems != null) {
                recommend.excludeItemsWhereIn("items", excludeItems);
            }

            transaction.recommend(recommend, new CompletionHandler() {
                @Override
                public void onCompletion(RecommendationResult result) {
                    // Process result
                    Log.d(TAG, result.getFeatureId());

                    for (RecommendedItem next : result.getProducts()) {
                        Item item = new Item(next);
                        data.add(item);
                    }

                    callBack.onRecommendedRequestComplete("", result.getProducts());
                }
            });
        }
        sendTransaction(transaction);
    }

    private void sendTransaction(Transaction transaction) {
        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(transaction, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {

            }
        });
    }

    private List<CartItem> getCartItems()
    {
        List<CartItem> result = new ArrayList<>();
        if (JumiaApplication.INSTANCE.getCart() == null) return result;
        List<PurchaseCartItem> cartItems = JumiaApplication.INSTANCE.getCart().getCartItems();
        if (cartItems == null) return result;
        for (PurchaseCartItem purchaseCartItem:cartItems) {
            CartItem item = new CartItem(purchaseCartItem.getSku(), (float)purchaseCartItem.getPrice(), purchaseCartItem.getQuantity());
            result.add(item);
        }

        return result;
    }

    public Item getCartItem(ProductComplete product)
    {
        Item item = new Item();
        item.setItemID(product.getSku());

        return item;

    }

    public void setEmail() {
        if (JumiaApplication.CUSTOMER != null) {

            Session.getInstance().setCustomerEmail(JumiaApplication.CUSTOMER.getEmail());
            Session.getInstance().setCustomerId("" + JumiaApplication.CUSTOMER.getId());
        }
    }

}

package com.mobile.libraries.emarsys.predict.recommended;

import android.support.annotation.NonNull;
import android.util.Log;

import com.mobile.app.JumiaApplication;
import com.mobile.libraries.emarsys.predict.CompletionHandler;
import com.mobile.libraries.emarsys.predict.ErrorHandler;
import com.mobile.libraries.emarsys.predict.RecommendationRequest;
import com.mobile.libraries.emarsys.predict.RecommendationResult;
import com.mobile.libraries.emarsys.predict.RecommendedItem;
import com.mobile.libraries.emarsys.predict.Session;
import com.mobile.libraries.emarsys.predict.Transaction;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RecommendManager {

    private static final String TAG = "RecommendManager";

    public void buy() {
        Transaction transaction = new Transaction();
        List<PurchaseCartItem> cartItems = JumiaApplication.INSTANCE.getCart().getCartItems();
        transaction.cart(cartItems);

        String uuid = UUID.randomUUID().toString();

        transaction.purchase(uuid, cartItems);

        sendTransaction(transaction);

        //???
        //sharedCart().clear();
    }

    public void sendHomeRecommend(final RecommendListCompletionHandler callBack) {

        Transaction transaction = new Transaction();
        transaction.cart(JumiaApplication.INSTANCE.getCart().getCartItems());
        for (int i = 1; i < 11; i++) {
            String logic = "HOME_" + i;
            RecommendationRequest recommend = new RecommendationRequest(logic);
            recommend.setLimit(8);
            transaction.recommend(recommend, new CompletionHandler() {
                @Override
                public void onCompletion(RecommendationResult result) {
                    // Process result
                    Log.d(TAG, result.getFeatureId());

                    String category = result.getTopic();

                    if (category != null && !category.isEmpty()) {

                        List<Item> data = new ArrayList<>();

                        for (RecommendedItem next : result.getProducts()) {
                            Item item = new Item(next);
                            data.add(item);
                        }

                        callBack.onRecommendedRequestComplete(category, data);
                    }
                }
            });
        }

        sendTransaction(transaction);
    }

    public void sendCartRecommend(final RecommendCompletionHandler callBack) {
        sendRecommend(null, "CART", null, null, null, null, callBack);
    }

    public void sendCategoryRecommend(String searchTerm,
                                      String category,
                                      final RecommendCompletionHandler callBack) {
        sendRecommend(null, "CATEGORY", category, searchTerm, null, null, callBack);
    }

    public void sendRelatedRecommend(RecommendedItem item,
                                     String searchTerm,
                                     String itemId,
                                     List<String> excludeItems,
                                     final RecommendCompletionHandler callBack) {
        sendRecommend(item, "RELATED", null, searchTerm, itemId, excludeItems, callBack);
    }

    public void sendAlsoBoughtRecommend(RecommendedItem recommendedItem,
                                        String itemId,
                                        final RecommendCompletionHandler callBack) {
        sendRecommend(recommendedItem, "ALSO_BOUGHT", null, null, itemId, null, callBack);
    }


    public void sendPersonalRecommend(String searchTerm,
                                      final RecommendCompletionHandler callBack) {
        sendRecommend(null, "PERSONAL", null, searchTerm, null, null, callBack);
    }

    private void sendRecommend(RecommendedItem recommendedItem,
                               String logic, String category,
                               String searchTerm,
                               String itemId,
                               List<String> excludeItems,
                               final RecommendCompletionHandler callBack) {
        final List<Item> data = new ArrayList<>();

        Transaction transaction = recommendedItem == null ?
                new Transaction() :
                new Transaction(recommendedItem);
        transaction.cart(JumiaApplication.INSTANCE.getCart().getCartItems());

        if (category != null && !category.isEmpty()) {
            transaction.category(category);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            transaction.searchTerm(searchTerm);
        }

        if (itemId != null && !itemId.isEmpty()) {
            transaction.view(itemId);
        }

        RecommendationRequest recommend = new RecommendationRequest(logic);
        recommend.setLimit(8);

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

                callBack.onRecommendedRequestComplete(data);
            }
        });

        sendTransaction(transaction);
    }

    private void sendTransaction(Transaction transaction) {
        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(transaction, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, error.toString());
            }
        });
    }

}

package com.mobile.extlibraries.emarsys.predict.recommended;

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
import com.mobile.app.BamiloApplication;


import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.product.pojo.ProductComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RecommendManager {

    private static final String TAG = "RecommendManager";
    private static final int RecommendLimit = 15;
    private static final int HOME_RECOMMENDATION_LIMIT = 300;
    private static final String HOME_LOGIC_PREFIX = "HOME_";

    public void sendPurchaseRecommend() {
        Transaction transaction = new Transaction();
        List<CartItem> cartItems = getCartItems();

        String uuid = UUID.randomUUID().toString();


        transaction.purchase(uuid, cartItems);

        sendTransaction(transaction);

        transaction = new Transaction();
        transaction.cart(new ArrayList<CartItem>());
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

    public static Map<String, List<String>> createHomeExcludeItemListsMap(List<String>... itemLists) {
        Map<String, List<String>> resultMap = new HashMap<>();
        if (itemLists != null && itemLists.length > 0) {
            for (int i = 0; i < itemLists.length; i++) {
                String key = HOME_LOGIC_PREFIX + i;
                resultMap.put(key, itemLists[i]);
            }
        }
        return resultMap;
    }

    public void getEmarsysHomes(final RecommendListCompletionHandler callBack, EmarsysErrorCallback errorCallback, Map<String, List<String>> excludeItemLists, int homePagesCount) {
        setEmail();
        Transaction transaction = new Transaction();
        transaction.cart(getCartItems());
        for (int i = 0; i < homePagesCount; i++) {
            String logic = HOME_LOGIC_PREFIX + i;
            RecommendationRequest recommend = new RecommendationRequest(logic);
            List<String> excludeValues = excludeItemLists.get(logic);
            if (excludeValues != null) {
                recommend.excludeItemsWhereIn("item", excludeValues);
            }
            recommend.setLimit(HOME_RECOMMENDATION_LIMIT);
            transaction.recommend(recommend, new CompletionHandler() {
                @Override
                public void onCompletion(RecommendationResult result) {
                    // Process result
                    Log.d(TAG, result.getFeatureId());

                    String category = result.getTopic();

                    callBack.onRecommendedRequestComplete(category, result.getProducts());
                }


            });
        }
        sendTransactionWithErrorCallback(transaction, errorCallback);
    }

    public void sendCartRecommend(final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "CART", null, null, null, null, callBack);
    }

    public void sendPersonalRecommend(final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "PERSONAL", null, null, null, null, callBack);
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

    public void sendAlsoBoughtRecommend(RecommendedItem recommendedItem,
                                        String itemId,
                                        final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "ALSO_BOUGHT", null, null, itemId, null, callBack);
    }

    public void sendPopularRecommend(final RecommendListCompletionHandler callBack) {
        sendRecommend(null, "POPULAR", null, null, null, null, callBack);
    }

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
                recommendCount = 6;
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

    private void sendTransactionWithErrorCallback(Transaction transaction, final EmarsysErrorCallback callback) {
        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(transaction, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                if (callback != null) {
                    callback.onEmarsysRecommendError(error);
                }
            }
        });
    }

    private List<CartItem> getCartItems() {
        List<CartItem> result = new ArrayList<>();
        if (BamiloApplication.INSTANCE.getCart() == null) return result;
        List<PurchaseCartItem> cartItems = BamiloApplication.INSTANCE.getCart().getCartItems();
        if (cartItems == null) return result;
        for (PurchaseCartItem purchaseCartItem : cartItems) {
            CartItem item = new CartItem(purchaseCartItem.getSku(), (float) purchaseCartItem.getPrice(), purchaseCartItem.getQuantity());
            result.add(item);
        }

        return result;
    }

    public Item getCartItem(ProductComplete product) {
        Item item = new Item();
        item.setItemID(product.getSku());

        return item;

    }

    public void setEmail() {
        if (BamiloApplication.isCustomerLoggedIn() && BamiloApplication.CUSTOMER != null) {

            Session.getInstance().setCustomerEmail(BamiloApplication.CUSTOMER.getEmail());
            Session.getInstance().setCustomerId("" + BamiloApplication.CUSTOMER.getId());
        } else {
            Session.getInstance().setCustomerEmail(null);
            Session.getInstance().setCustomerId(null);
        }
    }

    public interface EmarsysErrorCallback {
        void onEmarsysRecommendError(Error error);
    }

}

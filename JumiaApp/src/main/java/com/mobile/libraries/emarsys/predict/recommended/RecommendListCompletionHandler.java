package com.mobile.libraries.emarsys.predict.recommended;


import java.util.List;

public abstract class RecommendListCompletionHandler {

    public abstract void onRecommendedRequestComplete(String category, List<Item> data);
}

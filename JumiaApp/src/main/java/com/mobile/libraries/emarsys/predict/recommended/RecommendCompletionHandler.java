package com.mobile.libraries.emarsys.predict.recommended;


import java.util.List;

public abstract class RecommendCompletionHandler {

    public abstract void onRecommendedRequestComplete(List<Item> resultData);
}

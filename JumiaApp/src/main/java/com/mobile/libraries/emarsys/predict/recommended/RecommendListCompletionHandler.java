package com.mobile.libraries.emarsys.predict.recommended;


import com.emarsys.predict.RecommendedItem;

import java.util.List;

public abstract class RecommendListCompletionHandler {

    public abstract void onRecommendedRequestComplete(String category, List<RecommendedItem> data);
}

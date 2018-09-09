package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended;


import com.emarsys.predict.RecommendedItem;

import java.util.List;

public interface RecommendListCompletionHandler {

    void onRecommendedRequestComplete(String category, List<RecommendedItem> data);
}

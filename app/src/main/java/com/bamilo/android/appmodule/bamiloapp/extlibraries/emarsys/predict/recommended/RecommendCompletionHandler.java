package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended;


import com.emarsys.predict.RecommendedItem;

import java.util.List;

public abstract class RecommendCompletionHandler {

    public abstract void onRecommendedRequestComplete(List<Item> resultData);
}

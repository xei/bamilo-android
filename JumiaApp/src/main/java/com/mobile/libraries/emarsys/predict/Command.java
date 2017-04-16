/*
 * Copyright 2016 Scarab Research Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*


package com.mobile.libraries.emarsys.predict;

import android.util.Log;

import com.mobile.libraries.emarsys.predict.RecommendationRequest;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.squareup.okhttp.HttpUrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

*/
/**
 * Base wrapper class for the commands.
 *//*

abstract class Command {

    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        return new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
    }

    com.mobile.libraries.emarsys.predict.ErrorParameter createEmptyStringErrorParameter(String command, String field) {
        String msg = "Invalid argument in " + command + " command: " + field
                + " should not be an empty string";
        Log.d(getClass().getSimpleName(), msg);
        return new com.mobile.libraries.emarsys.predict.ErrorParameter("INVALID_ARG", command, msg);
    }

    abstract void buildQuery(HttpUrl.Builder urlBuilder);

}

*/
/**
 * Base wrapper class for the rules.
 *//*

abstract class Filter extends com.mobile.libraries.emarsys.predict.Command {

    String catalogField;
    String rule;
    List<String> values;

    Filter(List<String> values, String rule, String catalogField) {
        this.values = values;
        this.rule = rule;
        this.catalogField = catalogField;
    }

    Filter(String value, String rule, String catalogField) {
        this(Arrays.asList(value), rule, catalogField);
    }

}

*/
/**
 * Wraps the exclude rules.
 *//*

class ExcludeCommand extends com.mobile.libraries.emarsys.predict.Filter {

    ExcludeCommand(List<String> values, String rule, String catalogField) {
        super(values, rule, catalogField);
    }

    ExcludeCommand(String value, String rule, String catalogField) {
        super(value, rule, catalogField);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        for (String next : values) {
            if (next.isEmpty()) {
                ret.add(createEmptyStringErrorParameter("exclude", catalogField));
            }
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {

    }

}

*/
/**
 * Wraps the include rules.
 *//*

class IncludeCommand extends com.mobile.libraries.emarsys.predict.Filter {

    IncludeCommand(List<String> values, String rule, String catalogField) {
        super(values, rule, catalogField);
    }

    IncludeCommand(String value, String rule, String catalogField) {
        super(value, rule, catalogField);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        for (String next : values) {
            if (next.isEmpty()) {
                ret.add(createEmptyStringErrorParameter("include", catalogField));
            }
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {

    }

}

*/
/**
 * Wraps the cart command.
 *//*

class CartCommand extends com.mobile.libraries.emarsys.predict.Command {

    private final List<PurchaseCartItem> items;

    CartCommand(List<PurchaseCartItem> items) {
        this.items = items;
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        for (PurchaseCartItem next : items) {
            if (next.getSku().isEmpty()) {
                ret.add(createEmptyStringErrorParameter("cart", "itemId"));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        List<String> l = new ArrayList<String>();
        for (PurchaseCartItem next : items) {
            String s = "";
            s += "i:";
            s += next.getSku();
            s += ",p:";
            s += next.getPrice();
            s += ",q:";
            s += next.getQuantity();
            l.add(s);
        }
        return com.mobile.libraries.emarsys.predict.StringUtil.toStringWithDelimiter(l, "|");
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("ca", toString());
    }

}

*/
/**
 * Base wrapper class for the simple string commands.
 *//*

class StringCommand extends com.mobile.libraries.emarsys.predict.Command {

    String value;

    StringCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {

    }

}

*/
/**
 * Wraps the availabilityZone command.
 *//*

class AvailabilityZoneCommand extends com.mobile.libraries.emarsys.predict.StringCommand {

    AvailabilityZoneCommand(String value) {
        super(value);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("availabilityZone", "availabilityZone"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("az", toString());
    }

}

*/
/**
 * Wraps the category command.
 *//*

class CategoryCommand extends com.mobile.libraries.emarsys.predict.StringCommand {

    CategoryCommand(String value) {
        super(value);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("category", "category"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("vc", toString());
    }

}

*/
/**
 * Wraps the keyword command.
 *//*

class KeywordCommand extends com.mobile.libraries.emarsys.predict.StringCommand {

    KeywordCommand(String value) {
        super(value);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("keyword", "keyword"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("k", toString());
    }

}

*/
/**
 * Wraps the tag command.
 *//*

class TagCommand extends com.mobile.libraries.emarsys.predict.StringCommand {

    TagCommand(String value) {
        super(value);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("tag", "tag"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("t", toString());
    }

}

*/
/**
 * Wraps the purchase command.
 *//*

class PurchaseCommand extends com.mobile.libraries.emarsys.predict.CartCommand {

    PurchaseCommand(String orderId, List<PurchaseCartItem> items) {
        super(items);
        this.orderId = orderId;
    }

    private final String orderId;

    String getOrderId() {
        return orderId;
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (orderId.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("purchase", "orderId"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("co", toString()).addQueryParameter("oi", getOrderId());
    }

}

*/
/**
 * Wraps the searchTerm command.
 *//*

class SearchTermCommand extends com.mobile.libraries.emarsys.predict.StringCommand {

    SearchTermCommand(String value) {
        super(value);
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("searchTerm", "searchTerm"));
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("q", toString());
    }

}

*/
/**
 * Wraps the view command.
 *//*

class ViewCommand extends com.mobile.libraries.emarsys.predict.Command {

    private final String itemId;
    private final RecommendedItem trackedItem;

    ViewCommand(String itemId, RecommendedItem trackedItem) {
        this.itemId = itemId;
        this.trackedItem = trackedItem;
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        if (itemId.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("view", "itemId"));
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "i:";
        ret += itemId;
        if (trackedItem != null) {
            ret += ",t:";
            ret += trackedItem.getResult().getFeatureId();
            ret += ",c:";
            ret += trackedItem.getResult().getCohort();
        }
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {
        urlBuilder.addQueryParameter("v", toString());
    }

}

*/
/**
 * Wraps the recommend command.
 *//*

class RecommendCommand extends com.mobile.libraries.emarsys.predict.Command {

    RecommendCommand(RecommendationRequest recommendationRequest) {
        this.recommendationRequest = recommendationRequest;
    }

    private final RecommendationRequest recommendationRequest;

    RecommendationRequest getRecommendationRequest() {
        return recommendationRequest;
    }

    @Override
    List<com.mobile.libraries.emarsys.predict.ErrorParameter> validate() {
        List<com.mobile.libraries.emarsys.predict.ErrorParameter> ret = new ArrayList<com.mobile.libraries.emarsys.predict.ErrorParameter>();
        // Validate logic
        if (recommendationRequest.getLogic().isEmpty()) {
            ret.add(createEmptyStringErrorParameter("recommend", "logic"));
        }
        // Validate all baselines
        if (recommendationRequest.getBaseline() != null) {
            for (String next : recommendationRequest.getBaseline()) {
                if (next.isEmpty()) {
                    ret.add(createEmptyStringErrorParameter("recommend", "baseline"));
                }
            }
        }
        // Validate all filters
        for (com.mobile.libraries.emarsys.predict.Filter next : recommendationRequest.getFilters()) {
            if (next.catalogField.isEmpty()) {
                ret.add(createEmptyStringErrorParameter(
                        (next instanceof com.mobile.libraries.emarsys.predict.IncludeCommand) ? "include" : "exclude", "catalogField")
                );
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "f:";
        ret += recommendationRequest.getLogic();
        ret += ",l:";
        ret += recommendationRequest.getLimit();
        ret += ",o:";
        ret += 0;
        return ret;
    }

    @Override
    void buildQuery(HttpUrl.Builder urlBuilder) {

    }

}
*/

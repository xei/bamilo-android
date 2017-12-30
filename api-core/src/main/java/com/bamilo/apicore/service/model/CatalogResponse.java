package com.bamilo.apicore.service.model;

import com.bamilo.apicore.service.model.data.catalog.Catalog;
import com.bamilo.apicore.service.model.data.catalog.Filter;
import com.bamilo.apicore.service.model.data.catalog.Option;
import com.bamilo.apicore.service.model.data.catalog.PriceFilterOption;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/25/2017.
 */

public class CatalogResponse extends ServerResponse {
    @Expose
    @SerializedName(JsonConstants.RestConstants.SUCCESS)
    private boolean success;

    @Expose
    @SerializedName(JsonConstants.RestConstants.METADATA)
    private Catalog catalog;

    public CatalogResponse() {
        super(null, null);
    }

    public CatalogResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    @Override
    EventType getEventType() {
        return EventType.GET_CATALOG_EVENT;
    }

    @Override
    EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (jsonObject != null && gson != null) {
            success = jsonObject.get(JsonConstants.RestConstants.SUCCESS).getAsBoolean();
            jsonObject = jsonObject.getAsJsonObject(JsonConstants.RestConstants.METADATA);
            if (jsonObject != null) {
                catalog = gson.fromJson(jsonObject, Catalog.class);
                List<Filter> filters = catalog.getFilters();
                JsonArray filtersArray = jsonObject.getAsJsonArray(JsonConstants.RestConstants.FILTERS);
                if (filtersArray != null) {
                    for (int i = 0; i < filtersArray.size(); i++) {
                        JsonObject filterObject = filtersArray.get(i).getAsJsonObject();
                        Filter filter = filters.get(i);
                        if (filterObject.has(JsonConstants.RestConstants.OPTION)) {
                            JsonElement optionElement = filterObject.get(JsonConstants.RestConstants.OPTION);
                            if (optionElement.isJsonArray()) {
                                Type collectionType = new TypeToken<List<Option>>() {
                                }.getType();
                                filter.setOption((List<Option>) gson.fromJson(optionElement, collectionType));
                            } else {
                                filter.setPriceFilterOption(gson.fromJson(optionElement, PriceFilterOption.class));
                            }
                        }
                    }
                }
            }
        }
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }
}

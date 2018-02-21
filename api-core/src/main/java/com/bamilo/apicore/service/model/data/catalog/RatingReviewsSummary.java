
package com.bamilo.apicore.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingReviewsSummary {

    @SerializedName("average")
    @Expose
    private Long average;
    @SerializedName("ratings_total")
    @Expose
    private Long ratingsTotal;
    @SerializedName("reviews_total")
    @Expose
    private Long reviewsTotal;

    public Long getAverage() {
        return average;
    }

    public void setAverage(Long average) {
        this.average = average;
    }

    public Long getRatingsTotal() {
        return ratingsTotal;
    }

    public void setRatingsTotal(Long ratingsTotal) {
        this.ratingsTotal = ratingsTotal;
    }

    public Long getReviewsTotal() {
        return reviewsTotal;
    }

    public void setReviewsTotal(Long reviewsTotal) {
        this.reviewsTotal = reviewsTotal;
    }

}

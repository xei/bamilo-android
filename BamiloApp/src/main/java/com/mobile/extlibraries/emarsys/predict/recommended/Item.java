package com.mobile.libraries.emarsys.predict.recommended;

import com.emarsys.predict.RecommendedItem;

import java.io.Serializable;
import java.util.Map;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemID;
    private String link;
    private String title;
    private String image;
    private String category;
    private double price;
    private boolean available;
    private String brand;

    private RecommendedItem recommendedItem;

    public Item() {
    }

    public Item(RecommendedItem recommendedItem) {
        this.recommendedItem = recommendedItem;
        convertItem();
    }

    public String getItemID() {
        return itemID;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getBrand() {
        return brand;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public RecommendedItem getRecommendedItem() {
        return recommendedItem;
    }

    private void convertItem() {
        Map<String, Object> data = recommendedItem.getData();

        for (String key : data.keySet()) {
            switch (key) {
                case "item": {
                    setItemID((String) data.get(key));
                }
                break;
                case "link": {
                    setLink((String) data.get(key));
                }
                break;
                case "title": {
                    setTitle((String) data.get(key));
                }
                break;
                case "image": {
                    setImage((String) data.get(key));
                }
                break;
                case "price": {
                    setPrice((Double) data.get(key));
                }
                break;
                case "category": {
                    setCategory((String) data.get(key));
                }
                break;
                case "available": {
                    setAvailable((Boolean) data.get(key));
                }
                break;
                case "brand": {
                    setBrand((String) data.get(key));
                }
                break;
            }
        }
    }
}

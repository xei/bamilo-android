package com.mobile.view.components;

import com.bamilo.apicore.service.model.data.*;

import java.util.ArrayList;
import java.util.List;

public class BaseViewComponentFactory {

    public static BaseViewComponent createBaseViewComponent(BaseComponent baseComponent) {
        if (baseComponent != null) {
            if (baseComponent instanceof SliderComponent) {
                return createSliderViewComponent((SliderComponent) baseComponent);
            } else if (baseComponent instanceof CarouselComponent) {
                return createCarouselViewComponent((CarouselComponent) baseComponent);
            } else if (baseComponent instanceof DealComponent) {
                return createDailyDealViewComponent((DealComponent) baseComponent);
            } else if (baseComponent instanceof TileComponent) {
                return createTileViewComponent((TileComponent) baseComponent);
            }
        }
        return null;
    }

    private static TileViewComponent createTileViewComponent(TileComponent tileComponent) {
        List<TileViewComponent.TileItem> tileItems = new ArrayList<>();

        for (TileComponent.Tile tile : tileComponent.getTiles()) {
            TileViewComponent.TileItem tempTile = new TileViewComponent.TileItem(tile.getPortraitImage(), tile.getTarget());
            tileItems.add(tempTile);
        }

        TileViewComponent tileViewComponent = new TileViewComponent();
        tileViewComponent.setContent(tileItems);
        return tileViewComponent;
    }

    private static DailyDealViewComponent createDailyDealViewComponent(DealComponent dealComponent) {
        DailyDealViewComponent.DealItem dealItem = new DailyDealViewComponent.DealItem();

        dealItem.componentBackgroundColor = dealComponent.getBackgroundColor();
        if (dealComponent.getDealHeader() != null) {
            DealComponent.DealHeader dealHeader = dealComponent.getDealHeader();
            dealItem.dealTitle = dealHeader.getTitle();
            dealItem.dealTitleColor = dealHeader.getTitleTextColor();

            if (dealHeader.getMoreOptions() != null) {
                DealComponent.DealMoreOptions dealMoreOptions = dealHeader.getMoreOptions();
                dealItem.moreOptionsTitle = dealMoreOptions.getMoreOptionsTitle();
                dealItem.moreOptionsTargetLink = dealMoreOptions.getMoreOptionsTarget();
                dealItem.moreOptionsTitleColor = dealMoreOptions.getMoreOptionsTitleColor();
            }

            if (dealHeader.getDealCountDown() != null) {
                DealComponent.DealCountDown dealCountDown = dealHeader.getDealCountDown();
                dealItem.countDownTextColor = dealCountDown.getCounterTextColor();
                dealItem.countDownRemainingSeconds = dealCountDown.getCounterRemainingSeconds();
                dealItem.countDownStartTimeSeconds = dealCountDown.getInitialTimeSeconds();
            }
        }

        List<DailyDealViewComponent.Product> dealProducts = new ArrayList<>();
        if (dealComponent.getDealBody() != null) {
            if (dealComponent.getDealBody().getProducts() != null) {
                for (DealComponent.Product product : dealComponent.getDealBody().getProducts()) {
                    DailyDealViewComponent.Product tempProduct = new DailyDealViewComponent.Product();
                    tempProduct.sku = product.getSku();
                    tempProduct.name = product.getName();
                    tempProduct.brand = product.getBrand();
                    tempProduct.thumb = product.getImage();
                    tempProduct.maxSavingPercentage = product.getMaxSavingPercentage();
                    if (product.getSpecialPrice() == 0) {
                        tempProduct.price = product.getPrice();
                    } else {
                        tempProduct.price = product.getSpecialPrice();
                        tempProduct.oldPrice = product.getPrice();
                    }
                    tempProduct.hasStock = product.isHasStock();
                    dealProducts.add(tempProduct);
                }
            }
        }
        dealItem.dealProducts = dealProducts;

        DailyDealViewComponent dealViewComponent = new DailyDealViewComponent();
        dealViewComponent.setContent(dealItem);
        return dealViewComponent;
    }

    private static CategoriesCarouselViewComponent createCarouselViewComponent(CarouselComponent carouselComponent) {
        List<CategoriesCarouselViewComponent.CategoryItem> categoryItems = new ArrayList<>();
        for (CarouselComponent.CarouselItem carouselItem : carouselComponent.getCarouselItems()) {
            CategoriesCarouselViewComponent.CategoryItem tempItem =
                    new CategoriesCarouselViewComponent.CategoryItem(carouselItem.getTitle(), carouselItem.getPortraitImage(), carouselItem.getTarget());
            categoryItems.add(tempItem);
        }
        CategoriesCarouselViewComponent carouselViewComponent = new CategoriesCarouselViewComponent();
        carouselViewComponent.setContent(categoryItems);
        return carouselViewComponent;
    }

    private static SliderViewComponent createSliderViewComponent(SliderComponent sliderComponent) {
        SliderViewComponent sliderViewComponent = new SliderViewComponent();
        List<SliderViewComponent.Item> items = new ArrayList<>();

        for (SliderComponent.Slide slide : sliderComponent.getSlides()) {
            SliderViewComponent.Item tempItem = new SliderViewComponent.Item(slide.getPortraitImage(), slide.getTarget());
            items.add(tempItem);
        }

        sliderViewComponent.setContent(items);
        return sliderViewComponent;
    }
}

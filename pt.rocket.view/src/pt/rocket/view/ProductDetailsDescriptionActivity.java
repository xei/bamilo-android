package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class ProductDetailsDescriptionActivity extends MyActivity {
    private final static String TAG = LogTagHelper.create(ProductDetailsDescriptionActivity.class);

    private TextView mProductName;
    private TextView mProductResultPrice;
    private TextView mProductNormalPrice;
    private TextView mProductFeaturesText;
    private TextView mProductDescriptionText;
    private TextView mProductDetailsText;
    private CompleteProduct mCompleteProduct;

    private View mProductFeaturesContainer;

    public ProductDetailsDescriptionActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SHARE),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.product_details_title, R.layout.productdetailsdescription);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }

    private void init() {
        mCompleteProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        displayProductInformation();
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        mProductName = (TextView) findViewById(R.id.product_name);
        mProductResultPrice = (TextView) findViewById(R.id.product_price_result);
        mProductNormalPrice = (TextView) findViewById(R.id.product_price_normal);

        mProductFeaturesContainer = findViewById(R.id.product_features_container);
        mProductFeaturesText = (TextView) findViewById(R.id.product_features_text);
        mProductDescriptionText = (TextView) findViewById(R.id.product_description_text);
        mProductDetailsText = (TextView) findViewById(R.id.product_details_text);
    }

    private void displayProductInformation() {
        mProductName.setText(mCompleteProduct.getName());
        displayPriceInformation();
        displaySpecification();
        displayDescription();
        displayDetails();
    }

    private void displayPriceInformation() {
        String unitPrice = mCompleteProduct.getPrice();
        if (unitPrice == null) {
            unitPrice = mCompleteProduct.getMaxPrice();
        }
        String specialPrice = mCompleteProduct.getSpecialPrice();
        if (specialPrice == null)
            specialPrice = mCompleteProduct.getMaxSpecialPrice();

        displayPriceInfo(unitPrice, specialPrice);
    }

    private void displayPriceInfo(String unitPrice, String specialPrice) {

        if (specialPrice == null && unitPrice == null) {
            mProductNormalPrice.setVisibility(View.GONE);
            mProductResultPrice.setVisibility(View.GONE);
        } else if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the normal price
            mProductResultPrice.setText(unitPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mProductResultPrice.setText(specialPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setText(unitPrice);
            mProductNormalPrice.setVisibility(View.VISIBLE);
            mProductNormalPrice.setPaintFlags(mProductNormalPrice.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
        if (TextUtils.isEmpty(shortDescription)) {
            mProductFeaturesContainer.setVisibility(View.GONE);
            return;
        } else {
            mProductFeaturesText.setVisibility(View.VISIBLE);
        }

        String translatedDescription = shortDescription.replace("\r", "<br>");
        Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");
        mProductFeaturesText.setText(Html.fromHtml(translatedDescription));
    }

    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        String translatedDescription = longDescription.replace("\r", "<br>");
        mProductDescriptionText.setText(Html.fromHtml(translatedDescription));
    }

    private void displayDetails() {
        // TODO: where do the details come from
        // The database for complete product delivers two similar texts.
        // It looks strange to show them both as "long description" and as details
        findViewById(R.id.product_details_container).setVisibility(View.GONE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return true;
    }
}

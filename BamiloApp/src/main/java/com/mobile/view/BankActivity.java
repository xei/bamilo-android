package com.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.classes.models.EmarsysEventModel;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.tracking.CategoryConstants;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.output.Print;

import java.util.ArrayList;
import java.util.List;

/**
 * for process bank payment from browser
 *
 * @author Shahrooz Jahanshah
 */
public class BankActivity extends Activity {
    private static final String LAUNCH_FROM_URL = "com.payment.browser";
    private static final String TAG = BankActivity.class.getSimpleName();
    private String mOrderNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bank_layout);

        Button btnOrderDetails = (Button) findViewById(R.id.btnOrderDetails);
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        ImageView checkout_image = (ImageView) findViewById(R.id.checkout_image);
        TextView launchInfo = (TextView) findViewById(R.id.tvPaymentMessage);
        TextView tvOrderInfo = (TextView) findViewById(R.id.tvOrderInfo);
        btnOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
                if (mOrderNumber != null) {
                    myIntent.putExtra(ConstantsIntentExtra.ORDER_NUMBER, mOrderNumber);
                }
                startActivity(myIntent);
                finish();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        Intent bankIntent = getIntent();
        if (bankIntent != null && bankIntent.getAction().equals(LAUNCH_FROM_URL)) {
            Bundle bundle = bankIntent.getExtras();
            if (bundle != null) {
                String msgFromBrowserUrl = bundle.getString("msg_from_browser");
                mOrderNumber = bundle.getString(ConstantsIntentExtra.ORDER_NUMBER);
                if (mOrderNumber != null) {
                    try {
                        Integer.parseInt(mOrderNumber);
                    } catch (Exception e) {
                        mOrderNumber = null;
                    }
                }
                if (mOrderNumber == null) {
                    btnOrderDetails.setVisibility(View.INVISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                }
                PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
                if (cart == null) {
                    cart = new PurchaseEntity();
                }
                ArrayList<PurchaseCartItem> cartItems=  cart.getCartItems();
                StringBuilder categories = new StringBuilder();
                if (cartItems != null) {
                    for (PurchaseCartItem cat : cartItems) {
                        categories.append(cat.getCategories());
                    }
                }
                if (msgFromBrowserUrl.equals("reject")) {

                    // Track screen
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_PAYMENT_FAILURE.getName()),
                            getString(R.string.gaScreen), "", 0);
                    TrackerManager.trackScreen(this, screenModel, false);

                    // Track Purchase
                    EmarsysEventModel purchaseEventModel = new EmarsysEventModel(null, null, null, SimpleEventModel.NO_VALUE,
                            EmarsysEventModel.createPurchaseEventModelAttributes(categories.toString(), (long) cart.getTotal(), true));
                    TrackerManager.trackEvent(this, EventConstants.Purchase, purchaseEventModel);

                    btnOrderDetails.setVisibility(View.INVISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                    launchInfo.setText(R.string.payment_unsuccessful);
                    launchInfo.setTextColor(ContextCompat.getColor(this, R.color.black_800));
                    launchInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    checkout_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_reject_checkout));
                } else {

                    // Track Purchase
                    EmarsysEventModel purchaseEventModel = new EmarsysEventModel(null, null, null, SimpleEventModel.NO_VALUE,
                            EmarsysEventModel.createPurchaseEventModelAttributes(categories.toString(), (long) cart.getTotal(), false));
                    TrackerManager.trackEvent(this, EventConstants.Purchase, purchaseEventModel);

                    // Track Checkout Finish
                    SimpleEventModel sem = new SimpleEventModel();
                    sem.category = CategoryConstants.CHECKOUT;
                    sem.action = EventActionKeys.CHECKOUT_FINISH;
                    sem.label = null;
                    sem.value = SimpleEventModel.NO_VALUE;
                    if (cartItems != null) {
                        List<String> skus = new ArrayList<>();
                        for (PurchaseCartItem item : cartItems) {
                            skus.add(item.getSku());
                        }
                        sem.label = android.text.TextUtils.join(",", skus);
                        sem.value = (long) cart.getTotal();
                    }
                    TrackerManager.trackEvent(this, EventConstants.CheckoutFinished, sem);

                    launchInfo.setText(R.string.thank_you_order_title);
                    tvOrderInfo.setVisibility(View.VISIBLE);
                    if (mOrderNumber != null) {
                        tvOrderInfo.setText(String.format("%s\n%s %s", getString(R.string.order_success),
                                getString(R.string.bank_activity_order_number_title), mOrderNumber));
                    } else {
                        tvOrderInfo.setText(R.string.order_success);
                    }
                    triggerClearCart();
                }
            }
        } else {
            Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
            startActivity(myIntent);
        }
    }

    private void triggerClearCart() {
        Print.i(TAG, "TRIGGER: CLEAR CART FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, null);
    }

    protected final void triggerContentEventNoLoading(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Request
        BamiloApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }
}

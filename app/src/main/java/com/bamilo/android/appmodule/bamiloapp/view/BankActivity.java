package com.bamilo.android.appmodule.bamiloapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ClearShoppingCartHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.tracking.TrackingPage;

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

        Button btnOrderDetails = findViewById(R.id.btnOrderDetails);
        Button btnReturn = findViewById(R.id.btnReturn);
        ImageView checkout_image = findViewById(R.id.checkout_image);
        TextView launchInfo = findViewById(R.id.tvPaymentMessage);
        TextView tvOrderInfo = findViewById(R.id.tvOrderInfo);
        btnOrderDetails.setOnClickListener(v -> {
            Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
            if (mOrderNumber != null) {
                myIntent.putExtra(ConstantsIntentExtra.ORDER_NUMBER, mOrderNumber);
            }
            startActivity(myIntent);
            finish();
        });
        btnReturn.setOnClickListener(v -> {
            Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
            startActivity(myIntent);
            finish();
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
                    MainEventModel purchaseEventModel = new MainEventModel(null, null, null, SimpleEventModel.NO_VALUE,
                            MainEventModel.createPurchaseEventModelAttributes(categories.toString(), (long) cart.getTotal(), true));
                    TrackerManager.trackEvent(this, EventConstants.Purchase, purchaseEventModel);

                    btnOrderDetails.setVisibility(View.INVISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                    launchInfo.setText(R.string.payment_unsuccessful);
                    launchInfo.setGravity(Gravity.CENTER);
                    launchInfo.setTextColor(ContextCompat.getColor(this, R.color.black_800));
                    launchInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    checkout_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_reject_checkout));
                } else {

                    // Track Purchase
                    MainEventModel purchaseEventModel = new MainEventModel(null, null, null, SimpleEventModel.NO_VALUE,
                            MainEventModel.createPurchaseEventModelAttributes(categories.toString(), (long) cart.getTotal(), false));
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
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, null);
    }

    protected final void triggerContentEventNoLoading(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        BamiloApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }
}

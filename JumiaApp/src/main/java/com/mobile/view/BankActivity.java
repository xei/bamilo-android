package com.mobile.view;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.a4s.sdk.plugins.annotations.UseA4S;
import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.fragments.CheckoutThanksFragment;

/**
 * for process bank payment from browser
 * @author Shahrooz Jahanshah
 */
@UseA4S
public class BankActivity extends Activity {
    private static final String LAUNCH_FROM_URL = "com.payment.browser";
    private static final String TAG = BankActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bank_layout);

        Button btn = (Button) findViewById(R.id.back_main);
        ImageView checkout_image = (ImageView) findViewById(R.id.checkout_image);
        TextView launchInfo = (TextView)findViewById(R.id.launch_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
                startActivity(myIntent);
            }
        });
        Intent bankIntent = getIntent();
        if(bankIntent != null && bankIntent.getAction().equals(LAUNCH_FROM_URL)){
            Bundle bundle = bankIntent.getExtras();
            if(bundle != null){
                String msgFromBrowserUrl = bundle.getString("msg_from_browser");
                if (msgFromBrowserUrl.equals("reject")) {
                    launchInfo.setText("پرداخت شما ناموفق بود");
                    checkout_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_reject_checkout));
                }
                else
                {
                    launchInfo.setText("پرداخت شما با موفقیت پرداخت شد");
                    triggerClearCart();
                }
            }
        }else{
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
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }
}

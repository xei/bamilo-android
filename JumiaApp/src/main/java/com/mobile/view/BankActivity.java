package com.mobile.view;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.a4s.sdk.plugins.annotations.UseA4S;

/**
 * for process bank payment from browser
 * @author Shahrooz Jahanshah
 */
@UseA4S
public class BankActivity extends Activity {
    private static final String LAUNCH_FROM_URL = "com.payment.browser";

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
                }
            }
        }else{
            Intent myIntent = new Intent(BankActivity.this, MainFragmentActivity.class);
            startActivity(myIntent);
        }
    }
}
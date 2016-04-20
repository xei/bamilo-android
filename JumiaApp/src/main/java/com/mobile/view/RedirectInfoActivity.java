package com.mobile.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.configs.RedirectInfo;

public class RedirectInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout
        setContentView(R.layout._def_redirect_info_page);
        // Get redirect object
        final RedirectInfo redirect = getIntent().getExtras().getParcelable(ConstantsIntentExtra.DATA);
        // Set tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.redirect_info_tool_bar);


//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mSupportActionBar.setDisplayHomeAsUpEnabled(false);
//        mSupportActionBar.setDisplayShowCustomEnabled(true);
//        mSupportActionBar.setCustomView(R.layout.action_bar_initial_logo_layout);

        // Set html info
        ((TextView) findViewById(R.id.redirect_info_text)).setText(redirect.getHtml());
        // Set button link
        findViewById(R.id.redirect_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}

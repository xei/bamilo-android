package com.bamilo.modernbamilo.product.descspec

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.util.extension.replaceFragmentInActivity
import com.bamilo.modernbamilo.util.logging.Logger

private const val TAG_DEBUG  = "DescSpecActivity"
private const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"

fun startActivity(context: Context, productId: String) {
    val intent = Intent(context, DescSpecActivity::class.java).apply {
        putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    }
    context.startActivity(intent)

    Logger.log("DescSpecActivity has started for product: $productId", TAG_DEBUG)
}

class DescSpecActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desc_spec)

        replaceFragmentInActivity(DescSpecFragment.newInstance(intent.getStringExtra(KEY_EXTRA_PRODUCT_ID)), R.id.activityDescSpec_frameLayout_container)
    }
}

package com.bamilo.modernbamilo.product.rate

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bamilo.modernbamilo.R

private const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"

fun startActivity(invokerContext: Context, productId: String) {
    val intent = Intent(invokerContext, SubmitRateActivity::class.java)
    intent.putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    invokerContext.startActivity(intent)
}

class SubmitRateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_rate)
    }
}

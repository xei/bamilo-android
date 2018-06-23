package com.bamilo.modernbamilo.product.sellerslist

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bamilo.modernbamilo.R

const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"

fun startActivity(invokerContext: Context, productId: String) {
    val startIntent = Intent(invokerContext, SellersListActivity::class.java)
    startIntent.putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    invokerContext.startActivity(startIntent)
}

class SellersListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sellers_list)
    }
}

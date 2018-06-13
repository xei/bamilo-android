package com.mobile.view.productdetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mobile.view.R
import com.mobile.view.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.content_product_detail_title_score)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
//        bindToolbarClickListener()
    }

    private fun bindToolbarClickListener() {
//        binding.productDetailAppImageViewBack.setOnClickListener { _ -> super.onBackPressed() }
//        binding.productDetailAppImageViewCart.setOnClickListener { _ -> gotoCartView() }
    }

    private fun gotoCartView() {
    }
}

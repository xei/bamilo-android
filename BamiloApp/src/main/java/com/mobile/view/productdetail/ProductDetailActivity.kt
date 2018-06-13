package com.mobile.view.productdetail

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.mobile.view.R
import com.mobile.view.productdetail.slider.DepthPageTransformer
//import com.mobile.view.databinding.ActivityProductDetailBinding
import com.mobile.view.productdetail.slider.ProductSliderPagerAdapter

class ProductDetailActivity : AppCompatActivity() {

//    private lateinit var binding: ContentPdvSliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding = DataBindingUtil.*/setContentView( R.layout.content_pdv_slider)
//        bindToolbarClickListener()
        val viewPager: ViewPager = findViewById(R.id.contentProductSlider_viewPager)
        val pagerAdapter: PagerAdapter = ProductSliderPagerAdapter(supportFragmentManager, 8)
        viewPager.setPageTransformer(true, DepthPageTransformer())
        viewPager.adapter = pagerAdapter
    }

    private fun bindToolbarClickListener() {
//        binding.productDetailAppImageViewBack.setOnClickListener { _ -> super.onBackPressed() }
//        binding.productDetailAppImageViewCart.setOnClickListener { _ -> gotoCartView() }
    }

    private fun gotoCartView() {
    }
}

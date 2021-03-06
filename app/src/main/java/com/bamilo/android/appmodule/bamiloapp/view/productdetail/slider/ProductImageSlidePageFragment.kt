package com.bamilo.android.appmodule.bamiloapp.view.productdetail.slider

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 6/13/2018.
 * contact farshidabazari@gmail.com
 */
class ProductImageSlidePageFragment : Fragment() {
    fun newInstance(imageUrl: String): ProductImageSlidePageFragment {
        val productImageSlidePageFragment = ProductImageSlidePageFragment()
        val bundle = Bundle()
        bundle.putString("image", imageUrl)
        productImageSlidePageFragment.arguments = bundle
        return productImageSlidePageFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.content_pdv_slider_image, container, false)
        val image: AppCompatImageView = view.findViewById(R.id.pdvSlider_appImageView_Image)
        val progressBar: ProgressBar = view.findViewById(R.id.pdvSlider_progressBar)

        ImageManager.getInstance().loadImage(arguments?.getString("image"),
                image,
                progressBar,
                R.drawable.no_image_large,
                true)
        return view
    }
}
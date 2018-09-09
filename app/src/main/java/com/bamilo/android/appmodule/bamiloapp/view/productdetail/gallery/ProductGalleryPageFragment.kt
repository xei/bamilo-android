package com.bamilo.android.appmodule.bamiloapp.view.productdetail.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager
import com.bamilo.android.R
import com.ortiz.touch.TouchImageView

/**
 * Created by Farshid
 * since 6/13/2018.
 * contact farshidabazari@gmail.com
 */
class ProductGalleryPageFragment : Fragment() {
    fun newInstance(imageUrl: String): ProductGalleryPageFragment {
        val productImageSlidePageFragment = ProductGalleryPageFragment()
        val bundle = Bundle()
        bundle.putString("image", imageUrl)
        productImageSlidePageFragment.arguments = bundle
        return productImageSlidePageFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.content_pdv_gallery_slider_image, container, false)
        val image: TouchImageView = view.findViewById(R.id.pdvGallery_touchImageView_Image)
        val progressBar: ProgressBar = view.findViewById(R.id.pdvGallery_progressBar)
        arguments?.getString("image")

        ImageManager.getInstance().loadImage(arguments?.getString("image"),
                image,
                progressBar,
                R.drawable.no_image_large,
                false)
        return view
    }
}
package com.mobile.view.productdetail.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.mobile.utils.imageloader.ImageManager
import com.mobile.utils.photoview.PhotoView
import com.mobile.view.R

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
        val image: PhotoView = view.findViewById(R.id.pdvGallery_photoView_Image)
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
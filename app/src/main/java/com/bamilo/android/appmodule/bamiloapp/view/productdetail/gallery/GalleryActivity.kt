package com.bamilo.android.appmodule.bamiloapp.view.productdetail.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.bamilo.android.appmodule.modernbamilo.app.BaseActivity
import com.bamilo.android.framework.components.ghostadapter.GhostAdapter

import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.OnItemClickListener
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Image
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.gallery.GalleryBottomImageItem

import java.lang.Exception

const val KEY_EXTRA_IMAGES = "KEY_EXTRA_IMAGES"

@Suppress("UNCHECKED_CAST")
class GalleryActivity : BaseActivity() {

    private var images: ArrayList<Image>? = null

    private var adapter = GhostAdapter()
    private var items = ArrayList<Any>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var zoomableViewPager: ViewPager

    companion object {

        @JvmStatic
        fun start(invokerContext: Context, images: ArrayList<Image>) {
            val intent = Intent(invokerContext, GalleryActivity::class.java)
            intent.putExtra(KEY_EXTRA_IMAGES, images)
            invokerContext.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallerya)
        onCloseItemClicked()
        getImagesListFromBundle()

        if (images != null && images!!.size > 0) {
            showImages()
        } else {
            showNoImageView()
        }
    }

    private fun getImagesListFromBundle() {
        try {
            images = intent.extras[KEY_EXTRA_IMAGES] as ArrayList<Image>?
        } catch (e: Exception) {
        }
    }

    private fun showImages() {
        showBottomImages()
        showViewPagerImages()
    }

    private fun showViewPagerImages() {
        setupViewPager()
        addImagesToViewPager()
    }

    private fun addImagesToViewPager() {
        val pagerAdapter: PagerAdapter = ProductGalleryPagerAdapter(supportFragmentManager,
                ArrayList(images?.asReversed()))

        zoomableViewPager.adapter = pagerAdapter
        zoomableViewPager.currentItem = pagerAdapter.count - 1
    }

    private fun setupViewPager() {
        zoomableViewPager = findViewById(R.id.gallery_zoomableViewPager_viewPager)
        bindViewPagerPageChangeListener()
    }

    private fun bindViewPagerPageChangeListener() {
        zoomableViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                recyclerView.scrollToPosition(items.size - position - 1)
                deSelectImages()
                (items[items.size - position - 1] as GalleryBottomImageItem).selectImage()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun showBottomImages() {
        setupRecyclerView()
        addImagesToRecyclerView()
    }

    private fun addImagesToRecyclerView() {
        var selectImage = true

        for (i in 0 until images!!.size) {
            items.add(GalleryBottomImageItem(images!![i].medium!!,
                    selectImage,
                    object : OnItemClickListener {
                        override fun onItemClicked(any: Any?) {
                            deSelectImages()

                            val position = any as Int
                            val gotoPosition = items.size - position - 1
                            recyclerView.scrollToPosition(gotoPosition)
                            zoomableViewPager.currentItem = gotoPosition
                        }
                    }))
            selectImage = false
        }

        adapter.setItems(items)
    }

    private fun deSelectImages() {
        for (item in items) {
            (item as GalleryBottomImageItem).deSelectImage()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.gallery_recyclerView_images)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
    }

    private fun showNoImageView() {
    }

    private fun onCloseItemClicked() {
        findViewById<AppCompatImageView>(R.id.gallery_appImageView_close).setOnClickListener { onBackPressed() }
    }
}

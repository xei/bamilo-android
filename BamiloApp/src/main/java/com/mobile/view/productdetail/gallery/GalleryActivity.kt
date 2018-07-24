package com.mobile.view.productdetail.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.mobile.components.ghostadapter.GhostAdapter
import com.mobile.view.R
import com.mobile.view.productdetail.OnItemClickListener
import com.mobile.view.productdetail.model.Image
import com.mobile.view.productdetail.viewtypes.gallery.GalleryBottomImageItem
import java.lang.Exception

const val KEY_EXTRA_IMAGES = "KEY_EXTRA_IMAGES"

@Suppress("UNCHECKED_CAST")
class GalleryActivity : AppCompatActivity() {

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
        val pagerAdapter: PagerAdapter = ProductGalleryPagerAdapter(supportFragmentManager, images)
        zoomableViewPager.adapter = pagerAdapter
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
                recyclerView.scrollToPosition(position)
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
        for (imageUrl in images!!) {
            items.add(GalleryBottomImageItem(imageUrl.medium!!, object : OnItemClickListener {
                override fun onItemClicked(any: Any?) {
                    val position = any as Int
                    recyclerView.scrollToPosition(position)
                    zoomableViewPager.currentItem = position
                }
            }))
        }
        adapter.setItems(items)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.gallery_recyclerView_images)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
    }

    private fun showNoImageView() {
    }

    private fun onCloseItemClicked() {
        findViewById<AppCompatImageView>(R.id.gallery_appImageView_close).setOnClickListener { onBackPressed() }
    }
}

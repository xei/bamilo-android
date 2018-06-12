package com.bamilo.modernbamilo.util.extension

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageFromNetwork(url: String) {
    if (!url.isEmpty()) {
        Glide.with(context).load(url).into(this)
    }
}
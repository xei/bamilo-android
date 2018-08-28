package com.bamilo.android.appmodule.modernbamilo.product.descspec.desc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo.DescriptionRow
import com.bamilo.android.appmodule.modernbamilo.util.extension.loadImageFromNetwork

class DescriptionRecyclerAdapter(private val mDescriptionRows: ArrayList<DescriptionRow>) : RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_description, parent, false)
        return DescriptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {

        if (!mDescriptionRows[position].title.isNullOrEmpty()) {
            holder.titleTextView.text = mDescriptionRows[position].title
            holder.titleTextView.visibility = View.VISIBLE
        } else {
            holder.titleTextView.visibility = View.GONE
        }

        if (!mDescriptionRows[position].hint.isNullOrEmpty()) {
            holder.hintTextView.text = mDescriptionRows[position].hint
            holder.hintTextView.visibility = View.VISIBLE
        } else {
            holder.hintTextView.visibility = View.GONE
        }

        if (!mDescriptionRows[position].description.isNullOrEmpty()) {
            holder.descriptionTextView.text = mDescriptionRows[position].description
            holder.descriptionTextView.visibility = View.VISIBLE
        } else {
            holder.descriptionTextView.visibility = View.GONE
        }

        if (!mDescriptionRows[position].image.isNullOrEmpty()) {
            holder.imageImageView.loadImageFromNetwork(mDescriptionRows[position].image!!)
            holder.imageImageView.visibility = View.VISIBLE
        } else {
            holder.imageImageView.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = mDescriptionRows.size

    inner class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.rowDescription_xeiTextView_title) as TextView
        val hintTextView = itemView.findViewById(R.id.rowDescription_xeiTextView_hint) as TextView
        val descriptionTextView = itemView.findViewById(R.id.rowDescription_xeiTextView_description) as TextView
        val imageImageView = itemView.findViewById(R.id.rowDescription_imageView_image) as ImageView
    }
}

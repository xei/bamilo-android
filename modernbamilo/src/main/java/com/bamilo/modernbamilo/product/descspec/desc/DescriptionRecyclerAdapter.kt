package com.bamilo.modernbamilo.product.descspec.desc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.customview.DateTimeView
import com.bamilo.modernbamilo.product.descspec.desc.pojo.DescriptionRow
import com.bamilo.modernbamilo.product.sellerslist.pojo.SellerViewModel
import com.bamilo.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.modernbamilo.util.extension.persianizeNumberString

class DescriptionRecyclerAdapter(private val mDescriptionRows: ArrayList<DescriptionRow>) : RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_description, parent, false)
        return DescriptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = mDescriptionRows.size

    inner class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}

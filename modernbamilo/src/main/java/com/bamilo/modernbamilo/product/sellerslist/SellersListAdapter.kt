package com.bamilo.modernbamilo.product.sellerslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.customview.DateTimeView
import com.bamilo.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.modernbamilo.util.extension.persianizeNumberString

class SellersListAdapter(private val mSellersViewModels: List<SellerViewModel>) : RecyclerView.Adapter<SellersListAdapter.SellerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_sellerslistadapter, parent, false)
        return SellerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        holder.titleTextView.text = mSellersViewModels[position].title
        holder.deliveryTimeTextView.setTime(mSellersViewModels[position].deliveryTime)
        holder.rateTextView.text = mSellersViewModels[position].rate.toString().persianizeDigitsInString()
        holder.payableAmountTextView.text = mSellersViewModels[position].payableAmount.toString()
        holder.discountPercentTextView.text = holder.itemView.context.resources.getString(R.string.suffix_percent, mSellersViewModels[position].discount.toString().persianizeNumberString())
        holder.baseAmountTextView.text = mSellersViewModels[position].baseAmount.toString()
        holder.addToCartButton.setOnClickListener {
            Toast.makeText(holder.addToCartButton.context, mSellersViewModels[position].sellerId, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int = mSellersViewModels.size

    inner class SellerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_sellerTitle) as TextView
        val deliveryTimeTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_dateTimeView_deliveryTime) as DateTimeView
        val rateTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_sellerRate) as TextView
        val payableAmountTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_priceView_payablePrice) as TextView
        val discountPercentTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_discountPercent) as TextView
        val baseAmountTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_priceView_basePrice) as TextView
        val addToCartButton = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiButton_addToCart) as Button
    }
}

package com.bamilo.android.appmodule.modernbamilo.product.sellerslist.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.customview.DateTimeView
import com.bamilo.android.appmodule.modernbamilo.customview.PriceView
import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.viewmodel.SellersListItemViewModel
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeNumberString

class SellersListAdapter(
        private val mSellersViewModels: List<SellersListItemViewModel>,
        private val mOnAddToCartButtonClickListener: OnAddToCartButtonClickListener
) : RecyclerView.Adapter<SellersListAdapter.SellerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_sellerslistadapter, parent, false)
        return SellerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        holder.titleTextView.text = mSellersViewModels[position].title
        holder.deliveryTimeTextView.setTime(mSellersViewModels[position].deliveryTime)

        if (mSellersViewModels[position].isRateValid) {
            holder.rateTextView.visibility = View.VISIBLE
            holder.rateLabelTextView.visibility = View.VISIBLE
            holder.rateTextView.text = mSellersViewModels[position].rate.toString().persianizeDigitsInString()
        } else {
            holder.rateTextView.visibility = View.GONE
            holder.rateLabelTextView.visibility = View.GONE
        }

        holder.payableAmountTextView.setPrice(mSellersViewModels[position].payableAmount.toString(), mSellersViewModels[position].currency)

        if (mSellersViewModels[position].discount != 0) {
            holder.discountPercentTextView.visibility = View.VISIBLE
            holder.discountPercentTextView.text = holder.itemView.context.resources.getString(R.string.suffix_percent, mSellersViewModels[position].discount.toString().persianizeNumberString())
        } else {
            holder.discountPercentTextView.visibility = View.GONE
        }
        holder.discountPercentTextView.text = holder.itemView.context.resources.getString(R.string.suffix_percent, mSellersViewModels[position].discount.toString().persianizeNumberString())


        if (mSellersViewModels[position].baseAmount != 0L) {
            holder.baseAmountTextView.visibility = View.VISIBLE
            holder.baseAmountTextView.setPrice(mSellersViewModels[position].baseAmount.toString(), mSellersViewModels[position].currency)
        } else {
            holder.baseAmountTextView.visibility = View.GONE
        }

        holder.addToCartButton.setOnClickListener {
            mOnAddToCartButtonClickListener.onAddToCartButtonClicked(mSellersViewModels[position].sku)
        }
    }

    override fun getItemCount(): Int = mSellersViewModels.size

    inner class SellerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_sellerTitle) as TextView
        val deliveryTimeTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_dateTimeView_deliveryTime) as DateTimeView
        val rateLabelTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_sellerRateLabel) as TextView
        val rateTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_sellerRate) as TextView
        val payableAmountTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_priceView_payablePrice) as PriceView
        val discountPercentTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiTextView_discountPercent) as TextView
        val baseAmountTextView = itemView.findViewById(R.id.layoutRowSellerslistadapter_priceView_basePrice) as PriceView
        val addToCartButton = itemView.findViewById(R.id.layoutRowSellerslistadapter_xeiButton_addToCart) as Button
    }

    interface OnAddToCartButtonClickListener {
        fun onAddToCartButtonClicked(sku: String)
    }

}

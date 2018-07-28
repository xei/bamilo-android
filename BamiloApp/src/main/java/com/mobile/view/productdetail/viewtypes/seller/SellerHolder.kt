package com.mobile.view.productdetail.viewtypes.seller

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.mobile.components.customfontviews.TextView
import com.mobile.view.R
import net.cachapa.expandablelayout.ExpandableLayout

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
class SellerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var sellerName: TextView = itemView.findViewById(R.id.pdvSellerInfo_textView_name)

    var sellerScore: TextView = itemView.findViewById(R.id.pdvSellerScore_textView_score)
    var overallScore: TextView = itemView.findViewById(R.id.sellerScore_textView_score)
    var sellerScoreMaxValue: TextView = itemView.findViewById(R.id.pdvSellerScore_textView_maxValue)

    var collaborationPeriod: TextView = itemView.findViewById(R.id.pdvSellerInfo_textView_collaborationPeriod)
    var collaborationLable: TextView = itemView.findViewById(R.id.pdvSellerInfo_textView_collaborationPeriodLabel)

    var warranty: TextView = itemView.findViewById(R.id.pdvSellerInfo_textView_warranty)
    var deliveryTimeAndCity: TextView = itemView.findViewById(R.id.pdvSellerInfo_texView_deliverTimeAndCity)
    var otherSellersCount: TextView = itemView.findViewById(R.id.pdvSellerInfo_textView_otherSellersCount)

    var successfulSupplyProgress: ProgressBar = itemView.findViewById(R.id.sellerScore_progressBar_successfulSupply)
    var salesWithoutReturnProgress: ProgressBar = itemView.findViewById(R.id.sellerScore_progressBar_salesWithoutReturn)
    var sendOnTimeProgress: ProgressBar = itemView.findViewById(R.id.sellerScore_progressBar_sendOnTime)

    var otherSellersDivider: View = itemView.findViewById(R.id.pdvSellerInfo_view_otherSellersDivider)

    var sellerScoreParent: ConstraintLayout = itemView.findViewById(R.id.pdvSellerInfo_constraintLayout_sellerScoreParent)

    var noScoreLayout: LinearLayout = itemView.findViewById(R.id.pdvSellerInfo_relativeLayout_noScore)

    var otherSellerLayout: LinearLayout = itemView.findViewById(R.id.pdvSellerInfo_linearLayout_otherSellers)
    var scoreLayout: LinearLayout = itemView.findViewById(R.id.pdvSellerInfo_relativeLayout_sellerScoreTitleParent)

    var deliveryCitySpinner: AppCompatSpinner = itemView.findViewById(R.id.pdv_spinner_deliveryCity)
    var deliveryRegionSpinner: AppCompatSpinner = itemView.findViewById(R.id.pdv_spinner_deliveryRegion)
    var newBadge: AppCompatImageView = itemView.findViewById(R.id.pdvSellerInfo_appImageView_newBadge)

    var warrantyLayout: LinearLayout = itemView.findViewById(R.id.pdvSellerInfo_linearLayout_warrantyRoot)
    var percentageLayout: LinearLayout = itemView.findViewById(R.id.pdvSellerInfo_linearLayout_percentageRoot)
}
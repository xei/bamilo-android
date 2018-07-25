@file:Suppress("DEPRECATION")

package com.mobile.view.productdetail.viewtypes.seller

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.mobile.components.customfontviews.TextView
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.service.utils.TextUtils
import com.mobile.utils.ui.UIUtils
import com.mobile.utils.ui.WarningFactory
import com.mobile.view.R
import com.mobile.view.productdetail.PDVMainView
import com.mobile.view.productdetail.model.Score
import com.mobile.view.productdetail.model.Seller
import com.mobile.view.productdetail.network.RegionWebApi
import com.mobile.view.productdetail.network.model.City
import com.mobile.view.productdetail.network.model.Region
import com.mobile.view.productdetail.network.response.DeliveryTimeData
import com.mobile.view.productdetail.network.response.DeliveryTimeResponse
import com.mobile.view.productdetail.network.response.GetCityListResponse
import com.mobile.view.productdetail.network.response.GetRegionsListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_seller_info, holder = SellerHolder::class)
class SellerItem(private var seller: Seller,
                 private var otherSellersCount: Int,
                 private var simpleSku: String,
                 private var pdvMainView: PDVMainView) {

    private var regions: ArrayList<Region>? = null
    private var cities: ArrayList<City>? = null
    private var selectedCityId: Int = -1
    private var selectedRegionId: Int = -1
    private var defaultRegionId: Int = -1
    private var defaultCityId: Int = -1

    private lateinit var holder: SellerHolder

    /***************************** Setup Seller Info ************************************/

    private fun showNoScoreWithBadge(holder: SellerHolder) {
        holder.noScoreLayout.visibility = View.VISIBLE
        holder.newBadge.visibility = View.VISIBLE

        holder.sellerScoreParent.visibility = View.GONE
        holder.scoreLayout.visibility = View.GONE
    }

    private fun showNoScoreWithoutBadge(holder: SellerHolder) {
        holder.noScoreLayout.visibility = View.VISIBLE

        holder.newBadge.visibility = View.GONE
        holder.sellerScoreParent.visibility = View.GONE
        holder.scoreLayout.visibility = View.GONE
    }

    private fun doNotShowSellerScore(holder: SellerHolder) {
        holder.sellerScoreParent.visibility = View.GONE
        holder.scoreLayout.visibility = View.GONE
    }

    private fun showSellerScore(holder: SellerHolder) {
        holder.noScoreLayout.visibility = View.GONE
        holder.newBadge.visibility = View.GONE

        holder.sellerScoreParent.visibility = View.VISIBLE
        holder.scoreLayout.visibility = View.VISIBLE

        setupSellerRate(holder)
        showSellerRateProgressRate(holder)
        showSellerRateTexts(holder, seller.score)
    }

    @Binder
    public fun binder(holder: SellerHolder) {
        seller.name?.let {
            holder.sellerName.text = it
        }

        this.holder = holder

        if (seller.score.overall == 0F) {
            if (seller.isNew) {
                showNoScoreWithBadge(holder)
            } else {
                showNoScoreWithoutBadge(holder)
            }
        } else if (seller.score.isEnabled) {
            showSellerScore(holder)
        } else {
            doNotShowSellerScore(holder)
        }

        setupSellerCollaborationPeriod(holder)
        setupWarranty(holder)
        setupOtherSellers(holder)

        triggerGetRegions()

        holder.sellerInfoHeader.setOnClickListener { onShowInfoClicked(holder) }
    }

    @SuppressLint("SetTextI18n")
    private fun setupOtherSellers(holder: SellerHolder) {
        if (otherSellersCount > 0) {
            holder.otherSellersCount.text = "$otherSellersCount+"
            holder.otherSellersCount.setBackgroundDrawable(createRoundDrawable("#25a8ed",
                    UIUtils.dpToPx(holder.itemView.context, 12f)))
        } else {
            holder.otherSellerLayout.visibility = View.GONE
            holder.otherSellersDivider.visibility = View.GONE
        }

        holder.otherSellerLayout.setOnClickListener { showOtherSellers() }
    }

    private fun showOtherSellers() {
        pdvMainView.onShowOtherSeller()
    }

    private fun setupSellerRate(holder: SellerHolder) {
        setUpSellerRateVisibility(holder)

        holder.sellerScoreMaxValue.text = holder.itemView.context.getString(R.string.of_number,
                seller.score.maxValue)

        holder.sellerScore.text = seller.score.overall.toString()
        holder.overallScore.text = seller.score.overall.toString()

        holder.sellerScore.setBackgroundDrawable(createRoundDrawable("#47b638",
                UIUtils.dpToPx(holder.itemView.context, 2f)))
    }

    private fun setUpSellerRateVisibility(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.VISIBLE
        holder.sellerScoreParent.visibility = View.VISIBLE
        holder.scoreLayout.visibility = View.GONE
        holder.noScoreLayout.visibility = View.GONE
    }

    private fun setUpNewSeller(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.GONE
        holder.percentageLayout.visibility = View.GONE
        holder.sellerScoreParent.visibility = View.GONE
        holder.scoreLayout.visibility = View.GONE

        holder.noScoreLayout.visibility = View.VISIBLE
    }

    private fun onShowInfoClicked(holder: SellerHolder) {
        if (holder.sellerInfoExpandableLayout.isExpanded) {
            collapseView(holder)
        } else {
            expandView(holder)
        }
    }

    private fun collapseView(holder: SellerHolder) {
        holder.sellerInfoExpandableLayout.collapse()
        animateExpandIcon(holder.showDetailImageView, true)
    }

    private fun expandView(holder: SellerHolder) {
        holder.sellerInfoExpandableLayout.expand()
        animateExpandIcon(holder.showDetailImageView, false)
    }

    @SuppressLint("SetTextI18n")
    private fun setupSellerCollaborationPeriod(holder: SellerHolder) {
        if (TextUtils.isEmpty(seller.presenceDuration.value)) {
            holder.percentageLayout.visibility = View.GONE
        } else {
            holder.percentageLayout.visibility = View.VISIBLE
            holder.collaborationLable.visibility = View.VISIBLE
            holder.collaborationPeriod.visibility = View.VISIBLE

            holder.collaborationLable.text = seller.presenceDuration.label + " : "
            holder.collaborationPeriod.text = seller.presenceDuration.value
        }

        if (TextUtils.isEmpty(seller.warranty)) {
            holder.warrantyLayout.visibility = View.GONE
        } else {
            holder.warrantyLayout.visibility = View.VISIBLE
            holder.warranty.text = seller.warranty
        }
    }

    private fun setupWarranty(holder: SellerHolder) {
        if (TextUtils.isEmpty(seller.warranty)) {
            holder.warrantyLayout.visibility = View.GONE
        } else {
            holder.warrantyLayout.visibility = View.VISIBLE
            holder.warranty.text = seller.warranty
        }
    }

    private fun animateExpandIcon(view: AppCompatImageView, reverse: Boolean) {
        val animationSet = AnimationSet(true)
        val rotateAnimation: RotateAnimation = if (!reverse) {
            RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        } else {
            RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        }

        rotateAnimation.duration = 250
        rotateAnimation.fillAfter = true
        rotateAnimation.isFillEnabled = true

        animationSet.addAnimation(rotateAnimation)

        view.startAnimation(rotateAnimation)
    }

    private fun showSellerRateProgressRate(holder: SellerHolder) {
        holder.salesWithoutReturnProgress.max = seller.score.maxValue * 10
        holder.successfulSupplyProgress.max = seller.score.maxValue * 10
        holder.sendOnTimeProgress.max = seller.score.maxValue * 10

        holder.salesWithoutReturnProgress.progress = (seller.score.notReturned * 10).toInt()
        holder.successfulSupplyProgress.progress = (seller.score.fullFillment * 10).toInt()
        holder.sendOnTimeProgress.progress = (seller.score.sLAReached * 10).toInt()
    }

    private fun showSellerRateTexts(holder: SellerHolder, id: Int, value: String) {
        (holder.itemView.findViewById(id) as TextView).text = value
    }

    private fun showSellerRateTexts(holder: SellerHolder, sellerScore: Score) {
        showFullfilment(holder, sellerScore)
        showNotReturned(holder, sellerScore)
        showSLAReach(holder, sellerScore)
        showOverall(holder, sellerScore)
    }

    private fun showOverall(holder: SellerHolder, sellerScore: Score) {
        val overallText: TextView = holder.itemView.findViewById(R.id.sellerScore_textView_score)
        overallText.setTextColor(Color.parseColor("#47b638"))
//        overallText.text = getScoreString(sellerScore.overall)
        showSellerRateTexts(holder, R.id.sellerScore_textView_maxValueOfScore,
                holder.itemView.context.getString(R.string.seller_info_rate_from_score, sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showSLAReach(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.sLAReached)

        showSellerRateTexts(holder, R.id.sellerScore_textView_sendOnTimeRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showNotReturned(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.notReturned)
        showSellerRateTexts(holder, R.id.sellerScore_textView_salesWithoutReturnRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showFullfilment(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.fullFillment)
        showSellerRateTexts(holder, R.id.sellerScore_textView_successfulSupplyRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
    }

    private fun getScoreString(value: Float): String {
        @SuppressLint("DefaultLocale")
        var result = String.format("%.1f", value)

        if (result.contains("٫")) {
            result = result.replace("٫", "/")
        }

        if (result.contains("/0") || result.contains("/۰")) {
            result = result.replace("/0", "")
            result = result.replace("/۰", "")
        }

        return result
    }

    private fun createRoundDrawable(colorHex: String, round: Int): Drawable? {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = round.toFloat()
        shape.setColor(Color.parseColor(colorHex))
        return shape
    }

    /***************************** End Of Setup Seller Info ************************************/

    private fun setRegions(regions: ArrayList<Region>?) {
        if (regions == null) {
            return
        }

        val regionsWithEmptyItem = ArrayList<Region>()
        regionsWithEmptyItem.add(Region(-1, holder.itemView.context.getString(R.string.select_region)))
        regionsWithEmptyItem.addAll(regions)
        val adapter = RegionCitySpinnerAdapter(holder.itemView.context, R.layout.spinner_content, regionsWithEmptyItem)
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item)
        holder.deliveryRegionSpinner.adapter = adapter
        holder.deliveryRegionSpinner.onItemSelectedListener = onAddressItemSelected
        selectDefaultRegion(regions)
    }

    private fun setCities(cities: ArrayList<City>?) {
        if (cities == null) {
            return
        }

        holder.deliveryCitySpinner.visibility = View.VISIBLE
        // Create adapter
        val citiesWithEmptyItem = ArrayList<City>()
        citiesWithEmptyItem.add(City(-1, holder.itemView.context.getString(R.string.select_city)))
        citiesWithEmptyItem.addAll(cities)
        val adapter = RegionCitySpinnerAdapter(holder.itemView.context, R.layout.spinner_content, citiesWithEmptyItem)
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item)
        holder.deliveryCitySpinner.adapter = adapter
        selectDefaultCity(cities)
        holder.deliveryCitySpinner.onItemSelectedListener = onAddressItemSelected
    }

    private fun selectDefaultCity(cities: ArrayList<City>?) {
        var selected = 0
        if (defaultCityId > 0) {
            selected = defaultCityId
        } else if (selectedCityId > 0) {
            selected = selectedCityId
        }

        for (i in cities!!.indices) {
            if (cities[i].value == selected) {
                holder.deliveryCitySpinner.setSelection(i + 1)
            }
        }
    }

    private fun selectDefaultRegion(regions: ArrayList<Region>?) {
        if (regions == null) {
            return
        }

        var selected = 0
        if (defaultRegionId > 0) {
            selected = defaultRegionId
        } else if (selectedRegionId > 0) {
            selected = selectedRegionId
        }
        for (i in regions.indices) {
            if (regions[i].value == selected) {
                holder.deliveryRegionSpinner.setSelection(i + 1)
            }
        }
    }

    private fun triggerGetRegions() {
        RetrofitHelper.makeWebApi(holder.itemView.context, RegionWebApi::class.java)
                .getRegionsList()
                .enqueue(object : Callback<ResponseWrapper<GetRegionsListResponse>> {
                    override fun onFailure(call: Call<ResponseWrapper<GetRegionsListResponse>>?, t: Throwable?) {
                        pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                                holder.itemView.context.getString(R.string.error_occured))
                    }

                    override fun onResponse(call: Call<ResponseWrapper<GetRegionsListResponse>>?, response: Response<ResponseWrapper<GetRegionsListResponse>>?) {
                        response?.body()?.metadata?.data?.let {
                            regions = it
                            setRegions(regions!!)
                            triggerGetDeliveryTime(selectedCityId)
                        }
                    }
                })
    }

    private fun triggerGetDeliveryTime(cityId: Int) {
        RetrofitHelper.makeWebApi(holder.itemView.context, RegionWebApi::class.java)
                .getDeliveryTime(cityId, simpleSku)
                .enqueue(object : Callback<ResponseWrapper<DeliveryTimeResponse>> {

                    override fun onFailure(call: Call<ResponseWrapper<DeliveryTimeResponse>>?,
                                           t: Throwable?) {
                        pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                                holder.itemView.context.getString(R.string.error_occured))
                    }

                    override fun onResponse(call: Call<ResponseWrapper<DeliveryTimeResponse>>?,
                                            response: Response<ResponseWrapper<DeliveryTimeResponse>>?) {
                        response?.body()?.metadata?.data?.let {
                            if (it.isEmpty()) {
                                return
                            }

                            val deliveryTime: DeliveryTimeData = it[0]

                            val strDeliveryTime = if (deliveryTime.tehranDeliveryTime.isEmpty()) {
                                deliveryTime.delivery_message
                            } else {
                                String.format(Locale("fa"),
                                        "%s %s\n%s %s",
                                        holder.itemView.context.getString(R.string.tehran_delivery_time),
                                        deliveryTime.tehranDeliveryTime,
                                        holder.itemView.context.getString(R.string.other_cities_delivery_time),
                                        deliveryTime.otherCitiesDeliveryTime)
                            }

                            holder.deliveryTimeAndCity.text = strDeliveryTime
                            holder.deliveryTimeAndCity.visibility = View.VISIBLE
                            selectDefaultRegion(regions)
                        }
                    }
                })
    }

    private fun triggerGetCities(region: Int) {
        RetrofitHelper.makeWebApi(holder.itemView.context, RegionWebApi::class.java)
                .getCitiesList(region).enqueue(object : Callback<ResponseWrapper<GetCityListResponse>> {
                    override fun onFailure(call: Call<ResponseWrapper<GetCityListResponse>>?, t: Throwable?) {
                        pdvMainView.showErrorMessage(WarningFactory.ERROR_MESSAGE,
                                holder.itemView.context.getString(R.string.error_occured))
                    }

                    override fun onResponse(call: Call<ResponseWrapper<GetCityListResponse>>?,
                                            response: Response<ResponseWrapper<GetCityListResponse>>?) {
                        response?.body()?.metadata?.data?.let {
                            cities = it
                            setCities(cities)
                        }
                    }
                })
    }

    private var onAddressItemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val `object` = parent.getItemAtPosition(position)
            if (`object` is Region) {
                // Request the cities for this region id
                val region = `object`.value
                // Get cities
                if (region != -1) {
                    triggerGetCities(region)
                }
            } else if (`object` is City) {
                // Case list
                val city = `object`.value
                if (city != -1) {
                    selectedRegionId = (holder.deliveryRegionSpinner.selectedItem as Region).value
                    holder.deliveryTimeAndCity.text = holder.itemView.context.getString(R.string.getting_data_from_server)
                    selectedCityId = city
                    triggerGetDeliveryTime(city)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
    }
}
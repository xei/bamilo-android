@file:Suppress("DEPRECATION")

package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import android.widget.TextView;
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.framework.service.utils.TextUtils
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils.createRoundDrawable
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.PDVMainView
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Score
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Seller
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.RegionWebApi
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.City
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.Region
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.DeliveryTimeData
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.DeliveryTimeResponse
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.GetCityListResponse
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.GetRegionsListResponse
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeDigitsInString
import com.bamilo.android.appmodule.modernbamilo.util.getMorphNumberString
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper

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
        if (holder.isFilled) {
            return
        }

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

        holder.itemView
                .findViewById<LinearLayout>(R.id.pdvSellerInfo_linearLayout_nameLayout)
                .setOnClickListener { gotoSellerPage(holder.itemView.context) }

        holder.isFilled = true
    }

    @SuppressLint("SetTextI18n")
    private fun setupOtherSellers(holder: SellerHolder) {
        if (otherSellersCount > 0) {
            holder.otherSellersCount.text = "$otherSellersCount+".persianizeDigitsInString()
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
                seller.score.maxValue).persianizeDigitsInString()

        holder.sellerScore.text = seller.score.overall.toString().persianizeDigitsInString()
        holder.overallScore.text = getMorphNumberString(seller.score.overall).persianizeDigitsInString()

        holder.sellerScore.setBackgroundDrawable(createRoundDrawable("#47b638",
                UIUtils.dpToPx(holder.itemView.context, 2f)))
    }

    private fun setUpSellerRateVisibility(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.VISIBLE
        holder.sellerScoreParent.visibility = View.VISIBLE
        holder.scoreLayout.visibility = View.GONE
        holder.noScoreLayout.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun setupSellerCollaborationPeriod(holder: SellerHolder) {
        if (TextUtils.isEmpty(seller.presenceDuration.value)) {
            holder.percentageLayout.visibility = View.GONE
        } else {
            holder.percentageLayout.visibility = View.VISIBLE
            holder.collaborationLabel.visibility = View.VISIBLE
            holder.collaborationPeriod.visibility = View.VISIBLE

            holder.collaborationLabel.text = seller.presenceDuration.label + " : ".persianizeDigitsInString()
            holder.collaborationPeriod.text = seller.presenceDuration.value?.persianizeDigitsInString()
        }

        if (TextUtils.isEmpty(seller.warranty)) {
            holder.warrantyLayout.visibility = View.GONE
        } else {
            holder.warrantyLayout.visibility = View.VISIBLE
            holder.warranty.text = seller.warranty?.persianizeDigitsInString()
        }
    }

    private fun setupWarranty(holder: SellerHolder) {
        if (TextUtils.isEmpty(seller.warranty)) {
            holder.warrantyLayout.visibility = View.GONE
        } else {
            holder.warrantyLayout.visibility = View.VISIBLE
            holder.warranty.text = seller.warranty?.persianizeDigitsInString()
        }
    }

    private fun showSellerRateProgressRate(holder: SellerHolder) {
        holder.salesWithoutReturnProgress.max = seller.score.maxValue * 10
        holder.successfulSupplyProgress.max = seller.score.maxValue * 10
        holder.sendOnTimeProgress.max = seller.score.maxValue * 10

        holder.salesWithoutReturnProgress.progress = (seller.score.notReturned * 10).toInt()
        holder.successfulSupplyProgress.progress = (seller.score.fullfilment * 10).toInt()
        holder.sendOnTimeProgress.progress = (seller.score.SLAReached * 10).toInt()
    }

    private fun showSellerRateTexts(holder: SellerHolder, id: Int, value: String) {
        (holder.itemView.findViewById(id) as TextView).text = value.persianizeDigitsInString()
    }

    private fun showSellerRateTexts(holder: SellerHolder, sellerScore: Score) {
        showFullfilment(holder, sellerScore)
        showNotReturned(holder, sellerScore)
        showSLAReach(holder, sellerScore)
        showOverall(holder, sellerScore)
    }

    private fun showOverall(holder: SellerHolder, sellerScore: Score) {
        showSellerRateTexts(holder, R.id.sellerScore_textView_maxValueOfScore,
                holder.itemView.context.getString(R.string.seller_info_rate_from_score,
                        getMorphNumberString(sellerScore.maxValue.toFloat()).toInt()))
    }

    @SuppressLint("StringFormatMatches")
    private fun showSLAReach(holder: SellerHolder, sellerScore: Score) {
        val result = getMorphNumberString(sellerScore.SLAReached)

        showSellerRateTexts(holder, R.id.sellerScore_textView_sendOnTimeRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showNotReturned(holder: SellerHolder, sellerScore: Score) {
        val result = getMorphNumberString(sellerScore.notReturned)
        showSellerRateTexts(holder, R.id.sellerScore_textView_salesWithoutReturnRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showFullfilment(holder: SellerHolder, sellerScore: Score) {
        val result = getMorphNumberString(sellerScore.fullfilment)
        showSellerRateTexts(holder, R.id.sellerScore_textView_successfulSupplyRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))
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
                            if (selectedCityId < 1) {
                                triggerGetDeliveryTime(null)
                            } else {
                                triggerGetDeliveryTime(selectedCityId)
                            }

                        }
                    }
                })
    }

    private fun triggerGetDeliveryTime(cityId: Int?) {
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
                    if (city < 1) {
                        triggerGetDeliveryTime(null)
                    } else {
                        triggerGetDeliveryTime(city)
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
    }

    private fun gotoSellerPage(context: Context) {
        val intent = Intent(context, MainFragmentActivity::class.java).apply {
            putExtra("seller_target", seller.target)
        }
        context.startActivity(intent)
    }
}
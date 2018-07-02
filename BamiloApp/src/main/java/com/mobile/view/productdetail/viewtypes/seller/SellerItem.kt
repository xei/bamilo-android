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
import com.mobile.app.BamiloApplication
import com.mobile.components.customfontviews.TextView
import com.mobile.components.ghostadapter.BindItem
import com.mobile.components.ghostadapter.Binder
import com.mobile.datamanagement.DataManager
import com.mobile.helpers.address.GetCitiesHelper
import com.mobile.helpers.address.GetRegionsHelper
import com.mobile.helpers.products.GetDeliveryTimeHelper
import com.mobile.interfaces.IResponseCallback
import com.mobile.service.objects.addresses.AddressCities
import com.mobile.service.objects.addresses.AddressCity
import com.mobile.service.objects.addresses.AddressRegion
import com.mobile.service.objects.addresses.AddressRegions
import com.mobile.service.objects.product.DeliveryTimeCollection
import com.mobile.service.pojo.BaseResponse
import com.mobile.service.utils.ApiConstants
import com.mobile.service.utils.CollectionUtils
import com.mobile.service.utils.EventType
import com.mobile.utils.ui.UIUtils
import com.mobile.view.R
import com.mobile.view.productdetail.model.Score
import com.mobile.view.productdetail.model.Seller
import java.util.*

/**
 * Created by Farshid
 * since 6/23/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_seller_info, holder = SellerHolder::class)
class SellerItem(private var seller: Seller,
                 private var otherSellersCount: Int,
                 private var sku: String) : IResponseCallback {

    private var regions: AddressRegions? = null
    private var cities: AddressCities? = null
    private var selectedCityId: Int = -1
    private var selectedRegionId: Int = -1
    private var defaultRegionId: Int = -1
    private var defaultCityId: Int = -1

    private lateinit var holder: SellerHolder

    /***************************** Setup Seller Info ************************************/

    /**
     * onBindItem
     * */
    @Binder
    public fun binder(holder: SellerHolder) {
        sku = "SA277CD0KHKEYALIYUN"
        holder.sellerName.text = seller.name
        this.holder = holder

        when {
            seller.isNew -> {
                setUpNewSeller(holder)
            }
            seller.score.isEnabled -> {
                setupSellerRate(holder)
                showSellerRateProgressRate(holder)
                showSellerRateTexts(holder, seller.score)
            }
            else -> {
                goneAllSellerRateViews(holder)
            }
        }

        showSellerCollaborationPeriod(holder)
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
    }

    private fun goneAllSellerRateViews(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.GONE
        holder.collaborationPeriodLabel.visibility = View.GONE
        holder.sellerScoreParent.visibility = View.GONE
        holder.scoreLayout.visibility = View.GONE
        holder.noScoreLayout.visibility = View.GONE
    }

    private fun setupSellerRate(holder: SellerHolder) {
        setUpSellerRateVisibility(holder)

        holder.sellerScoreMaxValue.text = holder.itemView.context.getString(R.string.of_number,
                seller.score.maxValue)

        holder.sellerScore.text = seller.score.overall.value.toString()

        holder.sellerScore.setBackgroundDrawable(createRoundDrawable(seller.score.overall.color,
                UIUtils.dpToPx(holder.itemView.context, 2f)))
    }

    private fun setUpSellerRateVisibility(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.VISIBLE
        holder.collaborationPeriodLabel.visibility = View.VISIBLE
        holder.sellerScoreParent.visibility = View.VISIBLE
        holder.scoreLayout.visibility = View.GONE
        holder.noScoreLayout.visibility = View.GONE
    }

    private fun setUpNewSeller(holder: SellerHolder) {
        holder.collaborationPeriod.visibility = View.GONE
        holder.collaborationPeriodLabel.visibility = View.GONE
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

    private fun showSellerCollaborationPeriod(holder: SellerHolder) {
        holder.collaborationPeriod.text = seller.presenceDuration.value
        holder.collaborationPeriodLabel.text = seller.presenceDuration.label
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

        holder.salesWithoutReturnProgress.progress = (seller.score.notReturned.value * 10).toInt()
        holder.successfulSupplyProgress.progress = (seller.score.fullFillment.value * 10).toInt()
        holder.sendOnTimeProgress.progress = (seller.score.sLAReached.value * 10).toInt()

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
        overallText.setTextColor(Color.parseColor(sellerScore.overall.color))
        overallText.text = getScoreString(sellerScore.overall.value)
        showSellerRateTexts(holder, R.id.sellerScore_textView_maxValueOfScore,
                holder.itemView.context.getString(R.string.seller_info_rate_from_score, sellerScore.maxValue))
    }

    @SuppressLint("StringFormatMatches")
    private fun showSLAReach(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.sLAReached.value)

        showSellerRateTexts(holder, R.id.sellerScore_textView_sendOnTimeRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))

        showSellerRateTexts(holder, R.id.sellerScore_textView_sendOnTimeText,
                sellerScore.sLAReached.label)
    }

    @SuppressLint("StringFormatMatches")
    private fun showNotReturned(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.notReturned.value)
        showSellerRateTexts(holder, R.id.sellerScore_textView_salesWithoutReturnRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))

        showSellerRateTexts(holder, R.id.sellerScore_textView_salesWithoutReturnText,
                sellerScore.notReturned.label)
    }

    @SuppressLint("StringFormatMatches")
    private fun showFullfilment(holder: SellerHolder, sellerScore: Score) {
        val result = getScoreString(sellerScore.fullFillment.value)
        showSellerRateTexts(holder, R.id.sellerScore_textView_successfulSupplyRate,
                holder.itemView.context.getString(R.string.seller_info_rate_from,
                        result,
                        sellerScore.maxValue))

        showSellerRateTexts(holder, R.id.sellerScore_textView_successfulSupplyText,
                sellerScore.fullFillment.label)
    }

    private fun getScoreString(value: Double): String {
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

    private fun setRegions(regions: ArrayList<AddressRegion>?) {
        if (regions == null) {
            return
        }

        val regionsWithEmptyItem = ArrayList<AddressRegion>()
        regionsWithEmptyItem.add(AddressRegion(-1, holder.itemView.context.getString(R.string.select_region)))
        regionsWithEmptyItem.addAll(regions)
        val adapter = RegionCitySpinnerAdapter(holder.itemView.context, R.layout.form_spinner_item, regionsWithEmptyItem)
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item)
        holder.deliveryRegionSpinner.adapter = adapter
        holder.deliveryRegionSpinner.onItemSelectedListener = onAddressItemSelected
        selectDefaultRegion(regions)
    }

    private fun setCities(cities: ArrayList<AddressCity>?) {
        if (cities == null) {
            return
        }

        holder.deliveryCitySpinner.visibility = View.VISIBLE
        // Create adapter
        val citiesWithEmptyItem = java.util.ArrayList<AddressCity>()
        citiesWithEmptyItem.add(AddressCity(-1, holder.itemView.context.getString(R.string.select_city)))
        citiesWithEmptyItem.addAll(cities)
        val adapter = RegionCitySpinnerAdapter(holder.itemView.context, R.layout.form_spinner_item, citiesWithEmptyItem)
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item)
        holder.deliveryCitySpinner.adapter = adapter
        selectDefaultCity(cities)
        holder.deliveryCitySpinner.onItemSelectedListener = onAddressItemSelected
    }

    private fun selectDefaultCity(cities: List<AddressCity>) {
        var selected = 0
        if (defaultCityId > 0) {
            selected = defaultCityId
        } else if (selectedCityId > 0) {
            selected = selectedCityId
        }

        for (i in cities.indices) {
            if (cities[i].value == selected) {
                holder.deliveryCitySpinner.setSelection(i + 1)
            }
        }
    }

    private fun selectDefaultRegion(regions: List<AddressRegion>?) {
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
        DataManager.getInstance().loadData(GetRegionsHelper(),
                GetRegionsHelper.createBundle(ApiConstants.GET_REGIONS_API_PATH),
                this)
    }

    private fun triggerGetDeliveryTime(cityId: Int) {
        BamiloApplication.INSTANCE.sendRequest(GetDeliveryTimeHelper(),
                GetDeliveryTimeHelper.createBundle(sku, cityId), this)
    }

    private fun triggerGetCities(apiCall: String, region: Int) {
        DataManager.getInstance().loadData(GetCitiesHelper(), GetCitiesHelper.createBundle(apiCall, region, null), this)
    }

    override fun onRequestComplete(baseResponse: BaseResponse<*>?) {
        val eventType = baseResponse?.eventType ?: return

        if (eventType == EventType.GET_REGIONS_EVENT) {
            regions = baseResponse.contentData as AddressRegions
            if (CollectionUtils.isNotEmpty(regions)) {
                setRegions(regions!!)
                triggerGetDeliveryTime(selectedCityId)
            }
        } else if (eventType == EventType.GET_CITIES_EVENT) {
            cities = baseResponse.contentData as GetCitiesHelper.AddressCitiesStruct
            if (CollectionUtils.isNotEmpty(cities)) {
                setCities(cities)
            }
        } else if (eventType == EventType.GET_DELIVERY_TIME) {
            val deliveryTimeCollection = baseResponse.contentData as DeliveryTimeCollection

            if (deliveryTimeCollection.deliveryTimes != null) {
                return
            }

            if (deliveryTimeCollection.deliveryTimes.size > 0) {
                return
            }

            val deliveryTime = deliveryTimeCollection.deliveryTimes[0]
            val strDeliveryTime: String

            strDeliveryTime = if (deliveryTime.tehranDeliveryTime.isEmpty()) {
                deliveryTime.deliveryMessage
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
            defaultRegionId = deliveryTimeCollection.regionId
            defaultCityId = deliveryTimeCollection.cityId
            selectDefaultRegion(regions)
        }
    }

    override fun onRequestError(baseResponse: BaseResponse<*>?) {

    }

    private var onAddressItemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val `object` = parent.getItemAtPosition(position)
            if (`object` is AddressRegion) {
                // Request the cities for this region id
                val region = `object`.value
                // Get cities
                if (region != -1) {
                    triggerGetCities(ApiConstants.GET_CITIES_API_PATH, region)
                }

            } else if (`object` is AddressCity) {
                // Case list
                val city = `object`.value
                if (city != -1) {
                    selectedRegionId = (holder.deliveryRegionSpinner.selectedItem as AddressRegion).value
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
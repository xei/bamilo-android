package com.bamilo.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import com.bamilo.modernbamilo.userreview.ReviewPageType


const val ARG_PARAM_FRAGMENT_INDEX = "ARG_PARAM_FRAGMENT_INDEX"
const val ARG_PARAM_PRODUCT_IMAGE_URL = "ARG_PARAM_PRODUCT_IMAGE_URL"

open class ReviewPageBaseFragment : Fragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param pageType Parameter 1.
         * @return A new instance of fragment BlankFragment.
         */
        @JvmStatic
        fun newInstance(pageType: ReviewPageType,fragmentIndex: Int, productImageUrl: String?) =

                when (pageType) {

                    ReviewPageType.RADIO -> ReviewPageTypeRadioFragment.newInstance(fragmentIndex, productImageUrl)

                    ReviewPageType.CHECKBOX -> ReviewPageTypeCheckboxFragment.newInstance(fragmentIndex, productImageUrl)

                    ReviewPageType.TEXTBOX, ReviewPageType.ESSAY  -> ReviewPageTypeEssayFragment.newInstance(fragmentIndex, productImageUrl)

                    ReviewPageType.IMAGE_SELECT -> ReviewPageTypeImageSelectFragment.newInstance(fragmentIndex, productImageUrl)
                    ReviewPageType.NPS -> ReviewPageTypeNpsFragment.newInstance(fragmentIndex, productImageUrl)

                    ReviewPageType.THANKS -> ReviewPageTypeThanksFragment.newInstance()

                    ReviewPageType.HIDDEN -> TODO()

                }

    }

}

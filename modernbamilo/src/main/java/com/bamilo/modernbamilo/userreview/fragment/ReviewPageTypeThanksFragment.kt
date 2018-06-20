package com.bamilo.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bamilo.modernbamilo.R


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageTypeRadioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReviewPageTypeThanksFragment : ReviewPageBaseFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ReviewPageTypeThanksFragment.
         */
        @JvmStatic
        fun newInstance() = ReviewPageTypeThanksFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_review_page_type_thanks, container, false)!!

}

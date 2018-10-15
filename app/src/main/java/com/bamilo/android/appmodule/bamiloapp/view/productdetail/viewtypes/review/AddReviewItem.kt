package com.bamilo.android.appmodule.bamiloapp.view.productdetail.viewtypes.review

import android.view.View
import com.bamilo.android.appmodule.modernbamilo.product.comment.submit.startSubmitRateActivity
import com.bamilo.android.framework.components.ghostadapter.BindItem
import com.bamilo.android.framework.components.ghostadapter.Binder
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 7/25/2018.
 * contact farshidabazari@gmail.com
 */
@BindItem(layout = R.layout.content_pdv_add_review, holder = AddReviewHolder::class)
class AddReviewItem(var sku: String) {
    @Binder
    public fun binder(holder: AddReviewHolder) {
        holder.itemView.run {
            holder.itemView.findViewById<View>(R.id.pdvAddReview_constraintLayout_layout)
                    .setOnClickListener {
                        startSubmitRateActivity(context, sku)
                    }
        }
    }
}
package com.mobile.view.productdetail.model

import java.io.Serializable

class Score : Serializable {
    var overall: Float = 0.0f
    var SLAReached: Float = 0.0f
    var notReturned: Float = 0.0f
    var fullfilment: Float = 0.0f

    var maxValue: Int = 0
    var isEnabled: Boolean = false
}
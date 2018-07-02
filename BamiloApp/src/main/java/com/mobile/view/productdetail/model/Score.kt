package com.mobile.view.productdetail.model

class Score {
    var overall = Overall()
    var sLAReached = SLAReached()
    var notReturned = NotReturned()
    var fullFillment = FullFillment()

    var maxValue: Int = 0
    var isEnabled: Boolean = false
}
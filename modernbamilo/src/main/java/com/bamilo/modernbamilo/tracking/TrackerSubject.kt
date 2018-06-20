package com.bamilo.modernbamilo.tracking

/**
 * This object is an observable singleton that you can call it's methods to notify
 * all the analytics platforms automatically.
 */
object TrackerSubject : EventObservation {

    private val mTrackingObservers = ArrayList<EventObservation>()

    init {
        mTrackingObservers.add(FirebaseEventObserver())
    }


    override fun event1() {
        for (observer in mTrackingObservers) {
            observer.event1()
        }
    }

    override fun event2() {
        for (observer in mTrackingObservers) {
            observer.event2()
        }
    }

}
package foundation.observable

import foundation.observable.Observable

fun interface DetachObserverAware {
    fun detachedFrom(observable: Observable<*>)
}
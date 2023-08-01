package foundation.observable

fun interface DetachObserverAware {
    fun detachedFrom(observable: Observable<*>)
}
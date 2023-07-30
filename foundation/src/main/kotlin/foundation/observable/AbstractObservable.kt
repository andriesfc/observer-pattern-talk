package foundation.observable

import foundation.utils.eachRemaining

abstract class AbstractObservable<T : Observable<T>> protected constructor(
    private val observers: MutableCollection<Any>
) : Observable<T> {
    constructor() : this(LinkedHashSet())

    override fun attach(observer: Observer<T>) {
        if (observer !in observers)
            observers.add(observer)
    }

    override fun detach(observer: Observer<T>) {
        if (observers.remove(observer))
            (observer as? DetachAwareObservation)?.observerDetached()
    }

    override fun observes(observer: Observer<T>): Boolean = observer in observers

    override fun detachAll() {
        observers.iterator().eachRemaining {
            val detachAware = next() as? DetachAwareObservation
            remove()
            detachAware?.observerDetached()
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun notifyObservers() {
        observers.forEach {
            it as Observer<Observable<T>>
            it.subjectChanged(this)
        }
    }

    override fun isObserved(): Boolean = observers.isNotEmpty()
}
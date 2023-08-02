package foundation.observable

import foundation.observable.DetachManagementStrategy.Handled
import foundation.observable.DetachManagementStrategy.Handled.PostDetachState
import foundation.utils.eachRemaining

/**
 * Abstract observable
 *
 * Use this a base class to implement observable subjects.  This base class provided the following conveniences:
 *
 * 1. It handles detach, and attach logic.
 * 2. It exposes an overloaded constructor in cases where the implementor does not want to use the default collection of observes (by default this is a set)
 * 3. It will correctly call back through any observer which implements either the [DetachObservation] contract, or the [DetachObserverAware].
 * 4. It exposes an internal mechanism an implementation, (via the [detachManagementStrategy]), may opt in to handle problems or arising from observer detaching call backs.
 * 5. It provides hooks into the dispatching action insofar as error handling goes (see [notificationFailureStrategy])
 *
 * The aim of these mechanisms is to allow an implementation to handle state better, without leaking such mechanisms
 * to callers.
 *
 * @param T Type parameter for a [MutableCollection]
 * @property observers A mutable collection to keep track of observers.
 * @constructor Create an  observable base instance on which observables can be implemented.
 */
abstract class AbstractObservable<T : Observable<T>> protected constructor(
    private val observers: MutableCollection<Any>,
) : Observable<T> {

    /**
     * Constructs a base class which uses a [LinkedHashSet] to keep track observers.
     */
    constructor() : this(LinkedHashSet())

    /**
     * Detach management strategy.
     *
     * By returning any instance of a [DetachManagementStrategy.Handled], this observable may choose to handle any
     * exceptions arising from observers implementing either the [DetachObservation], or the [DetachObserverAware]
     * contracts.
     *
     * **NOTE**: By default the detach management strategy used is [DetachManagementStrategy.Default]. This means
     * any observer raising an exception during the aforementioned functions wll be immediately raised during calls
     * made to either the [detach], or the [detachAll] calls.
     *
     */
    protected open val detachManagementStrategy: DetachManagementStrategy = DetachManagementStrategy.Default

    protected open val notificationFailureStrategy: ObserverNotificationFailureAction = ObserverNotificationFailureAction.Default
    override fun countObservers(): Int = observers.size
    override fun attach(observer: Observer<T>): Boolean {
        return if (observer !in observers)
            observers.add(observer)
        else false
    }

    override fun detach(observer: Observer<T>): Boolean {
        return if (observers.remove(observer)) {
            handleDetachment(observer, Handled.DetachTriggeredFrom.Detach)
            true
        } else false
    }

    override fun isObservedBy(observer: Observer<T>): Boolean = observer in observers

    override fun detachAll() {
        observers.iterator().eachRemaining {
            val observer = next()
            remove()
            handleDetachment(observer, Handled.DetachTriggeredFrom.DetachAll)
        }
    }

    private fun handleDetachment(observer: Any, context: Handled.DetachTriggeredFrom) {

        val detachState = when (observer) {
            is DetachObservation -> runCatching { observer.detached() }
            is DetachObserverAware -> runCatching { observer.detachedFrom(this@AbstractObservable) }
            else -> runCatching { Unit }
        }.fold(
            onSuccess = { PostDetachState.Ok },
            onFailure = { x -> PostDetachState.Panic(x) }
        )

        when (val d = detachManagementStrategy) {
            is Handled -> d.postDetachment(context, this, detachState)
            DetachManagementStrategy.Default -> (detachState as? PostDetachState.Panic)?.panic()
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun notifyObservers(notificationFailureStrategy: ObserverNotificationFailureAction) {
        observers.forEachIndexed { i, observer ->
            observer as Observer<Observable<T>>
            manageNotifyObserver(observer, i, notificationFailureStrategy)
        }
    }

    protected fun notifyObservers() = notifyObservers(notificationFailureStrategy)

    private fun manageNotifyObserver(
        observer: Observer<Observable<T>>,
        index: Int,
        failureAction: ObserverNotificationFailureAction,
    ) {
        val ex = runCatching { observer.subjectChanged(this) }.exceptionOrNull()?.let { ex ->
            when (failureAction) {
                ObserverNotificationFailureAction.Default -> ex
                is ObserverNotificationFailureAction.Handled -> failureAction.notifyObserverFailed(
                    observer,
                    index,
                    ex
                )
            }
        } ?: return
        throw ex
    }


    override fun isObserved(): Boolean = observers.isNotEmpty()
}

private fun PostDetachState.Panic.panic(): Nothing = throw cause

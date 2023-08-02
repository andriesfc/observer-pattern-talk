package foundation.observable

/**
 * Detach management strategy in the need of an [Observable] want to have stricter control, or
 * internal observation when am expected detached notification fail when an [Observer] either implements
 * the [DetachObservation], or the [DetachObserverAware] interfaces.
 *
 * @see [AbstractObservable.detachManagementStrategy]
 * @see [DetachManagementStrategy.Handled]
 *
 */
sealed interface DetachManagementStrategy {
    data object Default : DetachManagementStrategy

    /**
     * Implement this interface and make sure the [AbstractObservable.detachManagementStrategy] returns
     * an instance of it.
     */
    fun interface Handled : DetachManagementStrategy {

        /**
         * Caller context indicates which the detach operation was done through the [Observable.detach], or
         * [Observable.detachAll].
         *
         * @constructor Create empty Caller context
         */
        enum class DetachTriggeredFrom {
            /**
             * Single Caller detached via the [Observable.detach] call
             *
             */
            Detach,

            /**
             * Bulk, indicating the caller detached via the [Observable.detachAll]
             */
            DetachAll
        }

        /**
         * Post detach state
         *
         * @constructor Create empty Post detach state
         */
        sealed class PostDetachState {

            /**
             * Ok - All good.
             */
            data object Ok : PostDetachState()

            /**
             * Panic - something bad/unexpected happened.
             *
             * @property cause What caused it.
             */
            data class Panic(val cause: Throwable) : PostDetachState()
        }

        /**
         * Post detachment state to
         *
         * @param observable The observable which request management of detach callback results.
         * @param state The state after the callback
         * @param callerContext The context of the detachment call.
         */
        fun postDetachment(callerContext: DetachTriggeredFrom, observable: Observable<Observable<*>>, state: PostDetachState)
    }
}
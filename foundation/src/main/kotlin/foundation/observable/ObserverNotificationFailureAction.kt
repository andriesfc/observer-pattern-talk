package foundation.observable

import foundation.observable.ObserverNotificationFailureAction.BuiltIn.*
import java.io.PrintStream
import java.util.Objects.hash as objectHash

sealed interface ObserverNotificationFailureAction {

    data object Default : ObserverNotificationFailureAction

    /**
     * Handles the case where the observer failed while acting on the [Observer.subjectChanged] notification.
     *
     * The caller may choose to:
     *
     *  1. Ignore the exception thrown by returning a `null`
     *  2. Raise the exception by returning it back to the observer.
     *  3. Translate the exception by returning a different exception to be thrown.
     *
     * > **Why use it:**  An observable typically is responsible to call multiple observers. And may just choose to
     * > either ignore such errors (or some of them), or delay the raising of such errors until all other observers
     * > have been notified.
     */
    fun interface Handled : ObserverNotificationFailureAction {
        fun notifyObserverFailed(observer: Observer<*>, index: Int, failure: Throwable): Throwable?
    }

    /**
     * Some simple builtin handlers.
     *
     * These handlers deal with some common use cases related to debugging:
     *
     * 1. Print the error to standard output ([PrintToStdOut]).
     * 2. Print the error to standard error ([PrintToStdErr]).
     * 3. A proper log adapter ([AdaptToLogger]).
     *
     * > **NOTE:** Most of these handlers (if not all of them), must be configured once of via the
     * > constructor before they can be used.
     */
    sealed class BuiltIn : Handled {

        companion object {
            private val ALWAYS_IGNORE_ERROR = { _: Observer<*>, _: Throwable -> null }
        }

        class PrintToStdOut @JvmOverloads constructor(
            private val alsoHandleErrorWith: (Observer<*>, Throwable) -> Throwable? = ALWAYS_IGNORE_ERROR,
            private val print: PrintStream.(Observer<*>, Throwable) -> Unit,
        ) : BuiltIn() {
            override fun notifyObserverFailed(observer: Observer<*>, index: Int, failure: Throwable): Throwable? {
                print(System.out, observer, failure)
                return alsoHandleErrorWith(observer, failure)
            }

            override fun equals(other: Any?): Boolean {
                return when {
                    other === this -> true
                    other !is PrintToStdOut -> false
                    else -> alsoHandleErrorWith == other.alsoHandleErrorWith && print == other.print && alsoHandleErrorWith == other.alsoHandleErrorWith
                }
            }

            override fun hashCode(): Int = objectHash(print, alsoHandleErrorWith)

        }

        class PrintToStdErr @JvmOverloads constructor(
            private val alsoHandleErrorWith: (Observer<*>, Throwable) -> Throwable? = ALWAYS_IGNORE_ERROR,
            private val print: PrintStream.(Observer<*>, Throwable) -> Unit,
        ) : BuiltIn() {
            override fun notifyObserverFailed(observer: Observer<*>, index: Int, failure: Throwable): Throwable? {
                print(System.err, observer, failure)
                return alsoHandleErrorWith(observer, failure)
            }

            override fun hashCode(): Int = objectHash(alsoHandleErrorWith, print)

            override fun equals(other: Any?): Boolean {
                return when {
                    other === this -> true
                    other !is PrintToStdErr -> false
                    else -> alsoHandleErrorWith == other.alsoHandleErrorWith && print == other.print
                }
            }
        }

        class AdaptToLogger(
            private val sender: Observable<*>,
            private val adapter: LogAdapter,
        ) : BuiltIn() {
            fun interface LogAdapter {

                enum class AfterLogging {
                    RaiseAgain,
                    Ignore
                }

                fun sendToLog(
                    sender: Observable<*>,
                    failedObserver: Observer<*>,
                    failedCalledSeq: Int,
                    failure: Throwable,
                ): AfterLogging
            }

            override fun notifyObserverFailed(observer: Observer<*>, index: Int, failure: Throwable): Throwable? {
                return when (adapter.sendToLog(sender, observer, index, failure)) {
                    LogAdapter.AfterLogging.RaiseAgain -> failure
                    LogAdapter.AfterLogging.Ignore -> null
                }
            }

            override fun equals(other: Any?): Boolean {
                return when {
                    other === this -> true
                    other !is AdaptToLogger -> false
                    else -> sender == other.sender && adapter == other.adapter
                }
            }

            override fun hashCode(): Int = objectHash(sender, adapter)
        }
    }
}
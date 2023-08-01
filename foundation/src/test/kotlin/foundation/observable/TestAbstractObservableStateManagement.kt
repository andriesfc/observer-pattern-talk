package foundation.observable

import foundation.observable.DetachManagementStrategy.Managed
import foundation.testing.debug
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.assertions.throwables.shouldThrowAnyUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*


private typealias AnyObservable = Observer<Observable<Observable<*>>>

class TestAbstractObservableStateManagement : ShouldSpec({

    val iAmBadError = "iAmBadOne"

    val faultyObserver: AnyObservable = mockk("faultyObserver", false, DetachObservation::class) {
        this as DetachObservation
        every { subjectChanged(any()) } just runs
        every { detached() } throws Exception(iAmBadError)
    }

    context("An observer which throws an exception when it gets notified with detached.") {

        context("An subject which does not handle exception thrown") {
            val naiveObservable = object : AbstractObservable<Observable<*>>() {}.apply { attach(faultyObserver) }
            should("be attached") {
                naiveObservable.isObservedBy(faultyObserver).shouldBeTrue()
            }
            should("fail with an exception when the observer gets detached") {
                val thrown = shouldThrowAnyUnit { naiveObservable.detach(faultyObserver) }.debug()
                thrown.shouldNotBeNull().shouldHaveMessage(iAmBadError)
                verify(atMost = 1) { (faultyObserver as DetachObservation).detached() }
            }
            should("have been detached regardless") {
                (faultyObserver isObserving naiveObservable).shouldBeFalse()
            }
        }

        context("An subject which managed the exception thrown") {
            val safer = object : AbstractObservable<Observable<*>>() {
                init {
                    attach(faultyObserver)
                }


                fun detachCallbackFailed(c: Managed.DetachTriggeredFrom, o: Observable<*>, s: Managed.PostDetachState) {
                    lastErrorContext = c.debug("context")
                    lastErrorState = s.debug("state")
                    o.debug("observable")
                }

                var lastErrorState: Managed.PostDetachState? = null; private set
                var lastErrorContext: Managed.DetachTriggeredFrom? = null; private set

                fun discardLastErrorState() {
                    lastErrorState = null
                    lastErrorContext = null
                }

                override val detachManagementStrategy: DetachManagementStrategy = Managed { a, b, c ->
                    detachCallbackFailed(a, b, c)
                }
            }

            should("be attached") {
                (faultyObserver isObserving safer).shouldBeTrue()
            }

            should("does not fail with an exception when it tries to detach the observer") {
                shouldNotThrowAnyUnit { safer.detach(faultyObserver) }
                safer.lastErrorState.debug("lastErrorState")
                    .shouldBeInstanceOf<Managed.PostDetachState.Panic>()
                    .cause.shouldHaveMessage(iAmBadError)
                safer.lastErrorContext.debug("lastErrorContext").shouldBe(Managed.DetachTriggeredFrom.Detach)
                safer.discardLastErrorState()

            }
            should("have been detached the by the observer") {
                (faultyObserver isObserving safer).shouldBeFalse()
            }
        }
    }
})





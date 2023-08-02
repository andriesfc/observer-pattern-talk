package foundation.observable

import foundation.observable.DetachManagementStrategy.Handled
import foundation.testing.debug
import io.kotest.assertions.asClue
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


private typealias AnyObserver = Observer<Observable<Observable<*>>>

class AbstractObservableTest : ShouldSpec({

     context("An observer which throws an exception when it gets notified with detached.") {

        val iAmBadError = "iAmBadOne"

        val faultyObserver: AnyObserver = mockk("faultyObserver", false, DetachObservation::class) {
            this as DetachObservation
            every { subjectChanged(any()) } just runs
            every { detached() } throws Exception(iAmBadError)
        }

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


                fun detachCallbackFailed(c: Handled.DetachTriggeredFrom, o: Observable<*>, s: Handled.PostDetachState) {
                    lastErrorContext = c.debug("context")
                    lastErrorState = s.debug("state")
                    o.debug("observable")
                }

                var lastErrorState: Handled.PostDetachState? = null; private set
                var lastErrorContext: Handled.DetachTriggeredFrom? = null; private set

                fun discardLastErrorState() {
                    lastErrorState = null
                    lastErrorContext = null
                }

                override val detachManagementStrategy: DetachManagementStrategy = Handled { a, b, c ->
                    detachCallbackFailed(a, b, c)
                }
            }

            should("be attached") {
                (faultyObserver isObserving safer).shouldBeTrue()
            }

            should("does not fail with an exception when it tries to detach the observer") {
                shouldNotThrowAnyUnit { safer.detach(faultyObserver) }
                safer.lastErrorState.debug("lastErrorState")
                    .shouldBeInstanceOf<Handled.PostDetachState.Panic>()
                    .cause.shouldHaveMessage(iAmBadError)
                safer.lastErrorContext.debug("lastErrorContext").shouldBe(Handled.DetachTriggeredFrom.Detach)
                safer.discardLastErrorState()

            }
            should("have been detached the by the observer") {
                (faultyObserver isObserving safer).shouldBeFalse()
            }
        }
    }

    // todo
    xcontext("An observer throws an exception on change notification") {

        val faultyObserver = mockk<AnyObserver> {
            every { subjectChanged(any()) } throws Exception("Kaboom")
        }

        should("Deal with faulty observer according the default behaviour") {
            "Default behaviours is to raise the exception".asClue {
            }
        }
        should("Deal with faulty observer ignoring it") {
            "Expecting no exception be thrown".asClue {
            }
        }

        should("Deal with faulty observer by logging it to standard out") {}
        should("Deal with faulty observer by logging it to standard err") {}
        should("Deal with faulty observer by collecting it.") {}
    }
})






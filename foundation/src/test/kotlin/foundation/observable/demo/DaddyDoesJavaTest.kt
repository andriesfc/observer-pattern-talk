package foundation.observable.demo

import foundation.observable.Observer
import foundation.observable.oddsAndEnds.DaddyDoesJava
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DaddyDoesJavaTest : ShouldSpec({

    val observable = DaddyDoesJava(DaddyDoesJava.DoingNow.RESTING)

    afterContainer {
        "Expected DJ to not being observed (e.g. privacy concerns)".asClue {
            observable.isObserved().shouldBeFalse()
        }
    }

    context("Daddy Java ready") {

        val me: Observer<DaddyDoesJava> = mockk("LookAtDaddyJava") {
            every { subjectChanged(any<DaddyDoesJava>()) } answers {
                println(firstArg<DaddyDoesJava>().doingNow().message())
            }
        }

        should("Allow me to observe him without throwing a tantrum") {
            shouldNotThrowAnyUnit { observable.attach(me) }
            observable.isObservedBy(me).shouldBeTrue()
        }

        should("Ticking daddy Java off") {
            val tickles = 8
            repeat(tickles) { observable.doing() }
            verify(atLeast = tickles, atMost = tickles) { me.subjectChanged(any()) }
        }

        xshould("Me saying goodbye") {
            shouldNotThrowAnyUnit { observable.detach(me) }
            observable.isObservedBy(me).shouldBeFalse()
        }
    }

})
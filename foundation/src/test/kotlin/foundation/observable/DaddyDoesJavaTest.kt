package foundation.observable

import foundation.observable.doodads.DaddyDoesJava
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DaddyDoesJavaTest : ShouldSpec({

    val daddyJava = DaddyDoesJava(DaddyDoesJava.DoingNow.RESTING)

    context("Daddy Java ready") {

        val me: Observer<DaddyDoesJava> = mockk("LookAtDaddyJava") {
            every { subjectChanged(any<DaddyDoesJava>()) } answers {
                println(firstArg<DaddyDoesJava>().doingNow().message())
            }
        }

        should("Allow me to observe him without throwing a tantrum") {
            shouldNotThrowAnyUnit { daddyJava.attach(me) }
            daddyJava.isObservedBy(me).shouldBeTrue()
        }

        should("Ticking daddy Java off") {
            val tickles = 8
            repeat(tickles) { daddyJava.doing() }
            verify(atLeast = tickles, atMost = tickles) { me.subjectChanged(any())  }
        }

        should("Me saying goodbye") {
            shouldNotThrowAnyUnit { daddyJava.detach(me) }
            daddyJava.isObservedBy(me).shouldBeFalse()
        }
    }

})
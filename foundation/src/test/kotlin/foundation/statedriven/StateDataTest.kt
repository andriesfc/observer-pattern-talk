package foundation.statedriven

import foundation.testing.debug
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.util.*
import java.util.concurrent.TimeUnit

class StateDataTest : BehaviorSpec({

    Given("Empty state dats") {
        val state = StateData<String>()
        Then("Initial state should always be empty") { state.shouldBeEmpty() }
        Then("Accessing latest should fail with no such element") {
            shouldThrow<NoSuchElementException> { state.latest }
        }
        When("When setting initial state") {
            state["Initial"] = 61u
            println(state)
            Then("state data should contain exactly one element") {
                state.size.shouldBeExactly(1)
            }
            Then("latest should match state of \"Initial\" with data value of 61 (unsigned)") {
                state.latest.data.shouldBe(61u)
                state.latest.state.shouldBe("Initial")
                state.latest.type.shouldBe(UInt::class.java)
            }
        }
        When("Setting 6 more state data elements") {
            lateinit var last: String
            lateinit var lastData: UUID
            (1..6).forEach { i ->
                last = "Next$i"
                lastData = UUID.randomUUID()
                state[last] = lastData
            }
            println(state)
            Then("State should have a recording of a total of 7 elements") {
                state.shouldHaveSize(7)
            }
            Then("State latest should have state and value of \"$last\" and \"$lastData\"") {
                state.latest.state shouldBe last
                state.latest.data shouldBe lastData
                state.latest.type shouldBe UUID::class.java
            }
        }
        When("Destructuring latest") {
            shouldNotThrowAnyUnit {
                val (s: String, value: UUID, type) = state.latest
                println("state = $s")
                println("value = $value")
                println("type = $type")
            }
        }
    }

    Given("State data with at least 5 updates") {
        lateinit var s: StateData<TimeUnit>
        beforeContainer {
            s = StateData()
            TimeUnit.entries.forEach { s[it] = { i: Long -> it.toDays(i) } }
            println(s)
            s.shouldHaveAtLeastSize(5)

        }
        When("Clearing all elements") {
            s.clear()
            Then("state should be empty") { s.shouldBeEmpty() }
        }
        When("Discarding last update") {
            val oneLess = s.size - 1
            val last: StateData.Entry<TimeUnit> = s.discard().shouldNotBeNull()
            Then("state should have one less value") {
                s.shouldHaveSize(oneLess)
            }
            Then("last removed should not be in state") {
                s.shouldNotContain(last)
            }
        }
    }

    Given("A state recording") {

        val seq = listOf("one", "two", "three", "four", "five")
        val state = StateData<Int>()

        Then("State should have record 5 values") {
            seq.indices.forEach { i -> state[i] = seq[i] }
            state.latest.debug("latest:")
            state.latest.should { latest ->
                "latest.state".asClue { latest.state shouldBe seq.size - 1 }
                "latest.data".asClue { latest.data shouldBe seq.last() }
                "latest.type".asClue { latest.type shouldBe String::class.java }
            }
        }
        And("State should have record all changes in seq") {
           "state.size".asClue { state.size shouldBe seq.size }
        }
    }

})

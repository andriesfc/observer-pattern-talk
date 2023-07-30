package techare.simplevms.demo.serialization

import com.charleskorn.kaml.Yaml
import foundation.utils.q
import io.kotest.assertions.asClue
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldNotBeEmpty
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import techare.simplevms.demo.debug.debug
import techare.simplevms.demo.model.TickUnitModel
import java.util.concurrent.TimeUnit

class TickUnitModelSerializerTest : FunSpec({
    context("Test serialization of ${TickUnitModel::class.simpleName}") {

        val testData: List<T> = buildList {
            add(
                T(
                    TimeUnit.MILLISECONDS, 64,
                    json = q("64 millisec"),
                    yaml = q("64 millisec")
                ),
            )
            add(
                T(
                    TimeUnit.NANOSECONDS, value = 82726712,
                    json = q("82726712 nanosec"),
                    yaml = q("82726712 nanosec"),
                )
            )
        }
        context("Encoding of ${TickUnitModel::class.simpleName}") {
            withData(nameFn = T::named, testData) {
                assertSoftly(it) {
                    "encoding to json:".asClue { Json.encodeToString(model).debug("json") shouldBe json }
                    "encoding to yaml:".asClue { Yaml.default.encodeToString(model).debug("yaml") shouldBe yaml }
                }
            }
            context("Edge cases and badly formatted json/yaml") {
                test("Value contains additional spaces between values as well as before and after") {
                    val badlyFormed = q("    78     nanosec    ")
                    "Additional spaces not handled:".asClue {
                        val m = Json.decodeFromString<TickUnitModel>(badlyFormed)
                        assertSoftly(m) {
                            unit shouldBe TimeUnit.NANOSECONDS
                            value shouldBe 78u
                        }
                    }
                }
                test("Some poor John Dear added a ${q("+")} in front of the duration") {
                    val badlyFormedByJD = q("+82 days")
                    val expectedModel = TickUnitModel(82u, TimeUnit.DAYS).debug()
                    "Unnecessary ${q("+")} in front of duration:".asClue {
                        val actual = Json.decodeFromString<TickUnitModel>(badlyFormedByJD)
                        actual shouldBe expectedModel
                    }
                }
            }
            context("Encoding failure cases") {
                data class F(val failureCase: String, val invalid: String) {
                    fun testName() = failureCase
                }

                withData(
                    nameFn = F::testName,
                    F("Illegal value for time unit", q("46 jollies")),
                    F("Negative value for value", q("-47 days"))
                ) { f ->
                    val x = shouldThrow<IllegalArgumentException> { Json.decodeFromString<TickUnitModel>(f.invalid) }.debug()
                    x.message.shouldNotBeNull()
                    x.message.shouldNotBeEmpty()
                }
            }
        }
        context("Decoding of ${TickUnitModel::class.simpleName}") {
            withData(nameFn = T::named, testData) { t ->
                assertSoftly(t) {
                    "valid json: $json".asClue {
                        val decodedFromJson = Json.decodeFromString<TickUnitModel>(json)
                        decodedFromJson shouldBe model
                    }
                    "valid yaml: $yaml".asClue {
                        val decodedFromYaml = Yaml.default.decodeFromString<TickUnitModel>(yaml)
                        decodedFromYaml shouldBe model
                    }
                }
            }
        }
    }
})

private data class T(
    val model: TickUnitModel,
    val json: String,
    val yaml: String
) {
    constructor(timeUnit: TimeUnit, value: Int, json: String, yaml: String) : this(
        TickUnitModel(
            unit = timeUnit,
            value = value.toUInt()
        ),
        yaml = yaml,
        json = json
    )

    fun named(): String = "T(time=${model.value}, unitOfTime=${model.unit.name})"
    override fun toString(): String {
        return "T(\ntimeUnit=${model.unit}, value=${model.value},\n json=$json,\n yaml=$yaml\n)"
    }
}

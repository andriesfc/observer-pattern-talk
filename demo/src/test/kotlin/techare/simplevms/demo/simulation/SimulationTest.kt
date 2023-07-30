package techare.simplevms.demo.simulation

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldStartWith
import techare.simplevms.simulation.Simulation
import techare.simplevms.simulation.Simulation.Companion.sampleUniqueContent

class SimulationTest : FunSpec({

    context("Generation of various ids") {
        context("Simulation ID") {
            val id = Simulation.nextSimulationId().also { println("simulation_id = $it") }
            test("Total id length should be exactly ${Simulation.SIMULATION_ID_LENGTH}") {
                id.length shouldBeExactly Simulation.SIMULATION_ID_LENGTH
            }
            test("ID should start with ${Simulation.SIMULATION_ID_PREFIX}") {
                id.shouldStartWith(Simulation.SIMULATION_ID_PREFIX)
            }
        }
        context("Sampling of guaranteed unique strings") {

            val brandName = "Telmec Sensor Labs"
            val partSerialPrefix = "TELMS:"

            val nextId: () -> String = generateSequence(1) { it + 1 }
                .map { seq -> "%04d".format(seq) }.iterator()
                .let { iterator -> { iterator.next() } }
            data class D(
                val id: String = nextId(),
                val source: String,
                val fillerSampleSize: UInt = 7u,
                val fillerAlphabet: Simulation.IdAlphabet = Simulation.IdAlphabet.Numeric,
                val sourceSampleSize: UInt = source.length.toUInt().let { sourceLen ->
                    when {
                        // take the whole source if the source len is smaller than filler len
                        sourceLen <= fillerSampleSize -> sourceLen
                        // Ensure we're taking 75 of source chars%
                        else -> (3u * sourceLen) / 4u
                    }
                },
                val accept: AcceptChars = AcceptChars.LetterOrDigits
            ) {
                val totalSampleSize = (fillerSampleSize + sourceSampleSize).toInt()
            }

            val seen = mutableSetOf<String>()


            // Ensure that output for each test case has a big line
            // printed across the output
            beforeTest { repeat(120) { print('⎯') }; println(); println(it.name.testName.uppercase()); println() }

            withData(
                nameFn = D::toString,
                D(source = brandName),
                D(source = brandName),
                D(source = brandName),
                D(source = partSerialPrefix, accept = AcceptChars.UriLike),
                D(source = brandName),
                D(source = brandName),
                D(source = partSerialPrefix, accept = AcceptChars.UriLike),
            ) { d ->
                println(d.accept.description)
                val uniqueString = shouldNotThrowAny {
                    d.source.uppercase().sampleUniqueContent(
                        d.sourceSampleSize,
                        d.fillerSampleSize,
                        d.fillerAlphabet,
                        d.accept.criteria
                    ).also(::println)
                }
                assertSoftly(uniqueString) {

                    withClue("Expected total length of sample") {
                        length shouldBeExactly d.totalSampleSize
                    }

                    withClue("$uniqueString has already been seen") {
                        seen shouldNotContain uniqueString
                        seen += this
                    }
                }
            }
        }
    }


}) {
    private enum class AcceptChars(val description: String, val criteria: (Char) -> Boolean) {
        LetterOrDigits(
            """
                Source characters used for sampling will only contain letters and digits. Note these 
                letters may be upper of lower case.
            """.trimIndent(),
            Char::isLetterOrDigit
        ),
        UriLike(
            """
                Source characters will only contain letters or digits. In addition to these
                the following characters are also allowed: 
                    - Semicolon (:)
                    - Underscore (_)
                    - Dash (-)
        """.trimIndent(),
            { c: Char -> c.isLetterOrDigit() || c in ":_-" }
        ),
        ;
    }
}


package techare.simplevms.simulation

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import foundation.observable.AbstractObservable
import foundation.observable.DetachObservation
import foundation.observable.Observer
import foundation.utils.toCharArray
import foundation.utils.yn
import techare.simplevms.control.CabinControl
import techare.simplevms.control.MovementControl
import techare.simplevms.control.assembly.CabinLightAssembly
import techare.simplevms.control.assembly.MovementAssembly
import techare.simplevms.sensor.kind.CabinLightSensor
import techare.simplevms.sensor.kind.MovementSensor
import techare.simplevms.sensor.kind.Sensor
import techare.simplevms.sensor.SensorDescription
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.asJavaRandom

class Simulation(
    cabinLight: CabinLightAssembly,
    movement: MovementAssembly,
    private val simulationId: String,
    autoActivating: Boolean,
) : CabinControl by cabinLight,
    MovementControl by movement,
    AbstractObservable<Simulation>() {
    constructor(simulationId: String) : this(
        simulationId = simulationId,
        cabinLight = CabinLightAssembly(
            location = simulateLocation<CabinLightSensor>(simulationId),
            description = simulateDescription<CabinLightSensor>(simulationId),
            serialNo = simulateSerialNo<CabinLightSensor>(simulationId)
        ),
        movement = MovementAssembly(
            SensorDescription(
                description = simulateDescription<MovementSensor>(simulationId),
                location = simulateLocation<MovementSensor>(simulationId),
                serialNo = simulateSerialNo<MovementSensor>(simulationId)
            )
        ),
        autoActivating = false
    )

    constructor() : this(nextSimulationId())

    var activated: Boolean = false; private set
    var activationDate: LocalDateTime? = null; private set
    var deactivationDate: LocalDateTime? = null; private set

    private val observer = object : Observer<Sensor>, DetachObservation {
        init {
            if (autoActivating) attach()
        }

        override fun detached() {
            activated = false
            deactivationDate = LocalDateTime.now()
            this@Simulation.notifyObservers()
        }

        override fun subjectChanged(subject: Sensor) {
            this@Simulation.notifyObservers()
        }

        fun attach() {
            movement.attach(this)
            cabinLight.attach(this)
        }

        fun detach() {
            movement.manageDetachment(this)
            cabinLight.manageDetachment(this)
            activationDate = LocalDateTime.now()
            deactivationDate = null
        }
    }

    fun activate() {
        if (activated) {
            observer.attach()
        }
    }

    fun deactivate() {
        if (activated) {
            observer.detach()
        }
    }

    override fun toString(): String =
        "Simulation(simulationId='$simulationId', activated=${activated.yn}, activationDate=$activationDate, deactivationDate=$deactivationDate)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Simulation

        if (simulationId != other.simulationId) return false
        if (activated != other.activated) return false
        if (activationDate != other.activationDate) return false
        if (deactivationDate != other.deactivationDate) return false
        if (observer != other.observer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = simulationId.hashCode()
        result = 31 * result + activated.hashCode()
        result = 31 * result + (activationDate?.hashCode() ?: 0)
        result = 31 * result + (deactivationDate?.hashCode() ?: 0)
        result = 31 * result + observer.hashCode()
        return result
    }


    internal enum class IdAlphabet(internal val allowed: CharArray) {
        Numeric(('0'..'9').toCharArray()),
        Alpha(('a'..'z').toCharArray()),
        AlphaNumeric(Numeric.allowed + Alpha.allowed),
    }

    companion object {

        private enum class LetterCase {
            Upper,
            Lower,
        }

        private val random = Random.asJavaRandom()
        private val AcceptAnySourceChar = { _: Char -> true }

        internal fun String.sampleUniqueContent(
            sourceSampleSize: UInt,
            fillerSampleSize: UInt,
            fillAlphabet: IdAlphabet,
            acceptSourceChar: (Char) -> Boolean = AcceptAnySourceChar,
        ): String {
            val sampleSize = (fillerSampleSize + sourceSampleSize).toInt()

            return buildString(sampleSize) {
                mutableSetOf<Char>().also { set ->
                    for (c in this@sampleUniqueContent) {
                        if (set.size == sourceSampleSize.toInt())
                            break
                        if (acceptSourceChar(c) && set.add(c))
                            append(c)
                    }
                }
                (sampleSize - length)
                    .takeIf { remaining -> remaining > 0 && fillerSampleSize != 0u }
                    ?.let { remaining -> nanoId(fillAlphabet, remaining) }
                    ?.also { nanoId -> append(nanoId) }
            }
        }

        internal const val SIMULATION_ID_LENGTH = 12
        internal const val SIMULATION_ID_PREFIX = "𝞂:"

        private fun nanoId(alphabet: IdAlphabet, length: Int): String = randomNanoId(random, alphabet.allowed, length)

        internal inline fun <reified S : Sensor> simulateLocation(simulationId: String): Sensor.Location {
            return Sensor.Location("$simulationId:LOC:${nanoId(IdAlphabet.Numeric, 5)}")
        }

        internal inline fun <reified S : Sensor> simulateDescription(simulationId: String): String {
            return "$simulationId:${S::class.java.simpleName}:${nanoId(IdAlphabet.AlphaNumeric, 15)}"
        }

        internal inline fun <reified S : Sensor> simulateSerialNo(simulationId: String): String {
            return "$simulationId:${
                S::class.java.simpleName.sampleUniqueContent(
                    sourceSampleSize = 3u,
                    fillerSampleSize = 3u,
                    fillAlphabet = IdAlphabet.Numeric
                )
            }"
        }

        internal fun nextSimulationId(): String {
            return buildString(SIMULATION_ID_LENGTH - 2) {
                append(SIMULATION_ID_PREFIX)
                append(nanoId(IdAlphabet.Numeric, SIMULATION_ID_LENGTH - SIMULATION_ID_PREFIX.length))
            }
        }
    }

}
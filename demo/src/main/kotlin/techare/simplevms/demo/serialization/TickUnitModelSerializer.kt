@file:Suppress("SpellCheckingInspection")

package techare.simplevms.demo.serialization

import foundation.utils.si
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import techare.simplevms.demo.model.TickUnitModel
import java.util.concurrent.TimeUnit

object TickUnitModelSerializer : KSerializer<TickUnitModel> {

    private val SERIAL_NAME: String = TickUnitModel::class.java.simpleName
    private const val TIME_UNIT_NANO_SECONDS = "nanosec"
    private const val TIME_UNIT_MICRO_SECONDS = "microsec"
    private const val TIME_UNIT_MILLI_SECONDS = "millisec"
    private const val TIME_UNIT_SECONDS = "secs"
    private const val TIME_UNIT_MINUTES = "min"
    private const val TIME_UNIT_HOURS = "hours"
    private const val TIME_UNIT_DAYS = "days"
    private val splitOnSpaces = Regex("\\s+")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(SERIAL_NAME, PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): TickUnitModel {

        val encoded = decoder.decodeString()

        val tokenized = encoded.splitToSequence(splitOnSpaces)
            .map(String::trim)
            .filterNot(String::isEmpty)
            .take(2)
            .toList()

        val (encodedValue, encodedUnit) = tokenized.takeUnless { it.size != 2 }
            ?: throw IllegalArgumentException(
                """
                Encoded data is malformed: [$encoded]. Please ensure it in the following format: "<value> <unit>"
                (for example "78 $TIME_UNIT_MICRO_SECONDS"). Note that: (1) The only valid values for units are: 
                ${validSerializedUnitNames}. (2) Only whole positive numbers are 
                allowed for the first value.
                """.si()
            )
        return TickUnitModel(
            value = encodedValue.toUIntOrNull()
                ?: throw IllegalArgumentException(
                    """
                    Encoded value is not valid: [$encodedValue]. Please ensure that encoded value is whole positive
                    number.
                    """.si()
                ),
            unit = encodedUnit.timeUnit()
        )
    }

    override fun serialize(encoder: Encoder, value: TickUnitModel) {
        encoder.encodeString("${value.value} ${value.unit.serializedName()}")
    }

    private fun TimeUnit.serializedName(): String {
        return when (this) {
            TimeUnit.NANOSECONDS -> TIME_UNIT_NANO_SECONDS
            TimeUnit.MICROSECONDS -> TIME_UNIT_MICRO_SECONDS
            TimeUnit.MILLISECONDS -> TIME_UNIT_MILLI_SECONDS
            TimeUnit.SECONDS -> TIME_UNIT_SECONDS
            TimeUnit.MINUTES -> TIME_UNIT_MINUTES
            TimeUnit.HOURS -> TIME_UNIT_HOURS
            TimeUnit.DAYS -> TIME_UNIT_DAYS
        }
    }

    private val validSerializedUnitNames: String
        get() = TimeUnit.entries.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ", "
        ) { it.serializedName() }

    private fun String?.timeUnit(): TimeUnit {
        return when (this?.lowercase()) {
            TIME_UNIT_DAYS -> TimeUnit.DAYS
            TIME_UNIT_HOURS -> TimeUnit.HOURS
            TIME_UNIT_MINUTES -> TimeUnit.MINUTES
            TIME_UNIT_SECONDS -> TimeUnit.SECONDS
            TIME_UNIT_MICRO_SECONDS -> TimeUnit.MICROSECONDS
            TIME_UNIT_MILLI_SECONDS -> TimeUnit.MILLISECONDS
            TIME_UNIT_NANO_SECONDS -> TimeUnit.NANOSECONDS
            else -> throw IllegalArgumentException(
                """
                Value of [${this@timeUnit}] is not a valid value for a unit of time. The following values are 
                valid: $validSerializedUnitNames
                """.si()
            )
        }
    }
}
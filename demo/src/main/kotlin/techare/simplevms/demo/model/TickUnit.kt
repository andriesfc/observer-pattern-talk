package techare.simplevms.demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import techare.simplevms.demo.serialization.TickUnitModelSerializer
import java.util.concurrent.TimeUnit

@Serializable(with = TickUnitModelSerializer::class)
data class TickUnit(
    @SerialName("duration") val duration: UInt,
    @SerialName("unit") val unit: TimeUnit,
)
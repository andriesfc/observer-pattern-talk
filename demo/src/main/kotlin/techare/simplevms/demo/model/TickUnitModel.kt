package techare.simplevms.demo.model

import kotlinx.serialization.Serializable
import techare.simplevms.demo.serialization.TickUnitModelSerializer
import java.util.concurrent.TimeUnit

@Serializable(with = TickUnitModelSerializer::class)
data class TickUnitModel(
    val value: UInt,
    val unit: TimeUnit,
)
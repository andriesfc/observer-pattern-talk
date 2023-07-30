package techare.simplevms.demo.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class ActionModel(
    @SerialName("repeat") @EncodeDefault(EncodeDefault.Mode.ALWAYS) val repeat: UInt = 0u,
    @SerialName("unit-of-tick") val unitOfTick: TickUnitModel,
    @SerialName("when-done") val whenDone: List<String>,
    @SerialName("sequence") val sequence: List<String>,
)
package techare.simplevms.demo.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class StateRecording(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val enabled: Boolean = true,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val location: String? = null
)

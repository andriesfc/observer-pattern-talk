package techare.simplevms.demo.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class SimulationModel(
    @SerialName("simulation-id") val simulationId: String,
    @SerialName("auto-activating") val autoActivating: Boolean,
    @SerialName("cabin-light") val cabinLight: ControlAssemblyModel,
    @SerialName("movement") val movement: ControlAssemblyModel,
    @SerialName("run") val run: Map<String, ActionModel>,
    @SerialName("state-recording") val stateRecording: StateRecordingModel?
)


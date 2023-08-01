package techare.simplevms.demo.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class Simulation(
    @SerialName("simulation_id") val simulationId: String,
    @SerialName("auto_activate") val autoActivating: Boolean,
    @SerialName("controlling") val controlling: Controls,
    @SerialName("actions") val actions: Map<String, Action>,
    @SerialName("state_recording") val stateRecording: StateRecording?
) {
    @Serializable
    data class Controls(
        @SerialName("cabin_light") val cabinLight: ControlAssembly,
        @SerialName("movement") val movement: ControlAssembly
    )
}


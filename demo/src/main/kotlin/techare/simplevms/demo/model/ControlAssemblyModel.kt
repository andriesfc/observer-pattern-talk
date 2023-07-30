package techare.simplevms.demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ControlAssemblyModel(
    @SerialName("assembly") val assembly: String,
    @SerialName("location") val location: String,
    @SerialName("description") val description: String,
    @SerialName("serial-no") val serialNo: String,
)
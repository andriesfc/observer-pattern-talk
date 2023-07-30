package techare.simplevms.sensor

import techare.simplevms.sensor.kind.Sensor

data class SensorDescription(
    override val location: Sensor.Location,
    override val serialNo: String,
    override val description: String
): Sensor
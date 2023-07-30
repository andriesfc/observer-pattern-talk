package techare.simplevms.sensor.kind

interface CabinLightSensor : Sensor {

    val state: State
    enum class State {
        ON,
        ON_DIM,
        OFF
    }

}
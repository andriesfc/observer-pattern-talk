package techare.simplevms.sensor.kind

interface MovementSensor : Sensor {

    val movement: Movement

    val speed: UInt get() = when(val m = movement) {
        is Movement.InMotion -> m.speed
        Movement.Stationary -> 0u
    }
    sealed class Movement : Comparable<Movement> {

        data object Stationary : Movement() {
            override fun compareTo(other: Movement): Int {
                return when {
                    other === this -> 0
                    else -> when (other) {
                        is InMotion -> 0u.compareTo(other.speed)
                        Stationary -> 0
                    }
                }
            }
        }

        data class InMotion(val speed: UInt) : Movement() {
            override fun compareTo(other: Movement): Int {
                return when {
                    other === this -> 0
                    else -> when (other) {
                        is InMotion -> speed.compareTo(other.speed)
                        Stationary -> speed.compareTo(0u)
                    }
                }
            }
        }

    }

}
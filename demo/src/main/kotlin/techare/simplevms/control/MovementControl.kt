package techare.simplevms.control

interface MovementControl {
    fun start(initialSpeed: UInt)
    fun updateSpeedBy(delta: Int)
    fun stop()
}
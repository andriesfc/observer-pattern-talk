package techare.simplevms.control.assembly

import foundation.observable.AbstractObservable
import foundation.pedantic.expecting
import techare.simplevms.control.MovementControl
import techare.simplevms.sensor.kind.MovementSensor
import techare.simplevms.sensor.kind.MovementSensor.Movement
import techare.simplevms.sensor.kind.Sensor
import techare.simplevms.sensor.SensorDescription

class MovementAssembly(description: SensorDescription) : Sensor by description , MovementSensor, AbstractObservable<MovementAssembly>(),
    MovementControl {
    override var movement: Movement = Movement.Stationary; private set
    override fun start(initialSpeed: UInt) {
        movement.expecting(Movement.Stationary) { "Already moving at $movement" }
        movement = Movement.InMotion(initialSpeed)
        notifyObservers()
    }

    override fun updateSpeedBy(delta: Int) {
        expecting(movement is Movement.InMotion) {"Cannot change speed if not moving"}
        val finalSpeed = speed.toInt() + delta
        movement = when {
            finalSpeed <= 0 -> Movement.Stationary
            else -> Movement.InMotion(finalSpeed.toUInt())
        }
        notifyObservers()
    }

    override fun stop() {
        if (movement is Movement.InMotion) {
            movement = Movement.Stationary
            notifyObservers()
        }
    }
}
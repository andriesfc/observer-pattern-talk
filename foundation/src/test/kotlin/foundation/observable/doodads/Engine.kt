package foundation.observable.doodads

import foundation.observable.AbstractObservable
import foundation.pedantic.expecting

class Engine : AbstractObservable<Engine>() {

    var running = false; private set
    var speed = 0; private set
    fun inc(speed: Int): Engine = apply {
        running.expecting(true) { "Engine must be running to increase the speed." }
        require(speed >= 0) { "speed can only increase in positive values or not at all" }
        if (speed > 0) {
            this.speed += speed
            notifyObservers()
        }
    }

    fun dec(speed: Int): Engine = apply {
        running.expecting(true) { "Engine must  be running to decrease th speed." }
        require(speed >= 0) { "speed can only decrease in positive values or not at all" }
        val update = (this.speed - speed).takeIf { it >= 0 } ?: 0
        this.speed = update
        if (this.speed == 0) {
            running = false
        }
        notifyObservers()
    }

    fun start(initialSpeed:UInt) {
        if (!running) {
            running = true
            speed = initialSpeed.toInt()
            notifyObservers()
        }
    }

    fun stop() {
        if (!running) {
            running = false
            speed = 0
            notifyObservers()
        }
    }

    override fun toString(): String {
        return "Engine(running=$running, speed=$speed)"
    }

}
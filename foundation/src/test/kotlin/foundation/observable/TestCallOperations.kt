package foundation.observable

import foundation.observable.doodads.Engine
import foundation.observable.doodads.Light
import io.kotest.core.spec.style.FunSpec

class TestCallOperations : FunSpec({

    val engine = Engine()
    val greenLight = Light("green")
    val redLight = Light("red")

    val engineObserver = Observer<Engine> { e ->
        var startSpeed: Int = -1
        if (e.running && startSpeed == -1) {
            startSpeed = e.speed
            greenLight.toggle()
        }

        if (!e.running) {
            greenLight.switchOff()
            redLight.switchOff()
            startSpeed = -1
        }

        if (e.speed >= 25) {
            redLight.switchOn()
        } else {
            redLight.switchOff()
        }
    }

    val logState = Observer<Any?> { println(it) }

    beforeContainer {
        engine.attach(engineObserver)
        engine.attach(logState)
        greenLight.attach(logState)
        redLight.attach(logState)
    }

    afterContainer {
        engine.detach(engineObserver)
        engine.detach(logState)
        greenLight.detach(logState)
        redLight.detach(logState)
    }

    context("Simple tests") {

        test("test1") {
            redLight.toggle()
            redLight.toggle()
        }

        test("test 2") {
            engine.start(10u)
            engine.inc(15)
            engine.inc(15)
            engine.dec(15*5)
            engine.stop()
        }
    }

})



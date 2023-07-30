package techare.simplevms.demo.driver

import foundation.observable.AbstractObservable
import foundation.pedantic.expectStateOfOrFail
import foundation.utils.si
import techare.simplevms.demo.model.SimulationModel
import java.time.Duration
import java.time.LocalDateTime

class Driver() : AbstractObservable<Driver>(), Runnable {
    sealed class State {
        data object UnConfigured : State()
        data class Ready(val model: SimulationModel) : State()
        data class Running(val model: SimulationModel, val timestamp: LocalDateTime) : State()
        data class Aborted(
            val model: SimulationModel,
            val timestamp: LocalDateTime,
            val duration: Duration,
            val cause: Throwable,
        ) : State()

        data class Stopped(
            val model: SimulationModel,
            val timestamp: LocalDateTime,
            val duration: Duration
        ) : State()
    }

    var state: State = State.UnConfigured; private set
    var runCounter: UInt = 0u; private set

    fun configure(model: SimulationModel) {

        state.expectStateOfOrFail<State, State.Running> {
            throw IllegalStateException(
                """
                Cannot configure running of simulation of ${model.simulationId} 
                while ${it.model.simulationId} is still running.
                """.si()
            )
        }

        if (state.model() == model)
            return



    }


    override fun run() {
        TODO("Not yet implemented")
    }

}

private fun Driver.State.model(): SimulationModel? {
    return when (this) {
        is Driver.State.Aborted -> model
        is Driver.State.Ready -> model
        is Driver.State.Running -> model
        is Driver.State.Stopped -> model
        Driver.State.UnConfigured -> null
    }
}